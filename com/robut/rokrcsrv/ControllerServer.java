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
import com.robut.markov.MarkovChain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class ControllerServer {
    private String bindAddr;
    private int port;

    private IRCClient ircClient;
    private ArrayList<MarkovChain> chains;
    private String dbDir;

    private String server;

    public ControllerServer(){

    }

    public ControllerServer(String bindAddress, int port, String dbDirectory){
        this.dbDir = dbDirectory;
        this.bindAddr = bindAddress;
        this.port = port;
    }

    public ControllerServer(int port, String dbDirectory){
        this("0.0.0.0", port, dbDirectory);
    }

    public void listen() throws IOException {
        ServerSocket listener = new ServerSocket(this.port);
        System.out.printf("Listening on port %d binded on %s%n", this.port, this.bindAddr);

        Socket controlSocket = listener.accept();
        System.out.printf("Connection made to client at address %s%n", controlSocket.getInetAddress());


    }

    public void startIrcClient(String server, int port, String nick, String auth, Collection<String> channels){
        ircClient = new IRCClient(server, port, nick, auth, channels);
        ircClient.start();
    }
}
