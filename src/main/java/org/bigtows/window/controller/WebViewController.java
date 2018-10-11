/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.controller;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.bigtows.config.settings.PinNoteSettings;

/**
 * Web view controller for Evernote
 */
public class WebViewController {


    private final PinNoteSettings pinNoteSettings;
    @FXML
    public WebView webView;

    private CallBackWebViewController callBackWebViewController;

    public WebViewController(PinNoteSettings settings, CallBackWebViewController callBackWebViewController) {

        this.pinNoteSettings = settings;
        this.callBackWebViewController = callBackWebViewController;
    }

    public void initialize() {
        webView.getEngine().load(this.getOAuth2ServerUrl());

        webView.getEngine().getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        if (webView.getEngine().getLocation().equalsIgnoreCase(this.getOAuth2ServerUrl() + "index.php?action=accessToken")) {
                            try {
                                callBackWebViewController.onFnishPageOpened(
                                        webView.getEngine().getDocument().getElementsByTagName("body").item(0).getChildNodes().item(0).getNodeValue()
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    private String getOAuth2ServerUrl() {
        return
                pinNoteSettings.isDebugMode() ?
                        pinNoteSettings.getEvernoteSettings().getOAuth2Server() + "/debug/" :
                        pinNoteSettings.getEvernoteSettings().getOAuth2Server();
    }
}
