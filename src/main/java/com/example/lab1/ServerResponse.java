package com.example.lab1;

import com.example.lab1.entities.Player;
import com.example.lab1.model.GameState;

import java.util.ArrayList;
import java.util.List;

public class ServerResponse {
    private ServerActionType type;
    private GameState gameState;
    private List<Player> players = new ArrayList<>();

    @Override
    public String toString() {
        return "ServerResponse{" +
                "type=" + type +
                ", gameState=" + gameState +
                ", leaders=" + players +
                '}';
    }

    public ServerResponse(ServerActionType type, GameState gameState) {
        this.type = type;
        this.gameState = gameState;
    }

    public ServerResponse(ServerActionType type, GameState gameState, List<Player> players) {
        this.type = type;
        this.gameState = gameState;
        this.players = players;
    }

    public ServerActionType getType() {
        return type;
    }

    public void setType(ServerActionType type) {
        this.type = type;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
