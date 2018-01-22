package com.robut.rokrcsrv;

import java.io.*;

public class ControllerInstance {
    private BufferedReader sockIn;
    private DataOutputStream sockOut;

    private IRCManager ircManager;

    public ControllerInstance(IRCManager ircManager, DataInputStream sockIn, DataOutputStream sockOut){
        this.sockIn = new BufferedReader(new InputStreamReader(sockIn));
        this.sockOut = new DataOutputStream(sockOut);

        this.ircManager = ircManager;
    }
}
