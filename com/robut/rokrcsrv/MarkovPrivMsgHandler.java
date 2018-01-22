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

    public MarkovPrivMsgHandler(File dbDirPath, Collection<String> channels){
        this.dbDirPath = dbDirPath;
        addChannels(channels);
    }

    public MarkovPrivMsgHandler(String dbDirPath, Collection<String> channels){
        this(new File(dbDirPath), channels);
    }

    public void addChannels(Collection<String> channels){
        for (String chan : channels){
            addChannel(chan);
        }
    }

    public void addChannel(String channel){
        if (!chains.containsKey(channel)){
            chains.put(channel, new MarkovChain(dbDirPath + File.separator + channel + ".sqlite3"));
        }
    }

    public void handleNewMessage(PrivMsg msg){
        System.out.printf("New message receieved: %s%n", msg);
        if (!chains.containsKey(msg.getChannel())){
            addChannel(msg.getChannel());
        }

        chains.get(msg.getChannel()).parseString(msg.getMessage());
        chains.get(msg.getChannel()).saveToDisk();
    }

    public String generateMarkovMessage(String channel) throws IRCManagerException{
        if (chains.get(channel) != null) {
            return chains.get(channel).generateString();
        }
        else{
            throw new IRCManagerException("Error: Markov chains aren't ready for generation yet.");
        }
    }
}
