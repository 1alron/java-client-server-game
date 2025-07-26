package com.example.lab1.model;

import com.example.lab1.GameUtil;

public class Arrow {
    private double x = GameUtil.ARROW_START_X_POSITION;
    private double y;
    private String ownerName;

    public Arrow(double y, String ownerName) {
        this.y = y;
        this.ownerName = ownerName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return "Arrow{" +
                "x=" + x +
                ", y=" + y +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
