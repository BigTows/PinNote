/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.browser;

import com.google.gson.Gson;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.window.component.browser.event.EvernoteOAuth2BrowserErrorEvent;
import org.bigtows.window.component.browser.event.EvernoteOAuth2BrowserTokenSuccessProcessedEvent;
import org.bigtows.window.controller.ResponseToken;
import org.bigtows.window.controller.WebViewController;

import java.io.IOException;

/**
 * Component of authentication on Evernote
 */
public class EvernoteOAuth2Browser {

    /**
     * Instance of JSON parser (GSON)
     */
    private final Gson jsonParser;

    /**
     * Callback on success process token
     */
    private EvernoteOAuth2BrowserTokenSuccessProcessedEvent tokenSuccessProcessedEvent;

    /**
     * Callback on error
     */
    private EvernoteOAuth2BrowserErrorEvent errorEvent;


    @Inject
    public EvernoteOAuth2Browser(Gson gson) {
        this.jsonParser = gson;
    }

    /**
     * Subscribe on success process token
     *
     * @param tokenSuccessProcessedEvent callback for success process token
     * @return self
     */
    public EvernoteOAuth2Browser onTokenSuccessProcessed(EvernoteOAuth2BrowserTokenSuccessProcessedEvent tokenSuccessProcessedEvent) {
        this.tokenSuccessProcessedEvent = tokenSuccessProcessedEvent;
        return this;
    }

    /**
     * Subscribe on error
     *
     * @param errorEvent callback on error
     * @return self
     */
    public EvernoteOAuth2Browser onError(EvernoteOAuth2BrowserErrorEvent errorEvent) {
        this.errorEvent = errorEvent;
        return this;
    }


    /**
     * Open browser.
     * <p>
     * This method JavaFX Thread safely.
     *
     * @param jfxPanel        where put the element of browser
     * @param pinNoteSettings settings PinNote
     */
    public void openBrowser(JFXPanel jfxPanel, PinNoteSettings pinNoteSettings) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = this.buildFXMLLoader(pinNoteSettings);
                jfxPanel.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                this.errorEvent.onError(e);
            }
        });
    }

    /**
     * Build FXML loader
     *
     * @param pinNoteSettings PinNote settings
     * @return FXML loader
     */
    private FXMLLoader buildFXMLLoader(PinNoteSettings pinNoteSettings) {
        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(getClass().getClassLoader());
        loader.setLocation(getClass().getResource("/fxml/WebView.fxml"));
        loader.setControllerFactory((clazz) -> this.getInstanceWebViewController(pinNoteSettings));
        return loader;
    }

    /**
     * Get instance of webViewController
     *
     * @param pinNoteSettings settings of PinNote
     * @return controller
     */
    private WebViewController getInstanceWebViewController(PinNoteSettings pinNoteSettings) {
        return new WebViewController(
                pinNoteSettings,
                this::callbackWebView
        );
    }

    /**
     * Callback of webview
     *
     * @param responseOfToken response token
     */
    private void callbackWebView(String responseOfToken) {
        //TODO added check JSON
        this.tokenSuccessProcessedEvent.onTokenSuccessProcessed(jsonParser.fromJson(responseOfToken, ResponseToken.class));
    }

}
