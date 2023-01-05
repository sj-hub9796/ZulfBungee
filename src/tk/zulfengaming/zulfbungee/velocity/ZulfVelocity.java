package tk.zulfengaming.zulfbungee.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;
import tk.zulfengaming.zulfbungee.velocity.command.VelocityCommand;
import tk.zulfengaming.zulfbungee.velocity.command.VelocityConsole;
import tk.zulfengaming.zulfbungee.velocity.config.VelocityConfig;
import tk.zulfengaming.zulfbungee.velocity.event.VelocityEvents;
import tk.zulfengaming.zulfbungee.velocity.objects.VelocityPlayer;
import tk.zulfengaming.zulfbungee.velocity.task.VelocityTaskManager;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Plugin(id = "zulfbungee", name = "ZulfBungee", version = "0.8.0", url = "https://github.com/Zulfen/ZulfBungee",
description = "A Skript addon which adds proxy integration.", authors = {"zulfen"})
public class ZulfVelocity implements ZulfBungeeProxy<ProxyServer> {

    private final ProxyServer velocity;
    private final VelocityConfig pluginConfig;
    private MainServer<ProxyServer> mainServer;

    private final Logger logger;

    private final Path pluginFolderPath;

    private final boolean isDebug;

    private final CheckUpdateTask<ProxyServer> updater;

    private final VelocityTaskManager taskManager;

    private final LegacyComponentSerializer legacyTextSerialiser = LegacyComponentSerializer.builder()
            .character('&').
            hexCharacter('#')
            .hexColors()
            .build();

    @Inject
    public ZulfVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {

        this.velocity = server;
        this.logger = logger;
        this.pluginFolderPath = dataDirectory;
        this.pluginConfig = new VelocityConfig(this);

        this.taskManager = new VelocityTaskManager(this);

        this.isDebug = pluginConfig.getBoolean("debug");
        this.updater = new CheckUpdateTask<>(this);

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        try {

            this.mainServer = new MainServer<>(pluginConfig.getInt("port"),
                    InetAddress.getByName(pluginConfig.getString("host")), this);

            taskManager.newTask(mainServer);

        } catch (UnknownHostException e) {
            error("Could not start the server! (velocity)");
            throw new RuntimeException(e);
        }


        velocity.getEventManager().register(this, new VelocityEvents(mainServer));
        velocity.getCommandManager().register("zulfbungee", new VelocityCommand(new CommandHandlerManager<>(mainServer)));

    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        try {
            mainServer.end();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logDebug(String messageIn) {
        if (isDebug) logInfo(messageIn);
    }

    @Override
    public void logInfo(String messageIn) {
        logger.info(messageIn);
    }

    @Override
    public void error(String messageIn) {
        logger.error(messageIn);
    }

    @Override
    public void warning(String messageIn) {
        logger.warn(messageIn);
    }

    @Override
    public MainServer<ProxyServer> getServer() {
        return mainServer;
    }

    @Override
    public ProxyTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public ProxyConfig<ProxyServer> getConfig() {
        return pluginConfig;
    }

    @Override
    public ZulfProxyPlayer<ProxyServer> getPlayer(UUID uuidIn) {
        Optional<Player> player = velocity.getPlayer(uuidIn);
        return player.map(value -> new VelocityPlayer(value, this)).orElse(null);
    }

    @Override
    public ZulfProxyPlayer<ProxyServer> getPlayer(String nameIn) {
        Optional<Player> player = velocity.getPlayer(nameIn);
        return player.map(value -> new VelocityPlayer(value, this)).orElse(null);
    }

    @Override
    public Collection<ZulfProxyPlayer<ProxyServer>> getPlayers() {
        return velocity.getAllPlayers().stream()
                .map(player -> new VelocityPlayer(player, this))
                .collect(Collectors.toList());
    }

    @Override
    public ZulfProxyServer<ProxyServer> getServer(String name) {
        return null;
    }

    @Override
    public Map<String, ZulfServerInfo<ProxyServer>> getServersCopy() {

        HashMap<String, ZulfServerInfo<ProxyServer>> serversMap = new HashMap<>();

        for (RegisteredServer server : velocity.getAllServers()) {

            ServerInfo serverInfo = server.getServerInfo();

            List<ZulfProxyPlayer<ProxyServer>> proxyPlayers = server.getPlayersConnected().stream()
                    .map(player -> new VelocityPlayer(player, this))
                    .collect(Collectors.toList());

            ZulfServerInfo<ProxyServer> zulfInfo = new ZulfServerInfo<>(serverInfo.getAddress(), proxyPlayers);

            serversMap.put(serverInfo.getName(), zulfInfo);

        }

        return serversMap;

    }

    @Override
    public String getVersion() {
        return "0.8.0";
    }

    @Override
    public File getPluginFolder() {
        return pluginFolderPath.toFile();
    }

    @Override
    public ProxyCommandSender<ProxyServer> getConsole() {
        return new VelocityConsole(this);
    }

    @Override
    public String platformString() {
        return String.format("Velocity (%s)", velocity.getVersion().getVersion());
    }

    @Override
    public CheckUpdateTask<ProxyServer> getUpdater() {
        return updater;
    }

    public LegacyComponentSerializer getLegacyTextSerialiser() {
        return legacyTextSerialiser;
    }

    public ProxyServer getVelocity() {
        return velocity;
    }

}