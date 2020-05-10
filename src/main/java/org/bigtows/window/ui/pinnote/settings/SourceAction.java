package org.bigtows.window.ui.pinnote.settings;

import org.bigtows.window.ui.pinnote.OAuthPanel;

public interface SourceAction {

    void call(OAuthPanel panel, StatusSource currentSource);
}
