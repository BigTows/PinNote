package org.bigtows.config.settings.parser;

import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.config.settings.parser.exception.ParserException;

public interface PinNoteSettingsParser {


    PinNoteSettings getPinNoteSettings() throws ParserException;
}
