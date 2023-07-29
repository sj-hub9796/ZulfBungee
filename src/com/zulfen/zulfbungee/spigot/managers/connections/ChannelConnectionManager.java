package com.zulfen.zulfbungee.spigot.managers.connections;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.ChannelConnection;
import com.zulfen.zulfbungee.spigot.socket.factory.ChannelConnectionFactory;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChannelConnectionManager extends ConnectionManager<ChannelConnectionFactory> {

    private ChannelConnection channelConnection;

    private final SocketAddress socketAddress;

    public ChannelConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress serverAddress, int serverPort) {
        super(pluginIn, ChannelConnectionFactory.class);
        this.socketAddress = new InetSocketAddress(serverAddress, serverPort);
        newChannelConnection();
    }

    @Override
    protected void sendDirectImpl(Packet packetIn) {
        channelConnection.sendDirect(packetIn);
    }

    @Override
    public Optional<Packet> send(Packet packetIn) {
        if (sendDirect(packetIn)) {
            return channelConnection.read();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ClientPlayer> getPlayers(ClientServer[] serversIn) {

        Optional<Packet> send = send(new Packet(PacketTypes.PROXY_PLAYERS,
                true, false, serversIn));

        if (send.isPresent()) {
            Packet packet = send.get();
            return Arrays.stream(packet.getDataArray())
                    .filter(ClientPlayer.class::isInstance)
                    .map(ClientPlayer.class::cast)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();

    }

    public void newChannelConnection() {

        if (channelConnection != null) {
            channelConnection.destroy();
        }

        channelConnection = createNewConnection()
                .withAddress(socketAddress)
                .compressLargePacketTo(5120)
                .build();

        channelConnection.start();


    }

    public void signalAvailableConnection() {
        channelConnection.getClientCommHandler().signalProperConnection();
    }

    @Override
    public void shutdown() {
        channelConnection.destroy();
        super.shutdown();
    }

}