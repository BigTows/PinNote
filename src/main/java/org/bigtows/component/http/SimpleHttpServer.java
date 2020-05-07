package org.bigtows.component.http;

public interface SimpleHttpServer {


    void registerHandler(String path, RequestHandler requestHandler);


    void startAsync(int port);

}
