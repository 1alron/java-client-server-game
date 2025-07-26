package com.example.lab1.model;

public class PlayerGameInfo {
    private String name;
    private int score;
    private int shots;
    private boolean isReady = false;
    private boolean isShooting = false;

    public PlayerGameInfo(String name, int score, int shots) {
        this.name = name;
        this.score = score;
        this.shots = shots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    @Override
    public String toString() {
        return "PlayerGameInfo{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", shoots=" + shots +
                ", isReady=" + isReady +
                ", isShooting=" + isShooting +
                '}';
    }
}
