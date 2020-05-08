package org.bigtows.service.state;

import lombok.Data;

@Data
public class EvernoteState {

    private boolean enable = false;

    private String token = null;

    private StatusConnection statusConnection = StatusConnection.DISABLED;

    public void setEnable(boolean enabled) {
        this.enable = enabled;
        if (!enabled) {
            this.statusConnection = StatusConnection.DISABLED;
        }
    }
}
