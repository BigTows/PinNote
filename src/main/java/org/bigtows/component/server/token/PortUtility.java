package org.bigtows.component.server.token;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class PortUtility {

    public static int getFreePort() {
        while (true) {

            int randomPort = getRandomInt(500, 9999);
            try {
                ServerSocket socket = new ServerSocket(randomPort);
                socket.close();
                return randomPort;
            } catch (IOException ignored) {
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int getRandomInt(int at, int to) {
        var random = new Random();
        return random.nextInt(to + 1 - at) + at;
    }
}
