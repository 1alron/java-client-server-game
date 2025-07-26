package com.example.lab1.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<PlayerGameInfo> playersGameInfo = new ArrayList<>();
    private List<Shooter> shooters = new ArrayList<>();
    private List<Target> targets = new ArrayList<>();
    private List<Arrow> arrows = new ArrayList<>();
    private String winner = null;

    public GameState() {
    }

    public List<PlayerGameInfo> getPlayersGameInfo() {
        return playersGameInfo;
    }

    public void setPlayersGameInfo(List<PlayerGameInfo> playersGameInfo) {
        this.playersGameInfo = playersGameInfo;
    }

    public List<Shooter> getShooters() {
        return shooters;
    }

    public void setShooters(List<Shooter> shooters) {
        this.shooters = shooters;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<Arrow> getArrows() {
        return arrows;
    }

    public void setArrows(List<Arrow> arrows) {
        this.arrows = arrows;
    }

    public void addPlayerGameInfo(PlayerGameInfo p) {
        playersGameInfo.add(p);
    }

    public void addShooter(Shooter sh) {
        shooters.add(sh);
    }

    public void addTarget(Target t) {
        targets.add(t);
    }

    public void addArrow(Arrow arr) {
        arrows.add(arr);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "playersGameInfo=" + playersGameInfo +
                ", shooters=" + shooters +
                ", targets=" + targets +
                ", arrows=" + arrows +
                '}';
    }
}
