package org.bigtows.component.server.token.handler;

import io.netty.handler.codec.http.HttpRequest;
import org.bigtows.component.server.token.handler.event.EvernoteToken;

public class EvernoteTokenHandler extends UriHandlerBased {


    private final EvernoteToken event;

    public EvernoteTokenHandler(EvernoteToken evernoteToken) {
        this.event = evernoteToken;
    }

    @Override
    public void process(HttpRequest request, StringBuilder buff) {
        String token = request.headers().get("X-Evernote-Access-Token");
        String url = request.uri();
        if (token != null) {
            event.token(token);
            buff.append("OK");
        } else if (url.startsWith("/evernote?token=")) {
            event.token(url.replaceFirst("/evernote\\?token=", ""));
            buff.append("Success, goto IDE!");
        }else{
            buff.append("Incorrect request");
        }
    }
}