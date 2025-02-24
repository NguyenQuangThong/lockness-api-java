package com.example.LocknessAPI.commons;

public enum EntityStatus {
    IDLE((byte) 0, "Idle"),
    OFF((byte) 1, "Off"),
    BUSY((byte) 2, "Busy"),
    PENDING((byte) 3, "Pending"),
    DONE((byte) 4, "Done");

    private byte code;
    private String status;

    EntityStatus(byte code, String status) {
        this.code = code;
        this.status = status;
    }

    public byte getCode() {
        return this.code;
    }

    public String getStatus() {
        return this.status;
    }
}
