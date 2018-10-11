package org.bigtows.config;

import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.config.settings.parser.PinNoteSettingsXmlParser;
import org.bigtows.config.settings.parser.exception.ParserException;
import org.bigtows.util.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PinNoteSettingsTest {


    private PinNoteSettings settings;

    private File environmentFile;

    @Before
    public void setup() {
        this.environmentFile = Resource.getFile("config/environment.xml");
    }


    @Test
    public void testSimpleParse() throws ParserException {
        PinNoteSettingsXmlParser pinNoteSettingsXmlParser = new PinNoteSettingsXmlParser(environmentFile.toString());

        PinNoteSettings settings = pinNoteSettingsXmlParser.getPinNoteSettings();

        Assert.assertTrue(settings.isDebugMode());

        Assert.assertEquals("http://testServer.ru/debug", settings.getEvernoteSettings().getOAuth2Server());

    }

}
