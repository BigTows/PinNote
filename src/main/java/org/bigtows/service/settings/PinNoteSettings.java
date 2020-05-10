package org.bigtows.service.settings;

import com.intellij.serviceContainer.NonInjectable;
import lombok.Data;

@Data
public class PinNoteSettings {

    private final Storage storage = Storage.builder()
            .evernote(
                    EvernoteStorage.builder()
                            .oAuth(
                                    OAuthSettings.builder()
                                            .url("https://pinnote.bigtows.org/")
                                            .build()
                            )
                            .build()
            )
            .build();

    @NonInjectable
    public PinNoteSettings() {
    }
}
