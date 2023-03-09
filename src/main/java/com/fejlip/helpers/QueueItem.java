package com.fejlip.helpers;


import com.fejlip.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class QueueItem {
    private final String command;

    public QueueItem(String command) {
        this.command = command;
    }

    public void openAuction() {
        Helpers.sendDebugMessage("Executing: " + this.command);
        (Minecraft.getMinecraft()).thePlayer.sendChatMessage(this.command);
    }
}
