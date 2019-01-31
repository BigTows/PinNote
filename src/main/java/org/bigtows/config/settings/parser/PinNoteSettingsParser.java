/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.config.settings.parser;

import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.config.settings.parser.exception.ParserException;

public interface PinNoteSettingsParser {


    PinNoteSettings getPinNoteSettings() throws ParserException;
}
