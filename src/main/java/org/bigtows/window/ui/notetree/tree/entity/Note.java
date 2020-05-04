package org.bigtows.window.ui.notetree.tree.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Note {

    @Builder.Default
    private String name = "";
    
    @Builder.Default
    private String identity = "";
}
