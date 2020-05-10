package org.bigtows.component.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * Port utility
 */
public class PortUtility {

    /**
     * Get any available port
     *
     * @return available port
     */
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

    /**
     * Get random int
     *
     * @param at number
     * @param to number
     * @return random int
     */
    @SuppressWarnings("SameParameterValue")
    private static int getRandomInt(int at, int to) {
        var random = new Random();
        return random.nextInt(to + 1 - at) + at;
    }
}
