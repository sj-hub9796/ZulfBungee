package com.zulfen.zulfbungee.universal.handlers.proxy.transport;

import com.zulfen.zulfbungee.universal.handlers.proxy.ProxyCommHandler;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.transport.SocketServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class ProxySocketCommHandler<P, T> extends ProxyCommHandler<P, T> {

    private final SocketServerConnection<P, T> connection;
    private final Socket socket;

    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public ProxySocketCommHandler(SocketServerConnection<P, T> connectionIn, Socket socketIn) throws IOException {
        super(connectionIn);
        this.connection = connectionIn;
        this.socket = socketIn;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public Optional<Packet> readPacketImpl() {

        try {
            Object readObject = inputStream.readObject();
            if (readObject instanceof Packet) {
                return Optional.of((Packet) readObject);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (pluginInstance.isDebug()) {
                e.printStackTrace();
            }
            destroy();
        }

        return Optional.empty();

    }

    @Override
    public void writePacketImpl(Packet toWrite) {
        try {
            outputStream.writeObject(toWrite);
            outputStream.flush();
        } catch (IOException e) {
            if (pluginInstance.isDebug()) {
                e.printStackTrace();
            }
            destroy();
        }
    }

    @Override
    protected void freeResources() {
        try {
            socket.close();
        } catch (IOException e) {
            if (pluginInstance.isDebug()) {
                pluginInstance.error("Error closing socket on connection " + connection.getAddress());
                e.printStackTrace();
            }
        }
    }

}
