package com.badgames.box4dead;

public interface Constants {
    int SOCKET_TIMEOUT = 50;
    int PORT = 9999;
    int GAME_WIDTH = 1024;
    int GAME_HEIGHT = 728;

    int STILL = -1;
    int UP = 0;
    int DOWN = 1;
    int RIGHT = 2;
    int LEFT = 3;



    // action types
    String DELIMITER = "@@";
    String CONNECT = "CONNECT";
    String RECEIVE_ALL = "RECEIVE_ALL";
    String ADD_PLAYER = "ADD_PLAYER";
    String MOVE_PLAYER = "MOVE_PLAYER";
    String CHANGE_HP_PLAYER = "CHANGE_HP_PLAYER";
    String CHANGE_SCORE = "CHANGE_SCORE";
    String CHANGE_SPAWN_TIMER = "CHANGE_SPAWN_TIMER";
    String KILL_PLAYER = "KILL_PLAYER";
    String SPAWN_PLAYER = "SPAWN_PLAYER";

    String ADD_BULLET = "ADD_BULLET";
    String MOVE_BULLET = "MOVE_BULLET";
    String KILL_BULLET = "KILL_BULLET";

    String ADD_ZOMBIE = "ADD_ZOMBIE";
    String MOVE_ZOMBIE = "MOVE_ZOMBIE";
    String KILL_ZOMBIE = "KILL_ZOMBIE";
    String CHANGE_HP_ZOMBIE = "CHANGE_HP_ZOMBIE";


}
