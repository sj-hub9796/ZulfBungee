package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.net.SocketAddress;

public class ServerSwitchEvent extends PacketHandler {

    public ServerSwitchEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.SERVER_SWITCH_EVENT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyPlayer player = (ProxyPlayer) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerSwitchServer(player)
        );

        return null;

    }
}