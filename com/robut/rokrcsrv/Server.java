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

import com.robut.rirc.Client;
import com.robut.markov.MarkovChain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private Socket sock;
    private BufferedReader sockIn;
    private DataOutputStream sockOut;

    private Client conn;
    private ArrayList<MarkovChain> chains;
    private String dbDir;

    private String server;

    public Server(){

    }

    public Server(String dbDirectory){
        this.dbDir = dbDirectory;
    }

    public void listen(int port, String bindAddress) throws IOException {
        ServerSocket listener = new ServerSocket(port);
        this.sock = listener.accept();
        this.sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.sockOut = new DataOutputStream(sock.getOutputStream());
    }

    public void listen(int port) throws IOException {
        listen(port, "0.0.0.0");
    }

    private void receiveMessage() throws IOException {
        String msg = this.sockIn.readLine();


    }

    private void sendMessage(String msg) throws IOException {
        if (msg.contains("\r\n")){
            throw new IOException("Message contains newline characters.");
        }

        this.sockOut.write((msg + "\r\n").getBytes("UTF-8"));
    }

    private void loop() throws IOException{
        while(true){
            receiveMessage();
        }
    }

    private void handleJoin(String server, String channel){

    }
}
