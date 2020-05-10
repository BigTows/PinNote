package org.bigtows.notebook;

public interface Task {

    public String getId();

    public String getName();

    public boolean isChecked();

    void setName(String name);

    void setChecked(boolean checked);
}
