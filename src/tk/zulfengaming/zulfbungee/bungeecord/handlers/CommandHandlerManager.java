package tk.zulfengaming.zulfbungee.bungeecord.handlers;

import tk.zulfengaming.zulfbungee.bungeecord.command.subcommands.ScriptDelete;
import tk.zulfengaming.zulfbungee.bungeecord.command.subcommands.ScriptReload;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


public class CommandHandlerManager {

    private final Server server;
    private final ArrayList<CommandHandler> handlers = new ArrayList<>();

    public CommandHandlerManager(Server serverIn) {
        this.server = serverIn;
        handlers.add(new ScriptReload(serverIn));
        handlers.add(new ScriptDelete(serverIn));
    }

    public Server getMainServer() {
        return server;
    }

    // TODO: Make it case insensitive.

    public Optional<CommandHandler> getHandler(String[] argsIn) {

        // args from base command I call labels instead
        for (CommandHandler handler : handlers) {

            String[] argCheck = argsIn;

            if (argsIn.length != handler.getLabels().length) {
                argCheck = Arrays.copyOfRange(argsIn, 0, handler.getLabels().length);
            }

            if (Arrays.equals(handler.getLabels(), argCheck)) {
                return Optional.of(handler);
            }

        }

        return Optional.empty();

    }
}