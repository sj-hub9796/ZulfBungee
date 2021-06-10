package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.ChatColor;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ClientInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientListenerManager implements Runnable {

    private final ZulfBungeeSpigot pluginInstance;

    private final ClientConnection connection;

    private final SocketHandler socketHandler;

    private InetAddress serverAddress;

    private final int serverPort;

    private InetAddress clientAddress;

    private final int clientPort;

    private final TransferQueue<Socket> socketHandoff = new LinkedTransferQueue<>();
    private final Phaser socketBarrier = new Phaser();

    private volatile Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(false);

    private final LinkedList<ClientListener> listeners = new LinkedList<>();

    private ClientInfo clientInfo;

    public ClientListenerManager(ClientConnection connectionIn) {

        this.connection = connectionIn;
        this.pluginInstance = connectionIn.getPluginInstance();

        try {
            this.serverAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("server-address"));
            this.clientAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("client-address"));
        } catch (UnknownHostException e) {

            pluginInstance.error("Could not get the name of the host in the config!:");
            e.printStackTrace();

        }
        this.serverPort = pluginInstance.getYamlConfig().getInt("server-port");
        this.clientPort = pluginInstance.getYamlConfig().getInt("client-port");

        socketBarrier.register();

        this.socketHandler = new SocketHandler(this);

    }

    private Future<Optional<Socket>> connect() {

        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
    }


    public void shutdown() throws IOException {

        listeners.clear();

        socket.close();

    }

    public void addListener(ClientListener listener) {
        pluginInstance.logDebug("New listener added: " + listener.getClass().toString());
        listeners.addLast(listener);
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public AtomicBoolean isSocketConnected() {
        return socketConnected;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public BlockingQueue<Socket> getSocketHandoff() {
        return socketHandoff;
    }

    public Phaser getSocketBarrier() {
        return socketBarrier;
    }

    public LinkedList<ClientListener> getListeners() {
        return listeners;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    @Override
    public void run() {

        while (connection.isRunning().get()) {

            socketBarrier.arriveAndAwaitAdvance();

            if (socket != null && !socketConnected.get()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    pluginInstance.error("Error closing client socket:");
                    e.printStackTrace();
                }
            }

            while (!socketConnected.get()) {

                pluginInstance.warning("Not connected to the proxy! Trying to connect...");

                try {

                    Optional<Socket> futureSocket = connect().get(5, TimeUnit.SECONDS);

                    if (futureSocket.isPresent()) {

                        socket = futureSocket.get();

                        while (socketHandoff.hasWaitingConsumer()) {
                            socketHandoff.transfer(socket);
                        }

                        socketConnected.compareAndSet(false, true);

                        pluginInstance.logInfo(ChatColor.GREEN + "Connection established with proxy!");

                        clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers());

                        connection.send_direct(new Packet(PacketTypes.CLIENT_HANDSHAKE, true, true, clientInfo));

                    }

                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    pluginInstance.error("Error getting socket:");
                    e.printStackTrace();
                }
            }
        }
    }
}