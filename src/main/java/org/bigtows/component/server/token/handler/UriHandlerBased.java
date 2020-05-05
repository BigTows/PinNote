package org.bigtows.component.server.token.handler;

import io.netty.handler.codec.http.HttpRequest;

public abstract class UriHandlerBased{

    public abstract void process(HttpRequest request, StringBuilder buff);

    public String getContentType() {
        return "text/plain; charset=UTF-8";
    }
}