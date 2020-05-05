package org.bigtows.window.ui.notetree.tree.entity;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Task {
    @Builder.Default
    private String text = "";

    @Builder.Default
    private String identity = UUID.randomUUID().toString();
    @Builder.Default
    private Boolean checked = false;

}
