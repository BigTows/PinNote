package org.bigtows.component.http;

import java.io.IOException;

public interface SimpleHttpServer {


    void registerHandler(String path, RequestHandler requestHandler);


    void startAsync(int port) ;

    void stopAsync();

}
