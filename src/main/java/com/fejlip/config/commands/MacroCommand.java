package com.fejlip.config.commands;

import com.fejlip.Macro;
import com.fejlip.config.Config;
import com.fejlip.helpers.Helpers;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class MacroCommand extends CommandBase {

    public MacroCommand() {
    }

    public String getCommandName() {
        return "fm";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/fm <setting> <value>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public void processCommand(ICommandSender sender, String[] args) {
        Config config = Macro.getInstance().getConfig();
        if (args.length == 0) {
            Helpers.sendChatMessage("No arguments!");
            return;
        }
        switch (args[0]) {
            case "Help":
            case "help":
                Helpers.sendChatMessage("Help:");
                Helpers.sendChatMessage("/fm help - Shows this message");
                Helpers.sendChatMessage("/fm debug - Toggles debug mode");
                Helpers.sendChatMessage("/fm autobuy - Toggles autobuy mode");
                Helpers.sendChatMessage("/fm autoopen - Toggles autoopen mode");
                Helpers.sendChatMessage("/fm bedclick <delay> - Sets the delay between bed clicks in ms");
                Helpers.sendChatMessage("/fm bedinitial <delay> - Sets the delay before the first bed click in ms");
                break;
            case "autoBuy":
            case "autobuy":
                boolean autoBuy = config.toggleAutoBuy();
                Helpers.sendChatMessage("Autobuy " + (autoBuy ? "on" : "off"));
                break;
            case "autoOpen":
            case "autoopen":
                boolean autoOpen = config.toggleAutoOpen();
                if (Macro.getInstance().getThread().isAlive()) {
                    Macro.getInstance().getQueue().clear();
                    Macro.getInstance().getQueue().setRunning(false);
                    Macro.getInstance().getThread().interrupt();
                } else {
                    Macro.getInstance().getThread().start();
                }
                Helpers.sendChatMessage("Autoopen " + (autoOpen ? "on" : "off"));
                break;
            case "bedClick":
            case "bedclick":
                if (args.length == 1) {
                    Helpers.sendChatMessage("Invalid arguments for command bed!");
                    return;
                }
                try {
                    int bedDelay = Integer.parseInt(args[1]);
                    config.setBedClickDelay(bedDelay);
                    Helpers.sendChatMessage("Bed click delay: " + bedDelay);
                } catch (NumberFormatException e) {
                    Helpers.sendChatMessage("Invalid bed click speed!");
                }
                break;
            case "bedInitial":
            case "bedinitial":
                if (args.length == 1) {
                    Helpers.sendChatMessage("Invalid arguments for command bed!");
                    return;
                }
                try {
                    int bedDelay = Integer.parseInt(args[1]);
                    config.setBedInitialDelay(bedDelay);
                    Helpers.sendChatMessage("Bed initial delay: " + bedDelay);
                } catch (NumberFormatException e) {
                    Helpers.sendChatMessage("Invalid bed initial delay!");
                }
                break;
            case "bedAmount":
            case "bedamount":
                if (args.length == 1) {
                    Helpers.sendChatMessage("Invalid arguments for command bed!");
                    return;
                }
                try {
                    int bedAmount = Integer.parseInt(args[1]);
                    config.setBedClickAmount(bedAmount);
                    Helpers.sendChatMessage("Bed click amount: " + bedAmount);
                } catch (NumberFormatException e) {
                    Helpers.sendChatMessage("Invalid bed click amount!");
                }
                break;
            case "Debug":
            case "debug":
                boolean debug = config.toggleDebug();
                Helpers.sendChatMessage("Debug " + (debug ? "on" : "off"));
                break;
            default:
                Helpers.sendChatMessage("Invalid arguments!");
                break;
        }
    }


}
