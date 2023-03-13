package tk.zulfengaming.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

@Name("Send Proxy Player to Proxy Server")
@Description("Sends a proxy player to another given proxy server.")
public class EffPlayerChangeServer extends Effect {

    private Expression<ClientPlayer> players;
    private Expression<ClientServer> server;

    static {
        Skript.registerEffect(EffPlayerChangeServer.class, "[(proxy|bungeecord|bungee|velocity) [player]] (send|transfer)" +
                " %-proxyplayers% to [(proxy|bungeecord|bungee|velocity) [server[s]]] %-proxyserver%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        players = (Expression<ClientPlayer>) expressions[0];
        server = (Expression<ClientServer>) expressions[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        ZulfBungeeSpigot.getPlugin().getConnectionManager().sendDirect(new Packet(PacketTypes.PLAYER_SWITCH_SERVER,
                            true, true, new ClientPlayerDataContainer(server.getSingle(event), players.getArray(event))));
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect send proxy player " + players.toString(event, b) + " to server " + server.toString(event, b);
    }
}
