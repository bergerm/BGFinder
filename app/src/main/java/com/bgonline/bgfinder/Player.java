package com.bgonline.bgfinder;

import com.google.gson.Gson;

public class Player {
    private int playerId;
    private String userName;
    private byte[] imageBlob;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Player fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Player.class);
    }
}
