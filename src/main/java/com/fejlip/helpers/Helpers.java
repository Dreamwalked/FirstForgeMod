package com.fejlip.helpers;

import com.fejlip.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.event.ClickEvent;
import java.util.regex.Pattern;

public final class Helpers {

    public static String prefix = "§l§f[§l§cqf§eKiller§f]§r§f ";
    public static void sendClickPacket(int windowId, int slotId, int clickedButton, int actionNumber ) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(
                windowId != 0 ?
                        windowId
                        : Minecraft.getMinecraft().thePlayer.openContainer.windowId,
                slotId,
                clickedButton,
                actionNumber,
                null,
                (short) 0
        ));
    }
    public static void sendClickPacket(int windowId, int slotId, int clickedButton) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(
                windowId != 0 ?
                        windowId
                        : Minecraft.getMinecraft().thePlayer.openContainer.windowId,
                slotId,
                clickedButton,
                0,
                null,
                (short) 0
        ));
    }



    public static void clickableMessage(String message, String command) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(prefix + message).setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))));
    }
    public static void sendDebugMessage(String message) {
        if (Macro.getInstance().getConfig().isDebug()){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(prefix + message));
        }
}
    public static void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(prefix + message));
    }
}
