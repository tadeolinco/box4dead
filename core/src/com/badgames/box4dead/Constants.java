package com.badgames.box4dead;

public interface Constants {
    int SOCKET_TIMEOUT = 50;
    int PORT = 9999;
    int GAME_WIDTH = 800;
    int GAME_HEIGHT = 600;

    int STILL = -1;
    int UP = 0;
    int DOWN = 1;
    int RIGHT = 2;
    int LEFT = 4;

    String DELIMITER = "@@";
    // action types
    String CONNECT = "CONNECT";
    String RECEIVE_ALL = "RECEIVE_ALL";
    String ADD_PLAYER = "ADD_PLAYER";
    String MOVE_PLAYER = "MOVE_PLAYER";

    String ADD_BULLET = "ADD_BULLET";
    String MOVE_BULLET = "MOVE_BULLET";
    String KILL_BULLET = "KILL_BULLET";
}
