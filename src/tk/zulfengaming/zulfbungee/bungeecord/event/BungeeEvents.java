package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeeServer;
import tk.zulfengaming.zulfbungee.universal.event.ProxyEvents;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;

import java.util.Optional;

public class BungeeEvents extends ProxyEvents<ProxyServer> implements Listener  {

    public BungeeEvents(MainServer<ProxyServer> mainServerIn) {
        super(mainServerIn);
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            ServerInfo serverInfo = event.getServer().getInfo();
            Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(serverInfo.getName());

            getClientInfo.ifPresent(clientInfo -> serverConnected(new ClientServer(serverInfo.getName(), clientInfo), new BungeePlayer<>(eventPlayer,
                    new BungeeServer(serverInfo))));


        }
    }


    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            String toName = eventPlayer.getServer().getInfo().getName();

            Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(toName);

            getClientInfo.ifPresent(clientInfo -> switchServer(new ClientServer(toName, clientInfo), new BungeePlayer<>(eventPlayer)));

        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        if (event.getCause() == ServerKickEvent.Cause.SERVER) {

            ProxiedPlayer player = event.getPlayer();

            String serverName = event.getKickedFrom().getName();

            if (mainServer.getServerNames().contains(serverName)) {

                String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());
                serverKick(new ClientPlayer(player.getName(), player.getUniqueId()), legacyText);

            }

        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            String serverName = player.getServer().getInfo().getName();

            if (mainServer.getServerNames().contains(serverName)) {

                serverDisconnect(new ClientPlayer(player.getName(), player.getUniqueId()));

            }

        }

    }
}
