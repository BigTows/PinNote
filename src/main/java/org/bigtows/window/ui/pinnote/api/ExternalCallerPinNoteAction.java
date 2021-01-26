package org.bigtows.window.ui.pinnote.api;


import org.bigtows.window.ui.pinnote.PinNoteComponent;

public class ExternalCallerPinNoteAction {

    private PinNoteComponent component;


    public void registerComponent(PinNoteComponent component) {
        this.component = component;
    }


    public void actionAddTarget(){
        if (this.component != null){
            component.getAddAction().actionPerformed(null);
        }
    }
}
