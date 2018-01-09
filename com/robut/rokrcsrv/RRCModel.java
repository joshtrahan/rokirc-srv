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

import com.robut.rirc.IRCConnection;
import com.robut.rirc.PrivMsg;
import com.robut.markov.MarkovChain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RRCModel {
    private String dbDir;

    private IRCConnection connection;
    private ArrayList<MarkovChain> chains;

    private boolean sendAllMsgs = false;

    public RRCModel(){

    }

    public RRCModel(String dbDirectory){
        this.dbDir = dbDirectory;
        File dirPath = new File(dbDirectory);
        dirPath.mkdirs();
    }

    private void handleServerJoin(String hostname, int port, String username, String auth){
        this.connection = new IRCConnection(hostname, port, username, auth);
        try {
            this.connection.connect();
        }
        catch (IOException e){
            System.err.printf("Error creating connection to %s: %s%n", hostname, e);
        }
    }

    private void handleChannelJoin(String channel){
        if (this.connection.isConnected()){
            try {
                this.connection.joinChannel(channel);
            }
            catch (IOException e){
                System.err.printf("Error joining channel %s: %s%n", channel, e);
            }
        }
    }

    private void handleMessageSendingOption(String arg) throws RRCSrvException {

        if (arg.equalsIgnoreCase("true")){
            this.sendAllMsgs = true;
        }
        else if (arg.equalsIgnoreCase("false")){
            this.sendAllMsgs = false;
        }
        else{
            throw new RRCSrvException("Invalid option: Argument must be true or false.");
        }

    }
}
