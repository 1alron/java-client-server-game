package com.example.lab1;

import com.example.lab1.model.PlayerGameInfo;

public class ClientRequest {
    private ClientActionType type;
    private PlayerGameInfo playerGameInfo;

    @Override
    public String toString() {
        return "ClientRequest{" +
                "type=" + type +
                ", gameInfo=" + playerGameInfo +
                '}';
    }

    public ClientRequest(ClientActionType type, PlayerGameInfo playerGameInfo) {
        this.type = type;
        this.playerGameInfo = playerGameInfo;
    }

    public ClientActionType getType() {
        return type;
    }

    public void setType(ClientActionType type) {
        this.type = type;
    }

    public PlayerGameInfo getPlayerGameInfo() {
        return playerGameInfo;
    }

    public void setPlayerGameInfo(PlayerGameInfo playerGameInfo) {
        this.playerGameInfo = playerGameInfo;
    }
}