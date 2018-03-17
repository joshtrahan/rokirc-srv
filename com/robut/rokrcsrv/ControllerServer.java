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

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ControllerServer {

    private InetAddress bindAddr;
    private int port;

    private String dbDir;

    private String logFile = "log.txt";

    private HashMap<String, IRCManager> ircManagers = new HashMap<>();
    private HashMap<String, ControllerInstance> controllerInstances = new HashMap<>();

    public ControllerServer(String bindAddress, int port, String serverDbDir) {
        try {
            this.bindAddr = InetAddress.getByName(bindAddress);
        } catch (UnknownHostException e) {
            System.err.printf("Error creating bind address: %s%n", e);
        }

        dbDir = serverDbDir;

        this.port = port;
    }

    public void startServer() throws IOException {
        ServerSocket listener = new ServerSocket(this.port, 50, this.bindAddr);
        while (true) {
            System.out.printf("Listening on port %d bound on %s%n", listener.getLocalPort(),
                    listener.getLocalSocketAddress());

            Socket controlSocket = listener.accept();
            System.out.printf("Connection made to client at address %s%n", controlSocket.getInetAddress());

            IRCManager clientManager;
            String clientAddress = controlSocket.getInetAddress().getHostAddress();
            if (controllerInstances.containsKey(clientAddress) &&
                    controllerInstances.get(clientAddress).hasClientConnection()){
                System.err.printf("Error: Client at %s attempted a second connection. Closing socket.%n",
                        clientAddress);
                controlSocket.close();
                logConnection(clientAddress, false);
                continue;
            }

            if (!ircManagers.containsKey(clientAddress)) {
                clientManager = new IRCManager(dbDir + File.separator + clientAddress);
                ircManagers.put(clientAddress, clientManager);
            } else {
                clientManager = ircManagers.get(clientAddress);
            }

            ControllerInstance controller;
            try {
                controller = new ControllerInstance(controlSocket, clientManager);
            } catch (IOException e) {
                System.err.printf("Error creating controller for client %s: %s%n", clientAddress, e);
                return;
            }

            logConnection(clientAddress, true);
            controllerInstances.put(clientAddress, controller);
            Thread controllerThread = new Thread(controller);
            controllerThread.setDaemon(false);
            controllerThread.start();
        }
    }

    private void logConnection(String ip, boolean successful) {
        String result = (successful) ? "accpeted" : "rejected";
        try {
            PrintWriter writer = new PrintWriter(logFile, "UTF-8");
            writer.printf("Connection initiated from %s: %s%n", ip, result);
        } catch (FileNotFoundException e) {
            System.err.printf("Error: Log file not found: %s%n", e);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.err.printf("Error: Unsupported encoding for log file: %s%n", e);
            e.printStackTrace();
        }

    }
}
