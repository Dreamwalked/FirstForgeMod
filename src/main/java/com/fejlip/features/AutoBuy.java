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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AutoBuy {
    private int lastAuctionBought = 0;
    private final ScheduledExecutorService service;
    private int earlierWindowId = 0;
    private boolean soon = false;
    private boolean fastBed = false;

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
                        if (Items.feather != stack.getItem()) {
                            if (Items.potato == stack.getItem()) {
                                Helpers.sendDebugMessage("Potato auction found, skipping...");
                                soon = false;
                                fastBed = false;
                                Minecraft.getMinecraft().thePlayer.closeScreen();
                                Macro.getInstance().getQueue().setRunning(false);
                            } else if (Items.bed == stack.getItem()) {
                                ItemStack bedCountdown = chest.getSlot(13).getStack();
//                                if (!fastBed && !bedCountdown.getTagCompound().getCompoundTag("display").toString().contains("Can buy in: §eSoon!"))
//                                    fastBed = true;
//                                if (fastBed) {
                                if (bedCountdown.getTagCompound().getCompoundTag("display").toString().contains("Can buy in: §eSoon!") && !soon) {
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
                } else if (name.contains("Confirm Purchase")) {
                    if (chest.windowId != this.lastAuctionBought) {
                        clickConfirm(chest.windowId);
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
            soon = false;
            fastBed = false;
            Macro.getInstance().getStopWatch().stop();
            Helpers.sendDebugMessage("Bought auction in " + Macro.getInstance().getStopWatch().getNanoTime() / 1000000 + "ms");
            Macro.getInstance().getQueue().setRunning(false);
        }
        if (Macro.getInstance().getQueue().isRunning()) {
            boolean isDebug = Macro.getInstance().getConfig().isDebug();
            if (str.contains("This auction wasn't found") || str.contains("There was an error with the auction")) {
                soon = false;
                fastBed = false;
                Helpers.sendDebugMessage("Error or not found");
                Macro.getInstance().getQueue().setRunning(false);
            }
            if (str.contains("You don't have enough coins to afford this bid!")) {
                soon = false;
                fastBed = false;
                Helpers.sendDebugMessage("Not enough coins");
                Minecraft.getMinecraft().thePlayer.closeScreen();
                Macro.getInstance().getQueue().setRunning(false);
            }
        }
    }

    private void clickNugget(int id) {
        click(id, 31);
    }

    private void clickConfirm(int id) {
        click(id, 11);
    }

    private void click(int id, int index) {
        (Minecraft.getMinecraft()).playerController.windowClick(id, index, 0, 3, Minecraft.getMinecraft().thePlayer);
    }
}

