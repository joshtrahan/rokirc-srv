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
        if (!chains.containsKey(msg.getChannel())){
            chains.put(msg.getChannel(), new MarkovChain(dbDirPath + msg.getChannel()));
        }

        chains.get(msg.getChannel()).parseString(msg.getMessage());
    }

    public String genMessage(String channel){
        return chains.get(channel).generateString();
    }
}
