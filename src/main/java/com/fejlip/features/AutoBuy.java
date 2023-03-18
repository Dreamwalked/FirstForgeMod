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
import net.minecraft.util.EnumChatFormatting;


public class AutoBuy {
    private int lastAuctionBought = 0;
    private boolean getName = true;
    private boolean checkItem = true;
    private boolean checkSold = true;
    private String playerName;
    private String apikey;


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) throws Exception {
        Config config = Macro.getInstance().getConfig();
        if (config != null && config.isAutoBuyEnabled() && (post.gui instanceof GuiChest)) {
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String name = chest.getLowerChestInventory().getName();
                if (name.contains("BIN Auction View")) {
                    ItemStack stack = chest.getSlot(31).getStack();
                    if (stack != null) {
                        String lore = EnumChatFormatting.getTextWithoutFormattingCodes(chest.getSlot(13).getStack().getTagCompound().getCompoundTag("display").toString());
                        if(getName) {
                            playerName = Helpers.getPlayer(lore);
                            if(playerName != null && !playerName.contains("Refreshing")){
                                Helpers.clickableMessage("§2Sellers AH: Auction sold by " + playerName, "/ah " + playerName);
                                getName = false;
                            }
                        }
                        if (Items.feather != stack.getItem()) {
                              if (Items.bed == stack.getItem()) {
                                  if (checkItem) {
                                      String itemName = EnumChatFormatting.getTextWithoutFormattingCodes(chest.getSlot(13).getStack().getDisplayName());
                                      Pattern pattern = Pattern.compile("Buy it now: ([\\d,]+)");
                                      Matcher matcher = pattern.matcher(lore);
                                      if (matcher.find()) {
                                          String price = matcher.group(1);
                                          price = price.replaceAll(",", "");
                                          Helpers.sendDebugMessage("§eAuction price is " + price);
                                          if (apikey != null) {
                                              Helpers.checkAuctions(Helpers.getuuid(playerName), itemName, Integer.parseInt(price), apikey, chest.windowId);
                                          }
                                          else{
                                              Helpers.sendDebugMessage("§cAPI key not set!");
                                          }
                                      } else {
                                          Helpers.sendDebugMessage("Price not found!");
                                      }
                                        checkItem = false;
                                  }
                              }
                                  else if (Items.potato == stack.getItem()) {
                                  if (lore.contains("Buyer: ") && checkSold){
                                      Pattern pattern;
                                      if(lore.contains("Buyer: [")){
                                          pattern = Pattern.compile("Buyer:\\s\\[\\w*\\+*\\]\\s*(\\w+)");
                                      }
                                      else{
                                          pattern = Pattern.compile("Buyer:\\s(\\w+)");
                                      }
                                      Matcher matcher = pattern.matcher(lore);
                                      if (matcher.find()) {
                                          String playerName1 = matcher.group(1);
                                          Helpers.sendDebugMessage("§eAuction bought by "+playerName1);
                                          checkSold = false;
                                      } else {
                                          Helpers.sendDebugMessage("Player name not found!");
                                      }
                                  }
                                    Macro.getInstance().getQueue().setRunning(false);
                                    Thread thread = new Thread(() -> {
                                        try {
                                            Thread.sleep(250);
                                            if(chest.getSlot(31).getStack().getItem() == Items.potato) {
                                                Minecraft.getMinecraft().thePlayer.closeScreen();
                                                Helpers.sendDebugMessage("§cAuction already bought! Closing GUI...");
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    thread.start();

                              }
                            }
                        }
                    }
                else if (name.contains("Confirm Purchase")) {
                    if (chest.windowId != this.lastAuctionBought) {
                        Helpers.sendClickPacket(chest.windowId, 11, 0);
                        this.lastAuctionBought = chest.windowId;
                    }
                }
                else{
                    getName = true;
                    checkItem = true;
                    checkSold = true;
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

