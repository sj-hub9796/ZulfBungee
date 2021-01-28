package tk.zulfengaming.bungeesk.spigot.socket;

import tk.zulfengaming.bungeesk.spigot.socket.packets.Handshake;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.ArrayList;


public class PacketHandlerManager {

    public ArrayList<PacketHandler> handlers = new ArrayList<>();

    public PacketHandlerManager(ClientConnection connectionIn) {
        handlers.add(new Handshake(connectionIn));

    }

    public PacketHandler getHandler(Packet packetIn) {
        for (PacketHandler packetHandler : handlers)
            for (PacketTypes type : packetHandler.getTypes()) if (type == packetIn.type) return packetHandler;
        return null;
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        return getHandler(packetIn).handlePacket(packetIn, address);
    }
}