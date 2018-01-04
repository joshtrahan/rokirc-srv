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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RRCServer {
    private Socket sock;
    private BufferedReader sockIn;
    private DataOutputStream sockOut;



    public RRCServer(){

    }

    public void Listen(int port) throws IOException{
        ServerSocket listener = new ServerSocket(port);
        this.sock = listener.accept();
        this.sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.sockOut = new DataOutputStream(sock.getOutputStream());
    }
}
