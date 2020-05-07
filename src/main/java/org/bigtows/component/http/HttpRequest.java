package org.bigtows.component.http;

import java.util.List;
import java.util.Map;

public interface HttpRequest {

    String getMethod();

    Map<String, List<String>> getHeaders();

    void sendResponse(int httpCode, String message);
}
