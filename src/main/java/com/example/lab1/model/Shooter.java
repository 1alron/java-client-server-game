package com.example.lab1.model;

import com.example.lab1.GameUtil;

public class Shooter {
    private double x = GameUtil.SHOOTER_START_X_POSITION;
    private double y;

    private String color;

    public Shooter(double y, String color) {
        this.y = y;
        this.color = color;
    }

    public Shooter(double x, double y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Shooter{" +
                "x=" + x +
                ", y=" + y +
                ", color='" + color + '\'' +
                '}';
    }
}
