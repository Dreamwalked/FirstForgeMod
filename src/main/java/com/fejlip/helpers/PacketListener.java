package com.fejlip.helpers;

import com.fejlip.Macro;
import com.fejlip.config.Config;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import com.fejlip.features.AutoBuy;

@ChannelHandler.Sharable
public class PacketListener extends SimpleChannelInboundHandler<Packet> {

    public PacketListener() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        try {
            Config config = Macro.getInstance().getConfig();
            if (packet instanceof S2DPacketOpenWindow) {
                if (config != null && config.isAutoBuyEnabled() || (Macro.getInstance().getConfig().isAutoOpenEnabled() && Macro.getInstance().getQueue().isRunning())) {

                    S2DPacketOpenWindow packetOpenWindow = (S2DPacketOpenWindow) packet;
                    if (packetOpenWindow.getWindowTitle().toString().contains("BIN Auction View")) {
                        Macro.getInstance().getStopWatch().reset();
                        Macro.getInstance().getStopWatch().start();
                        Helpers.sendClickPacket(packetOpenWindow.getWindowId(), 31, 0);
                        Helpers.sendClickPacket(packetOpenWindow.getWindowId() + 1, 11, 0);
                    }
                }
            }
            channelHandlerContext.fireChannelRead(packet);
        } catch (Exception e) {
            Helpers.sendDebugMessage("Error while trying to buy: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void connect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        ChannelPipeline pipeline = event.manager.channel().pipeline();
        pipeline.addBefore("packet_handler", this.getClass().getName(), this);
    }
}
