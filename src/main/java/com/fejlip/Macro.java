package com.fejlip;

import com.fejlip.config.Config;
import com.fejlip.config.commands.MacroCommand;
import com.fejlip.features.AutoBuy;
import com.fejlip.features.AutoOpen;
import com.fejlip.helpers.PacketListener;
import com.fejlip.helpers.Queue;
import com.fejlip.helpers.QueueItem;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.lang3.time.StopWatch;


// Created by Fejlip#3070
// Do whatever you want with it :)
@Mod(modid = "macro", version = "1.0.0")
public class Macro {
    private Config config;
    private static Macro instance;
    private final Queue queue = new Queue();

    private final StopWatch stopWatch = new StopWatch();

    private final Thread thread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.config.isAutoOpenEnabled()) {
                if (!this.queue.isEmpty()) {
                    if (!this.queue.isRunning()) {
                        this.queue.setRunning(true);
                        QueueItem item = this.queue.get();
                        item.openAuction();
                    }
                }
            }
        }
    });

    public static Macro getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.config = new Config();
        instance = this;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Macro loaded!");
        ClientCommandHandler.instance.registerCommand(new MacroCommand());
        MinecraftForge.EVENT_BUS.register(new AutoBuy());
        MinecraftForge.EVENT_BUS.register(new PacketListener());
        new AutoOpen();
    }

    public Config getConfig() {
        return this.config;
    }

    public Queue getQueue() {
        return this.queue;
    }

    public Thread getThread() {
        return this.thread;
    }

    public StopWatch getStopWatch() {
        return this.stopWatch;
    }
}
