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

import com.robut.markov.MarkovChain;
import com.robut.rirc.PrivMsg;
import com.robut.rirc.PrivMsgHandler;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class MarkovPrivMsgHandler implements PrivMsgHandler {
    private File dbDirPath;
    private HashMap<String, MarkovChain> chains = new HashMap<>();

    public MarkovPrivMsgHandler(){
        this("");
    }

    public MarkovPrivMsgHandler(File dbDirPath){
        this.dbDirPath = dbDirPath;
    }

    public MarkovPrivMsgHandler(String dbDirPath){
        this(new File(dbDirPath));
    }

    public synchronized void addChannel(String channel){
        if (!chains.containsKey(channel)){
            chains.put(channel, new MarkovChain(dbDirPath + File.separator + channel + ".sqlite3"));
        }
    }

    public synchronized void handleNewMessage(PrivMsg msg){
        System.out.printf("Msg received.%n");
        if (!chains.containsKey(msg.getChannel())){
            addChannel(msg.getChannel());
        }

        chains.get(msg.getChannel()).parseString(msg.getMessage());
        chains.get(msg.getChannel()).saveToDisk();
    }

    public synchronized String generateMarkovMessage(String channel) throws IRCManagerException{
        if (chains.get(channel) != null) {
            return chains.get(channel).generateString();
        }
        else{
            throw new IRCManagerException("Error: Markov chains aren't ready for generation yet.");
        }
    }
}
