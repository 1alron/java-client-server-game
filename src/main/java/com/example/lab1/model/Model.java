package com.example.lab1.model;

import com.example.lab1.GameUtil;
import com.example.lab1.IObserver;
import com.example.lab1.database.HibernateActions;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private GameState gameState = new GameState();

    List<IObserver> observers = new ArrayList<>();

    public void refreshUI() {
        observers.forEach(IObserver::refreshUI);
    }

    public void addObserver(IObserver observer) {
        observers.add(observer);
        refreshUI();
    }

    Model() {
    }

    private Thread gameThread = null;

    public Thread getGameThread() {
        return gameThread;
    }

    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void resume() {
        synchronized (this) {
            if (gameThread != null) {
                this.notifyAll();
            }
        }
    }

    public void startGame() {
        isRunning = true;
        if (gameThread == null) {
            gameThread = new Thread(() -> {
                while (isRunning) {
                    updateTargets();
                    for (PlayerGameInfo player : getPlayersGameInfo()) {
                        if (player.isShooting()) {
                            shoot(player.getName());
                        }
                    }
                    try {
                        if (isPaused) {
                            synchronized (this) {
                                this.wait();
                            }
                            isPaused = false;
                        }
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        isRunning = false;
                        gameThread = null;
                    }
                }
            });
            gameThread.start();
        }
    }

    public void startShooting(String name) {
        for (PlayerGameInfo player : getPlayersGameInfo()) {
            if (player.getName().equals(name)) {
                player.setShooting(true);
                break;
            }
        }
        incrementShots(name);
    }

    public void endShooting(String name) {
        for (PlayerGameInfo player : getPlayersGameInfo()) {
            if (player.getName().equals(name)) {
                player.setShooting(false);
                break;
            }
        }
    }

    public void shoot(String name) {
        moveArrow(name);
        checkIfLargerTargetIsHit(name);
        checkIfSmallerTargetIsHit(name);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        refreshUI();
    }

    public synchronized List<Arrow> getArrows() {
        return gameState.getArrows();
    }

    public void setArrows(List<Arrow> arrows) {
        gameState.setArrows(arrows);
        refreshUI();
    }

    public void addPlayerGameInfo(PlayerGameInfo p) {
        gameState.addPlayerGameInfo(p);
        refreshUI();
    }

    public synchronized List<PlayerGameInfo> getPlayersGameInfo() {
        return gameState.getPlayersGameInfo();
    }

    public void setPlayersGameInfo(List<PlayerGameInfo> playersGameInfo) {
        gameState.setPlayersGameInfo(playersGameInfo);
        refreshUI();
    }

    public void addShooter(Shooter shooter) {
        gameState.addShooter(shooter);
        refreshUI();
    }

    public List<Shooter> getShooters() {
        return gameState.getShooters();
    }

    public void setShooters(List<Shooter> shooters) {
        gameState.setShooters(shooters);
        refreshUI();
    }

    public synchronized List<Target> getTargets() {
        return gameState.getTargets();
    }

    public void setTargets(List<Target> targets) {
        gameState.setTargets(targets);
        refreshUI();
    }

    public void addArrow(Arrow arrow) {
        gameState.addArrow(arrow);
        refreshUI();
    }

    public void addTarget(Target target) {
        gameState.addTarget(target);
        refreshUI();
    }

    private void updateTargets() {
        List<Target> targets = getTargets();
        for (Target target : targets) {
            if (target.getR() == GameUtil.SMALLER_TARGET_RADIUS) {
                double targetNewY = target.getY() + 3.5;
                if (targetNewY > GameUtil.GAME_AREA_HEIGHT) {
                    targetNewY = 0.0;
                }
                target.setY(targetNewY);
            }
        }
        for (Target target : targets) {
            if (target.getR() == GameUtil.LARGER_TARGET_RADIUS) {
                double targetNewY = target.getY() + 1.75;
                if (targetNewY > GameUtil.GAME_AREA_HEIGHT) {
                    targetNewY = 0.0;
                }
                target.setY(targetNewY);
            }
        }
        setTargets(targets);
    }

    private void moveArrow(String ownerName) {
        List<Arrow> arrows = getArrows();
        for (Arrow arrow : arrows) {
            if (arrow.getOwnerName().equals(ownerName)) {
                double arrowNewX = arrow.getX() + 10.0;
                if (arrowNewX > GameUtil.GAME_AREA_WIDTH) {
                    arrowNewX = GameUtil.ARROW_START_X_POSITION;
                    for (PlayerGameInfo player : getPlayersGameInfo()) {
                        if (player.getName().equals(ownerName)) {
                            player.setShooting(false);
                            break;
                        }
                    }
                }
                arrow.setX(arrowNewX);
            }
        }
        setArrows(arrows);
    }

    private void checkIfLargerTargetIsHit(String ownerName) {
        List<Arrow> arrows = getArrows();
        double arrowX = 0.0, arrowY = 0.0;
        double targetX = 0.0, targetY = 0.0, targetR = GameUtil.LARGER_TARGET_RADIUS;
        for (Arrow arrow : arrows) {
            if (arrow.getOwnerName().equals(ownerName)) {
                arrowX = arrow.getX();
                arrowY = arrow.getY();
                break;
            }
        }
        for (Target target : getTargets()) {
            if (target.getR() == targetR) {
                targetX = target.getX();
                targetY = target.getY();
                break;
            }
        }
        if ((Math.pow(arrowX - targetX, 2) + Math.pow(arrowY - targetY, 2)) <= (targetR * targetR)) {
            for (Arrow arrow : arrows) {
                if (arrow.getOwnerName().equals(ownerName)) {
                    arrow.setX(GameUtil.ARROW_START_X_POSITION);
                    break;
                }
            }
            setArrows(arrows);
            incrementScoreByOne(ownerName);
            endShooting(ownerName);
        }
    }

    private void checkIfSmallerTargetIsHit(String ownerName) {
        List<Arrow> arrows = getArrows();
        double arrowX = 0.0, arrowY = 0.0;
        double targetX = 0.0, targetY = 0.0, targetR = GameUtil.SMALLER_TARGET_RADIUS;
        for (Arrow arrow : arrows) {
            if (arrow.getOwnerName().equals(ownerName)) {
                arrowX = arrow.getX();
                arrowY = arrow.getY();
                break;
            }
        }
        for (Target target : getTargets()) {
            if (target.getR() == targetR) {
                targetX = target.getX();
                targetY = target.getY();
                break;
            }
        }
        if ((Math.pow(arrowX - targetX, 2) + Math.pow(arrowY - targetY, 2)) <= (targetR * targetR)) {
            for (Arrow arrow : arrows) {
                if (arrow.getOwnerName().equals(ownerName)) {
                    arrow.setX(GameUtil.ARROW_START_X_POSITION);
                    break;
                }
            }
            setArrows(arrows);
            incrementScoreByTwo(ownerName);
            endShooting(ownerName);
        }
    }

    private void incrementShots(String name) {
        List<PlayerGameInfo> players = getPlayersGameInfo();
        for (PlayerGameInfo player : players) {
            if (player.getName().equals(name)) {
                player.setShots(player.getShots() + 1);
                break;
            }
        }
        setPlayersGameInfo(players);
    }

    private void incrementScoreByOne(String name) {
        List<PlayerGameInfo> players = getPlayersGameInfo();
        int score;
        for (PlayerGameInfo player : players) {
            if (player.getName().equals(name)) {
                score = player.getScore() + 1;
                player.setScore(score);
                break;
            }
        }
        setPlayersGameInfo(players);
        checkWinner();
    }


    private void incrementScoreByTwo(String name) {
        List<PlayerGameInfo> players = getPlayersGameInfo();
        int score;
        for (PlayerGameInfo player : players) {
            if (player.getName().equals(name)) {
                score = player.getScore() + 2;
                player.setScore(score);
                break;
            }
        }
        setPlayersGameInfo(players);
        checkWinner();
    }

    public void checkWinner() {
        List<PlayerGameInfo> players = getPlayersGameInfo();
        for (PlayerGameInfo player : players) {
            if (player.getScore() >= GameUtil.SCORE_TO_WIN) {
                isRunning = false;
                if (gameThread != null) {
                    gameThread.interrupt();
                    gameThread = null;
                }
                GameState gameState = getGameState();
                gameState.setWinner(player.getName());
                for (Target target : getTargets()) {
                    target.setY(GameUtil.TARGETS_START_Y_POSITION);
                }
                for (PlayerGameInfo pl : players) {
                    pl.setReady(false);
                }
                setGameState(gameState);
                HibernateActions.updateDB(player.getName());
                break;
            }
        }
    }


}