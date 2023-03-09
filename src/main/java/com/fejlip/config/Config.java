package com.fejlip.config;

public class Config {
    private boolean autoBuyEnabled;
    private boolean autoOpenEnabled;
    private boolean debug;
    private int bedClickDelay;
    private int bedInitialDelay;

    private int bedClickAmount;

    public Config() {
        this.autoBuyEnabled = false;
        this.autoOpenEnabled = false;
        this.bedInitialDelay = 2300;
        this.bedClickDelay = 70;
        this.bedClickAmount = 5;
        this.debug = false;


    }

    public boolean isAutoBuyEnabled() {
        return autoBuyEnabled;
    }

    public boolean toggleAutoBuy() {
        this.autoBuyEnabled = !this.autoBuyEnabled;
        return this.autoBuyEnabled;
    }

    public boolean isAutoOpenEnabled() {
        return autoOpenEnabled;
    }

    public boolean toggleAutoOpen() {
        this.autoOpenEnabled = !this.autoOpenEnabled;
        return this.autoOpenEnabled;
    }

    public int getBedClickDelay() {
        return bedClickDelay;
    }

    public void setBedClickDelay(int bedDelay) {
        this.bedClickDelay = bedDelay;
    }

    public int getBedInitialDelay() {
        return bedInitialDelay;
    }

    public void setBedInitialDelay(int bedInitialDelay) {
        this.bedInitialDelay = bedInitialDelay;
    }

    public int getBedClickAmount() {
        return bedClickAmount;
    }

    public void setBedClickAmount(int bedClickAmount) {
        this.bedClickAmount = bedClickAmount;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean toggleDebug() {
        this.debug = !this.debug;
        return this.debug;
    }
}
