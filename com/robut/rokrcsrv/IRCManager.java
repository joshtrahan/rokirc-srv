package com.robut.rokrcsrv;

import com.robut.rirc.IRCClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class IRCManager {
    private String dbDir;
    private HashMap<String, IRCClient> ircServers = new HashMap<>();
    private HashMap<String, MarkovPrivMsgHandler> serverMsgHandlers = new HashMap<>();

    public IRCManager(String dbDir){
        this.dbDir = dbDir;
    }

    public IRCManager(){
        this("");
    }

    public void connectToIrcServer(String server, int port, String nick, String auth, Collection<String> channels){
        if (!ircServers.containsKey(server)){
            MarkovPrivMsgHandler handler = new MarkovPrivMsgHandler(this.dbDir);
            serverMsgHandlers.put(server, handler);
            ircServers.put(server, new IRCClient(server, port, nick, auth, channels, handler));
            ircServers.get(server).startThread();
        }
    }

    public void connectToIrcServer(String server, int port, String nick, String auth){
        connectToIrcServer(server, port, nick, auth, new ArrayList<>());
    }

    public void joinChannel(String server, String channel) throws IOException{
        try {
            ircServers.get(server).joinChannel(channel);
        }
        catch (IOException e){
            System.err.printf("Error connecting to channel through rokrcsrv IRCManager: %s%n", e);
            throw e;
        }
    }

    public String generateMarkovString(String server, String channel){
        return serverMsgHandlers.get(server).generateMarkovMessage(channel);
    }
}
