package org.bigtows.component.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SunHttpServer implements SimpleHttpServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpServer server;

    private boolean status = false;

    @SneakyThrows
    public SunHttpServer() {
        this.server = HttpServer.create(new InetSocketAddress(PortUtility.getFreePort()), 0);
    }

    @Override
    public int getPort() {
        return server.getAddress().getPort();
    }

    @Override
    public void registerHandler(String path, RequestHandler requestHandler) {
        server.createContext(path, new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
                requestHandler.handle(new HttpRequest() {
                    @Override
                    public String getMethod() {
                        return exchange.getRequestMethod();
                    }

                    @Override
                    public Map<String, List<String>> getHeaders() {
                        return exchange.getRequestHeaders();
                    }

                    @SneakyThrows
                    @Override
                    public void sendResponse(int httpCode, String message) {
                        exchange.sendResponseHeaders(httpCode, message.length());
                        OutputStream outputStream = exchange.getResponseBody();
                        outputStream.write(message.getBytes());
                        outputStream.close();
                    }

                    @Override
                    public Map<String, String> getParams() {
                        String[] params = exchange.getRequestURI().getQuery().split("&");
                        Map<String, String> map = new HashMap<>();
                        for (String param : params) {
                            String[] rawParams = param.split("=", 2);
                            map.put(rawParams[0], rawParams[1]);
                        }
                        return map;
                    }
                });
            }
        });
    }

    @SneakyThrows
    @Override
    public void startAsync() {
        if (!status) {
            logger.debug("Server start at {} port", server.getAddress().getPort());
            this.server.start();
            this.status = true;
        }
    }

    @Override
    public void stopAsync() {
        new Thread(() -> {
            try {
                Thread.sleep(250);
                server.stop(0);
                status = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
