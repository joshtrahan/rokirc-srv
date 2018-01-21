package com.robut.rokrcsrv;

import com.robut.rirc.IRCClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ControllerConnection {
    Socket controlSock;
    DataOutputStream sockOut;
    BufferedReader sockIn;

    IRCClient ircClient;

    public ControllerConnection(IRCClient ircClient, Socket socket) throws IOException{
        this.ircClient = ircClient;
        this.controlSock = socket;

        this.sockOut = new DataOutputStream(socket.getOutputStream());
        this.sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}
