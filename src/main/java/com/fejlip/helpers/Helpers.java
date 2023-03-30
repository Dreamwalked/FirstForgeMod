package com.fejlip.helpers;

import com.fejlip.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.event.ClickEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public final class Helpers {

    public static String prefix = "§l§f[§l§cqf§eKiller§f]§r§f ";

    public static String getPlayer(String lore){
        if (lore.contains("Seller: ")) {
            Pattern pattern;
            if (lore.contains("Seller: [")) {
                pattern = Pattern.compile("Seller:\\s\\[\\w*\\+*\\]\\s*(\\w+)");
            } else {
                pattern = Pattern.compile("Seller:\\s(\\w+)");
            }
            Matcher matcher = pattern.matcher(lore);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                Helpers.sendDebugMessage("Player name not found!");
            }
        }
        return null;
    }
    public static String getuuid(String name) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + name);
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject json = new JSONObject(result.toString());
        return json.getString("id");
    }

    public static void checkAuctions(String uuid, String itemName, int startingBid, String key, int windowId) throws Exception {
        Helpers.sendDebugMessage("checking uuid " + uuid + " for item " + itemName + " with starting bid " + startingBid);
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.hypixel.net/skyblock/auction?key=" + key + "&player=" + uuid);
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject json = new JSONObject(result.toString());
        JSONArray auctions = json.getJSONArray("auctions");
        for (int i = 0; i < auctions.length(); i++) {
            JSONObject auction = auctions.getJSONObject(i);
            int bid = auction.getInt("starting_bid");
            if (bid == startingBid) {
                long now = System.currentTimeMillis();
                long purchasable = auction.getLong("start")+20000;
                long remaining = purchasable-now;
                if(remaining >= 0) {
                    sendDebugMessage("Purchasable in " + remaining + "ms");
                    Thread thread = new Thread(() -> {
                        try {
                            long sleep = remaining-Macro.getInstance().getConfig().getBedInitialDelay();
                            if(sleep < 30000 && sleep > 0) {
                                Thread.sleep(remaining - Macro.getInstance().getConfig().getBedInitialDelay());
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        for (int j = 0; (j < 5); j++) {
                            if (((ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer).getLowerChestInventory().getName().equals("BIN Auction View")) {
                                sendDebugMessage("Bed clicked at " + (purchasable - System.currentTimeMillis()));
                                sendClickPacket(windowId, 31, 0);
                                sendClickPacket(windowId + 1, 11, 0);
                            }
                            try {
                                Thread.sleep(Macro.getInstance().getConfig().getBedClickDelay());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    thread.start();
                }
            }
        }
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
