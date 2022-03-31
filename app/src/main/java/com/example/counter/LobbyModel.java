package com.example.counter;

public class LobbyModel {
    String lobbyName;
    Integer inviteCode;

    public LobbyModel(String lobbyName, Integer inviteCode) {
        this.lobbyName = lobbyName;
        this.inviteCode = inviteCode;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Integer getInviteCode() {
        return inviteCode;
    }
}
