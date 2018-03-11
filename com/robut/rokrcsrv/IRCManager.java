/*
    rokirc-srv: A remote application that listens to an IRC server and generates Markov chains.
    Copyright (C) 2018  Joshua Trahan

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package com.robut.rokrcsrv;

import com.robut.rirc.IRCClient;

import java.io.File;
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
            File serverDbDir = new File(this.dbDir + File.separator + server);
            serverDbDir.mkdirs();

            MarkovPrivMsgHandler handler = new MarkovPrivMsgHandler(serverDbDir);
            serverMsgHandlers.put(server, handler);
            ircServers.put(server, new IRCClient(server, port, nick, auth, handler));
            ircServers.get(server).startThread();
            for (String channel : channels){
                serverMsgHandlers.get(server).addChannel(channel);
            }
        }
    }

    public void connectToIrcServer(String server, int port, String nick, String auth){
        connectToIrcServer(server, port, nick, auth, new ArrayList<>());
    }

    public void leaveIrcServer(String server){
        if (ircServers.containsKey(server)){
            try {
                ircServers.get(server).disconnect();
                ircServers.remove(server);
                serverMsgHandlers.remove(server);
            }
            catch (IOException e){
                System.err.printf("Error leaving server %s: %s%n", server, e);
            }
        }
    }

    public void joinChannel(String server, String channel) throws IOException, IRCManagerException {
        try {
            System.out.printf("Joining channel: %s - %s%n", server, channel);
            ircServers.get(server).joinChannel(channel);
        }
        catch (IOException e){
            System.err.printf("Error connecting to channel through rokrcsrv IRCManager: %s%n", e);
            throw e;
        }
        catch (NullPointerException e){
            System.err.printf("Error: Not connected to server %s to join channel %s%n", server, channel);
            throw new IRCManagerException("Server not connected.");
        }
        serverMsgHandlers.get(server).addChannel(channel);
    }

    public void leaveChannel(String server, String channel) throws IOException {

    }

    public String generateMarkovString(String server, String channel) throws IRCManagerException{
        return serverMsgHandlers.get(server).generateMarkovMessage(channel);
    }
}
