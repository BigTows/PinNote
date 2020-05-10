package org.bigtows.component.http;

import java.util.List;
import java.util.Map;

/**
 * Interface of request, used in Simple http server
 *
 * @see SimpleHttpServer
 */
public interface HttpRequest {

    /**
     * Get method name.
     * GET, POST, etc...
     *
     * @return name of method
     */
    String getMethod();

    /**
     * Get headers of request
     *
     * @return map of request
     */
    Map<String, List<String>> getHeaders();

    /**
     * Send response data
     *
     * @param httpCode code of http response
     * @param message  message for client
     */
    void sendResponse(int httpCode, String message);

    /**
     * Get params from URL
     *
     * @return params from URL
     */
    Map<String, String> getParams();
}
