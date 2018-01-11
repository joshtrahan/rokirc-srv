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

import com.robut.rirc.IRCConnection;
import com.robut.rirc.PrivMsg;
import com.robut.markov.MarkovChain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Driver {
    public static void main(String[] args){
        if (args.length > 2){
            String server = args[0];
            int port = Integer.parseInt(args[1]);
            String username;
            String auth;
            String[] channels = Arrays.copyOfRange(args, 2, args.length);

            try (BufferedReader credFile = new BufferedReader(new FileReader("resources/creds.txt"))) {
                username = credFile.readLine();
                auth = credFile.readLine();
                credFile.close();
            } catch (Exception e) {
                System.err.printf("Exception reading credentials: %s%n", e);
                return;
            }

            File dbDir = new File("dbs");
            dbDir.mkdirs();

            listenToChannel(server, port, username, auth, channels, dbDir);
        }
        else{
            System.out.printf("Usage: java Driver server port channel%n");
        }
    }

    public static void listenToChannel(String server, int port, String username, String auth, String[] channels,
                                       File dbDir){
        HashMap<String, MarkovChain> chains = new HashMap<>();

        for (String chan : channels){
            try {
                File dbPath = new File(dbDir.getCanonicalPath() + "/" + chan + ".sqlite3");
                chains.put(chan, new MarkovChain(dbPath.getCanonicalPath()));
            }
            catch (IOException e){
                System.err.printf("Error resolving path %s: %s%n", dbDir, e);
                return;
            }
        }

        IRCConnection conn = new IRCConnection(server, port, username, auth, Arrays.asList(channels));
        try {
            conn.connect();
        }
        catch (IOException e){
            System.err.printf("Error connecting to server %s: %s%n", server, e);
        }

        while (true){
            PrivMsg newMsg;
            try{
                newMsg = conn.getMessage();
            }
            catch(Exception e) {
                System.err.printf("Error getting privmsg: %s%n", e);
                continue;
            }

            System.out.printf("%s%n", newMsg);

            chains.get(newMsg.getChannel()).parseString(newMsg.getMessage());
            chains.get(newMsg.getChannel()).saveToDisk();
        }
    }
}
