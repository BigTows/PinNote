package org.bigtows.component.http;

public interface SimpleHttpServer {

    /**
     * Get listening port
     *
     * @return port
     */
    int getPort();

    /**
     * Register handler for http server
     *
     * @param path           path of handler
     * @param requestHandler instance of handler
     */
    void registerHandler(String path, RequestHandler requestHandler);

    /**
     * Start server async
     */
    void startAsync();

    /**
     * Stop server async
     */
    void stopAsync();

}
