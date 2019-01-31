/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.controller;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import org.bigtows.config.settings.PinNoteSettings;

import java.io.IOException;

/**
 * Web view controller for Evernote
 */
public class WebViewController {

    /**
     * Settings of PinNote
     */
    private final PinNoteSettings pinNoteSettings;

    /**
     * The Web view.
     */
    @FXML
    public WebView webView;

    /**
     * Callback for web view
     */
    private CallBackWebViewController callBackWebViewController;

    /**
     * Instantiates a new Web view controller.
     *
     * @param settings                  the settings
     * @param callBackWebViewController the call back web view controller
     */
    public WebViewController(PinNoteSettings settings, CallBackWebViewController callBackWebViewController) {
        this.pinNoteSettings = settings;
        this.callBackWebViewController = callBackWebViewController;
    }

    /**
     * Initialize.
     */
    public void initialize() {
        webView.getEngine().getLoadWorker().stateProperty().addListener(this::statePropertyListener);
        webView.getEngine().load(this.getResetSessionServerUrl());
    }

    /**
     * Property state listener
     *
     * @param observable The {@code ObservableValue} which value changed
     * @param oldState   The old value
     * @param newState   The new value
     */
    private void statePropertyListener(ObservableValue<? extends Worker.State> observable,
                                       Worker.State oldState,
                                       Worker.State newState
    ) {
        if (newState == Worker.State.SUCCEEDED) {
            if (webView.getEngine().getLocation().equalsIgnoreCase(this.getAccessTokenServerUrl())) {
                try {
                    callBackWebViewController.onAuthCompleted(
                            webView.getEngine().getDocument()
                                    .getElementsByTagName("body")
                                    .item(0)
                                    .getChildNodes()
                                    .item(0).getNodeValue()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (webView.getEngine().getLocation().equalsIgnoreCase(this.getResetSessionServerUrl())) {
                webView.getEngine().load(this.getOAuth2ServerUrl());
            }
        }

    }

    /**
     * Get server url for access token
     *
     * @return Access token url
     */
    private String getAccessTokenServerUrl() {
        return this.getOAuth2ServerUrl() + "index.php?action=accessToken";
    }

    /**
     * Get reset url for drop session
     *
     * @return Reset url
     */
    private String getResetSessionServerUrl() {
        return this.getOAuth2ServerUrl() + "index.php?action=reset";
    }

    /**
     * Get OAuth2ServerUrl
     *
     * @return OAuth2 url
     */
    private String getOAuth2ServerUrl() {
        return
                pinNoteSettings.isDebugMode() ?
                        pinNoteSettings.getEvernoteSettings().getOAuth2Server() + "/debug/" :
                        pinNoteSettings.getEvernoteSettings().getOAuth2Server();
    }


    public static Scene getInstance(PinNoteSettings pinNoteSettings, CallBackWebViewController callback) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(WebViewController.class.getClassLoader()); // set the plugin's class loader
        loader.setLocation(WebViewController.class.getResource("/fxml/WebView.fxml"));
        loader.setControllerFactory((clazz) ->
                new WebViewController(
                        pinNoteSettings,
                        callback
                )
        );
        Parent root = loader.load();
        return new Scene(root);
    }
}
