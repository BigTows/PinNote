package org.bigtows.window.ui.notetree.factory;

public class EditorTimer {

    private final Runnable runnable;
    private Status status = Status.END;

    public EditorTimer(Runnable runnable) {
        this.runnable = runnable;
        initTimer().start();
    }

    public void editing() {
        this.status = Status.EDITING;
    }


    private Thread initTimer() {
        return new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                if (status == Status.EDITING) {
                    status = Status.NEED_PUBLISH;
                } else if (status == Status.NEED_PUBLISH) {
                    System.out.println("Publish!!");
                    runnable.run();
                    status = Status.END;
                }
            }
        });
    }
}

enum Status {
    EDITING,
    NEED_PUBLISH,
    END
}
