package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptInfo;

import java.net.SocketAddress;

public class GlobalScript extends PacketHandler {

    public GlobalScript(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.GLOBAL_SCRIPT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ScriptInfo scriptInfo = (ScriptInfo) packetIn.getDataSingle();
        getConnection().processGlobalScript(scriptInfo);

    }
}