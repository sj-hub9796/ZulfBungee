package tk.zulfengaming.zulfbungee.spigot.tasks;

import ch.njol.skript.Skript;
import org.bukkit.command.CommandSender;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

public class GlobalScriptsTask implements Supplier<File> {

    private final ClientConnection connection;
    private final ZulfBungeeSpigot pluginInstance;

    private final byte[] data;

    private final String scriptName;
    private final ScriptAction scriptAction;
    private final CommandSender sender;

    public GlobalScriptsTask(ClientConnection connectionIn, String scriptNameIn, ScriptAction scriptActionIn, CommandSender senderIn, byte[] dataIn) {
        this.connection = connectionIn;
        this.pluginInstance = connection.getPluginInstance();
        this.scriptName = scriptNameIn;
        this.scriptAction = scriptActionIn;
        this.sender = senderIn;
        this.data = dataIn;
    }


    @Override
    public File get() {

        Thread.currentThread().setName("GlobalScriptsTask");

        File scriptFile = new File(Skript.getInstance().getDataFolder() + File.separator + "scripts",
                    scriptName);

        switch (scriptAction) {

            case NEW:
                newScript(scriptFile);
                break;
            case DELETE:
                removeScript(scriptFile);
                break;
            case RELOAD:
                removeScript(scriptFile);
                newScript(scriptFile);
                break;

        }

        skriptReload();

        return scriptFile;

    }

    private void newScript(File fileInstance) {

        try {

            boolean created = fileInstance.createNewFile();

            if (created) {

                Files.write(fileInstance.toPath(), data);

            }

        } catch (IOException e) {
            pluginInstance.error(String.format("There was an error trying to save script %s:", fileInstance.getName()));
            e.printStackTrace();
        }

    }

    private void removeScript(File fileInstance) {

        if (fileInstance.exists()) {
            if (!fileInstance.delete()) {
                pluginInstance.warning(String.format("Script file %s could not be deleted.", fileInstance.getName()));
            }
        }

    }

    private void skriptReload() {
        pluginInstance.getTaskManager().newPluginTask(Skript.getInstance(), () -> connection.getPluginInstance().getServer().dispatchCommand(sender, "sk reload " + scriptName));
    }

}



