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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ControllerServer {
    private InetAddress bindAddr;
    private int port;

    private String dbDir;

    private IRCManager ircManager;

    public ControllerServer(int port, String dbDir, String bindAddress){
        try {
            this.bindAddr = InetAddress.getByName(bindAddress);
        }
        catch (UnknownHostException e){
            System.err.printf("Error creating bind address: %s%n", e);
        }

        this.port = port;
        this.ircManager = new IRCManager(dbDir);
    }

    public void startServer() throws IOException {
        ServerSocket listener = new ServerSocket(this.port, 50, this.bindAddr);
        System.out.printf("Listening on port %d bound on %s%n", this.port, this.bindAddr);

        Socket controlSocket = listener.accept();
        System.out.printf("Connection made to client at address %s%n", controlSocket.getInetAddress());
    }
}
