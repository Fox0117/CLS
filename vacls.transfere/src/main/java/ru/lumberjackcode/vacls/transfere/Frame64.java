package ru.lumberjackcode.vacls.transfere;

public class Frame64 {
    private String frame64;

    private int frameWidth;

    private int frameHeight;

    private int frameType;

    public Frame64(String frame64, int frameWidth, int frameHeight, int frameType){
        this.frame64 = frame64;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameType = frameType;
    }


    public String getFrame64() {
        return frame64;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameType() {
        return frameType;
    }
}
