package com.ankushrayabhari.game.server;

import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class ServerLauncher {
    public static void main(String args[]) {
        Server server = new Server();
        server.start();
        try {
            server.bind(54555, 54777);

        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}