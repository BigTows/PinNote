package org.bigtows.config;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.intellij.ide.util.PropertiesComponent;
import org.bigtows.components.SimpleEvernoteTokenStorage;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.config.settings.parser.PinNoteSettingsXmlParser;
import org.bigtows.config.settings.parser.exception.ParserException;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.note.storage.credential.evernote.EvernoteCredentialImpl;
import org.bigtows.note.storage.parse.evernote.EvernoteStorageParserImpl;

public class PinNoteConfigurator extends AbstractModule {

    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    @Override
    protected void configure() {
        super.configure();
    }

    @Provides
    public PinNoteSettings providerPinNoteConfigurator() throws ParserException {
        return new PinNoteSettingsXmlParser(
                getClass().getClassLoader().getResource(
                        "environment.xml").toString()
        ).getPinNoteSettings();
    }


    @Provides
    @Inject
    public EvernoteCredential providerEvernoteCredential(PinNoteSettings settings) {
        return new EvernoteCredentialImpl(
                settings,
                new SimpleEvernoteTokenStorage()
        );
    }


    @Provides
    @Inject
    public EvernoteStorage providerEvernoteStorage(EvernoteCredential evernoteCredential) {
        return new EvernoteStorage(
                evernoteCredential,
                new EvernoteStorageParserImpl()
        );
    }
}
