package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.io.Serializable;

public class ScriptInfo implements Serializable {

    private final ScriptAction scriptAction;

    private final String scriptName;

    private final ClientPlayer sender;

    private final byte[] scriptData;

    public ScriptInfo(ScriptAction scriptActionIn, String scriptNameIn, ClientPlayer proxyPlayerIn, byte[] dataIn) {
        this.scriptAction = scriptActionIn;
        this.scriptName = scriptNameIn;
        this.sender = proxyPlayerIn;
        this.scriptData = dataIn;
    }

    public byte[] getScriptData() {
        return scriptData;
    }

    public ScriptAction getScriptAction() {
        return scriptAction;
    }

    public ClientPlayer getSender() {
        return sender;
    }

    public String getScriptName() {
        return scriptName;
    }
}