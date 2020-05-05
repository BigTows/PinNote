package org.bigtows.component.server.token;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class PortUtility {

    public static int getFreePort() {
        var random = new Random();

        while (true) {

            int randomPort = random.nextInt(9999 + 1 - 500) + 500;
            try {
                ServerSocket socket = new ServerSocket(randomPort);
                socket.close();
                return randomPort;
            } catch (IOException ignored) {
            }
        }
    }
}
