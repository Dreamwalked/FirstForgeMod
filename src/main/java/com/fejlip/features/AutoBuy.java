package com.fejlip.features;

import com.fejlip.Macro;
import com.fejlip.config.Config;
import com.fejlip.helpers.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.util.EnumChatFormatting;
public class AutoBuy {
    private int lastAuctionBought = 0;
    private final ScheduledExecutorService service;

    private String apikey;
    public AutoBuy() {
        this.service = Executors.newSingleThreadScheduledExecutor();
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) {
        Config config = Macro.getInstance().getConfig();
        if (config != null && config.isAutoBuyEnabled() && (post.gui instanceof GuiChest)) {
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String name = chest.getLowerChestInventory().getName();
                if (name.contains("BIN Auction View")) {
                    ItemStack stack = chest.getSlot(31).getStack();
                    if (stack != null) {
                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes(chest.getSlot(13).getStack().getTagCompound().getCompoundTag("display").toString());
                        if (lore.contains("Seller: ")){
                            Pattern pattern;
                            if(lore.contains("Seller: [")){
                                pattern = Pattern.compile("Seller:\\s\\[\\w*\\+*\\]\\s*(\\w+)");
                            }
                            else{
                                pattern = Pattern.compile("Seller:\\s(\\w+)");
                            }
                            Matcher matcher = pattern.matcher(lore);
                            if (matcher.find()) {
                                String playerName = matcher.group(1);
                                Helpers.clickableMessage("§2Sellers AH: Auction sold by "+playerName, "/ah "+playerName);
                            } else {
                                Helpers.sendDebugMessage("Player name not found!");
                            }
                        }
                        if (Items.feather != stack.getItem()) {
                              if (Items.bed == stack.getItem()) {
                                  //ItemStack bedCountdown = chest.getSlot(13).getStack();
//                                if (!fastBed && !bedCountdown.getTagCompound().getCompoundTag("display").toString().contains("Can buy in: §eSoon!"))
//                                    fastBed = true;
//                                if (fastBed) {

                              /*  if (bedCountdown.getTagCompound().getCompoundTag("display").toString().contains("Can buy in: §eSoon!") && !soon) {
                                    soon = true;
                                    Helpers.sendDebugMessage("Soon started!");
                                    Thread thread = new Thread(() -> {
                                        try {
                                            int clickAmount = config.getBedClickAmount();
                                            Thread.sleep(Macro.getInstance().getConfig().getBedInitialDelay());
                                            for (int j = 0; (j < clickAmount && soon); j++) {
                                                Helpers.sendDebugMessage("Bed clicked");
                                                Helpers.sendClickPacket(chest.windowId, 31, 0);
                                                Helpers.sendClickPacket(chest.windowId + 1, 11, 0);
                                                if (j == clickAmount) {
                                                    soon = false;
                                                }
                                                Thread.sleep(Macro.getInstance().getConfig().getBedClickDelay());
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    thread.start();
                                }*/
                              }
                                  else if (Items.potato == stack.getItem()) {
                                  if (lore.contains("Buyer: ")){
                                      Pattern pattern;
                                      if(lore.contains("Buyer: [")){
                                          pattern = Pattern.compile("Buyer:\\s\\[\\w*\\+*\\]\\s*(\\w+)");
                                      }
                                      else{
                                          pattern = Pattern.compile("Buyer:\\s(\\w+)");
                                      }
                                      Matcher matcher = pattern.matcher(lore);
                                      if (matcher.find()) {
                                          String playerName = matcher.group(1);
                                          Helpers.sendDebugMessage("§eAuction bought by "+playerName);
                                      } else {
                                          Helpers.sendDebugMessage("Player name not found!");
                                      }
                                  }
                                    Minecraft.getMinecraft().thePlayer.closeScreen();
                                    Macro.getInstance().getQueue().setRunning(false);
                                  }
//                                } else {
//                                    if (bedCountdown.getTagCompound().getCompoundTag("display").toString().contains("Can buy in: §eSoon!") && !soon)
//                                        soon = true;
//                                    int bedDelay = config.getBedClickDelay();
//                                    this.service.scheduleWithFixedDelay(() -> {
//                                        if (soon) {
//                                            Helpers.sendDebugMessage("Normal Bed clicked");
//                                            Helpers.sendClickPacket(chest.windowId, 31, 0);
//                                            Helpers.sendClickPacket(chest.windowId + 1, 11, 0);
//                                        }
//                                    }, 1L, 150, TimeUnit.MILLISECONDS);
//                                }
                            }
                        }
                    }
                else if (name.contains("Confirm Purchase")) {
                    if (chest.windowId != this.lastAuctionBought) {
                        Helpers.sendClickPacket(chest.windowId, 11, 0);
                        this.lastAuctionBought = chest.windowId;
                    }
                }
                }
            }
        }

    @SubscribeEvent
    public void onClientChatMessage(ClientChatReceivedEvent event) {
        String str = event.message.getUnformattedText();
        if (str.contains("Putting coins in")) {
            Macro.getInstance().getStopWatch().stop();
            Helpers.sendDebugMessage("Bought auction in " + Macro.getInstance().getStopWatch().getNanoTime() / 1000000 + "ms");
            Macro.getInstance().getQueue().setRunning(false);
        }
        else if(str.contains("Your new API key is ")){
            Pattern pattern = Pattern.compile("Your new API key is (.+)");
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                apikey = matcher.group(1);
                Helpers.sendDebugMessage("Your API key has been set to: "+apikey);
            } else {
                Helpers.sendDebugMessage("API key not found in the message!");
            }
        }
        if (Macro.getInstance().getQueue().isRunning()) {
            if (str.contains("This auction wasn't found") || str.contains("There was an error with the auction")) {
                Helpers.sendDebugMessage("Error or not found");
                Macro.getInstance().getQueue().setRunning(false);
            }
            if (str.contains("You don't have enough coins to afford this bid!")) {
                Helpers.sendDebugMessage("Not enough coins");
                Minecraft.getMinecraft().thePlayer.closeScreen();
                Macro.getInstance().getQueue().setRunning(false);
            }
        }
    }
}

