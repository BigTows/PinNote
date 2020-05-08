package org.bigtows.component.http;

public interface SimpleHttpServer {


    int getPort();

    void registerHandler(String path, RequestHandler requestHandler);


    void startAsync();

    void stopAsync();

}
