package org.bigtows.window.ui.pinnote.settings;

import org.bigtows.window.ui.pinnote.OAuthPanel;

/**
 * Handler of action when user want enable, disable or fix source.
 */
public interface SourceAction {

    /**
     * When user wanting change status of source
     *
     * @param panel               panel with current source
     * @param currentStatusSource current status source
     */
    void call(OAuthPanel panel, StatusSource currentStatusSource);
}
