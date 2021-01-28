package tk.zulfengaming.bungeesk.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import tk.zulfengaming.bungeesk.bungeecord.config.YamlConfig;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.bungeecord.task.TaskManager;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class BungeeSkProxy extends Plugin {

    public Logger logger;

    public YamlConfig config;

    public Server server;

    public TaskManager taskManager;

    public boolean isDebug = false;

    public void onEnable() {

        logger = getProxy().getLogger();

        taskManager = new TaskManager(this);

        config = new YamlConfig(this);

        if (config.getBoolean("debug")) isDebug = true;

        try {
            server = new Server(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);

            taskManager.newTask(server, "MainServer");

        } catch (UnknownHostException | TaskAlreadyExists e) {
            error("There was an error trying to start the server?:");
            e.printStackTrace();

        }

    }

    public void log(String message) {
        if (isDebug) logger.info(message);
    }

    public void error(String message) {
        logger.severe(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }
}