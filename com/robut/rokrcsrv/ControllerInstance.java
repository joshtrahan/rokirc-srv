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
import java.net.Socket;

public class ControllerInstance implements Runnable {
    private Socket sock;
    private BufferedReader sockIn;
    private DataOutputStream sockOut;

    private IRCManager ircManager;

    public ControllerInstance(Socket sock, IRCManager ircManager) throws IOException{
        this.sock = sock;
        this.sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.sockOut = new DataOutputStream(sock.getOutputStream());

        this.ircManager = ircManager;
    }

    public void run(){
        while(this.sock.isConnected()){
            String msg = "";
            try {
                msg = sockIn.readLine();
            }
            catch (IOException e){
                System.err.printf("Error receiving command: %s%n");
                continue;
            }

            if (msg == null || msg.isEmpty()){
                continue;
            }
            String[] tokens = msg.split(" ", 2);
            String cmd = tokens[0];
            String args = tokens[1];

            switch (cmd.toLowerCase()){
                case "joinserver":
                    joinServer(args);
                    break;

                case "leaveserver":
                    leaveServer(args);
                    break;

                case "joinchannel":
                    joinChannel(args);
                    break;

                case "leavechannel":
                    leaveChannel(args);
                    break;

                case "togglechat":
                    toggleChat(args);
                    break;

                case "genmessage":
                    genMessage(args);
                    break;

                default:
                    writeMessageToController("Invalid argument. Valid arguments are: ");
                    writeMessageToController("JoinServer, LeaveServer, JoinChannel, LeaveChannel, ToggleChat, GenMessage");

                    break;
            }
        }
    }

    private void joinServer(String args){
        String[] argTokens = args.split(" ");
        if (argTokens.length == 4){
            String server = argTokens[0];
            int port = Integer.parseInt(argTokens[1]);
            String nick = argTokens[2];
            String auth = argTokens[3];

            if (auth == null || auth == ""){
                    writeMessageToController("Something went wrong. Server not joined.");
            }
            else {
                ircManager.connectToIrcServer(server, port, nick, auth);
            }
        }
        else{
            writeMessageToController("Error: Incorrect number of arguments.");
            writeMessageToController("Usage: JoinServer server port nick auth");
        }
    }

    private void leaveServer(String args){
        String[] argTokens = args.split(" ");
        if (argTokens.length == 1){
            ircManager.leaveIrcServer(argTokens[0]);
        }
        else{
            writeMessageToController("Error: Incorrect number of arguments.");
            writeMessageToController("Usage: LeaveServer server");
        }
    }

    private void joinChannel(String args){
        String[] argTokens = args.split(" ");
        if (argTokens.length == 2){
            String server = argTokens[0];
            String channel = argTokens[1];

            try {
                ircManager.joinChannel(server, channel);
            }
            catch (IOException e){
                System.err.printf("Error joining channel: %s%n", e);
                writeMessageToController("Error joining channel: " + e);
            }
            catch (IRCManagerException e){
                System.err.printf("Error joining channel: %s%n", e);
                writeMessageToController("Server not yet joined.");
            }
        }
        else{
            writeMessageToController("Error: Incorrect number of arguments.");
            writeMessageToController("Usage: JoinChannel server channel");
        }
    }

    private void leaveChannel(String args){
        writeMessageToController("Sorry, this isn't implemented yet.");

/*
        String[] argTokens = args.split(" ");
        if (argTokens.length == 2){
            String server = argTokens[0];
            String channel = argTokens[1];

            try {
                ircManager.leaveChannel(server, channel);
            }
            catch (IOException e){
                System.err.printf("Error leaving channel: %s%n", e);
                try {
                    writeMessageToController("Error leaving channel: " + e);
                }
                catch (IOException e2){
                    System.err.printf("Error sending error to client: %s%n", e2);
                }
            }
        }
        else{
            try{
                writeMessageToController("Error: Incorrect number of arguments.");
                writeMessageToController("Usage: LeaveChannel server channel");
            }
            catch(IOException e){
                System.err.printf("Error sending error to client: %s%n", e);
            }
        }
        */
    }

    private void toggleChat(String args){
        writeMessageToController("Sorry, this isn't implemented yet.");
    }

    private void genMessage(String args){
        String[] argTokens = args.split(" ");

        if (argTokens.length != 2){
            writeMessageToController("Error: Incorrect number of arguments.");
            writeMessageToController("Usage: GenMessage server channel");

            return;
        }

        String server = argTokens[0];
        String channel = argTokens[1];

        String markovMsg = "";

        try{
            markovMsg = ircManager.generateMarkovString(server, channel);
        }
        catch (IRCManagerException e){
            System.out.printf("Error generating markov chain: %s%n");
            writeMessageToController("Error generating markov message: " + e);

        }

        if (!markovMsg.equals("")) {
            writeMessageToController(markovMsg);
        }
    }

    private void writeMessageToController(String msg) {
        try {
            this.sockOut.write((msg + "\r\n").getBytes("UTF-8"));
        }
        catch (IOException e){
            System.err.printf("Error writing message %s: %s%n", msg, e);
        }
    }
}
