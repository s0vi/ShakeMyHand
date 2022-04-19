package dev.s0vi.shakemyhand.client;

public class Lock {

    private final String VERSION_STRING_TO_NOT_TOUCH = "0.1.0";
    private boolean changeModlistOnRestart = false;
    private String targetModlist = "";


    public String getVersionString() {
        return VERSION_STRING_TO_NOT_TOUCH;
    }

    public Lock setChangeModlistOnRestart(boolean bool) {
        this.changeModlistOnRestart = bool;
        return this;
    }

    public boolean getChangeModlistOnRestart() {
        return changeModlistOnRestart;
    }

    public Lock setTargetModlist(String targetModlist) {
        this.targetModlist = targetModlist;
        return this;
    }

    public String getTargetModlist() {
        return targetModlist;
    }
}
