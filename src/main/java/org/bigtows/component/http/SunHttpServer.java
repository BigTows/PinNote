package org.bigtows.component.http;

import com.intellij.openapi.project.Project;
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

    @SneakyThrows
    public SunHttpServer(Project project) {
        this.server = HttpServer.create();
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
    public void startAsync(int port) {
        logger.warn("Start server at {} port", port);
        server.bind(new InetSocketAddress(port), 0);
        server.start();
    }

    @Override
    public void stopAsync() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                server.stop(1);
                logger.info("Server shutdown");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
