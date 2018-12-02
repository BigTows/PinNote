package org.bigtows.config;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.bigtows.components.SimpleEvernoteTokenStorage;
import org.bigtows.components.ThemeHelper;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.config.settings.parser.PinNoteSettingsXmlParser;
import org.bigtows.config.settings.parser.exception.ParserException;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.note.storage.credential.evernote.EvernoteCredentialImpl;
import org.bigtows.note.storage.parse.evernote.EvernoteStorageParserImpl;
import org.bigtows.window.component.browser.EvernoteOAuth2Browser;
import org.bigtows.window.component.note.EvernoteNoteView;

/**
 * Configuration class for setup DI
 */
public class PinNoteConfigurator extends AbstractModule {


    @Override
    protected void configure() {
        super.configure();
    }

    @Provides
    @Singleton
    public Gson providerGson() {
        return new Gson();
    }

    /**
     * Provider Settings of PinNote
     *
     * @return Settings of PinNote
     * @throws ParserException when parser can't prepare file
     */
    @Provides
    @Singleton
    public PinNoteSettings providerPinNoteSettings() throws ParserException {
        return new PinNoteSettingsXmlParser(
                getClass().getClassLoader().getResource(
                        "environment.xml").toString()
        ).getPinNoteSettings();
    }


    @Provides
    @Inject
    @Singleton
    public EvernoteCredential providerEvernoteCredential(PinNoteSettings settings) {
        return new EvernoteCredentialImpl(
                settings,
                new SimpleEvernoteTokenStorage()
        );
    }


    @Provides
    @Inject
    @Singleton
    public EvernoteStorage providerEvernoteStorage(EvernoteCredential evernoteCredential) {
        return new EvernoteStorage(
                evernoteCredential,
                new EvernoteStorageParserImpl()
        );
    }

    @Provides
    @Singleton
    public ThemeHelper providerEvernoteStorage() {
        return new ThemeHelper();
    }


    @Provides
    @Inject
    @Singleton
    public EvernoteOAuth2Browser providerEvernoteOAuth2Browser(Gson gson) {
        return new EvernoteOAuth2Browser(gson);
    }

    @Inject
    @Provides
    @Singleton
    public EvernoteNoteView providerEvernoteNoteView(ThemeHelper themeHelper) {
        return new EvernoteNoteView(themeHelper);
    }
}
