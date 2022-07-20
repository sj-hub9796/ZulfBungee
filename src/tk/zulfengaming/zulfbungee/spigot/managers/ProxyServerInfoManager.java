package tk.zulfengaming.zulfbungee.spigot.managers;

import tk.zulfengaming.zulfbungee.universal.socket.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.*;
import java.util.stream.Collectors;

public class ProxyServerInfoManager {

    private static final HashMap<String, ClientInfo> servers = new HashMap<>();

    public static ProxyServer toProxyServer(String nameIn) {
        return new ProxyServer(nameIn, servers.get(nameIn));
    }

    public static void setServers(ProxyServer[] serverList) {
        servers.clear();
        Arrays.stream(serverList).forEach(server -> servers.put(server.getName(), server.getServerInfo()));
    }

    public static boolean contains(String proxyServerNameIn) {
        return servers.containsKey(proxyServerNameIn);
    }

    public static List<ProxyServer> getServers() {
        return servers.keySet().stream().map(ProxyServer::new).collect(Collectors.toList());
    }

}