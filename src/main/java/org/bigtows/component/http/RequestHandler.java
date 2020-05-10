package org.bigtows.component.http;

/**
 * Interface of handler requests, used in Simple http server
 *
 * @see SimpleHttpServer
 */
public interface RequestHandler {

    /**
     * Handle request
     *
     * @param httpRequest information about request
     */
    void handle(HttpRequest httpRequest);
}
