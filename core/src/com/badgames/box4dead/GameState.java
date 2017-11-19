package com.badgames.box4dead;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


// holds the hold state of the game
// players, zombies, bullets, etc
public class GameState {

    private Map players=new HashMap();

    public GameState() {}

    public void update(String name, NetPlayer player){
        players.put(name,player);
    }

    // check toString of NetPlayer
    public String toString(){
        String retval="";
        for(Iterator ite = players.keySet().iterator(); ite.hasNext();){
            String name=(String)ite.next();
            NetPlayer player=(NetPlayer)players.get(name);
            retval+=player.toString()+":";
        }
        return retval;
    }

    public Map getPlayers() {return players;}
}
