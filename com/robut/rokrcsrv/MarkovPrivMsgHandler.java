package com.robut.rokrcsrv;

import com.robut.markov.MarkovChain;
import com.robut.rirc.PrivMsg;
import com.robut.rirc.PrivMsgHandler;

import java.util.HashMap;

public class MarkovPrivMsgHandler implements PrivMsgHandler {
    private String dbDirPath;
    private HashMap<String, MarkovChain> chains = new HashMap<>();

    public MarkovPrivMsgHandler(){
        this("");
    }

    public MarkovPrivMsgHandler(String dbDirPath){
        this.dbDirPath = dbDirPath;
    }

    public void handleNewMessage(PrivMsg msg){
        System.out.printf("New message receieved: %s%n", msg);
        if (!chains.containsKey(msg.getChannel())){
            chains.put(msg.getChannel(), new MarkovChain(dbDirPath + msg.getChannel() + ".sqlite3"));
        }

        chains.get(msg.getChannel()).parseString(msg.getMessage());
        chains.get(msg.getChannel()).saveToDisk();
    }

    public String generateMarkovMessage(String channel){
        return chains.get(channel).generateString();
    }
}
