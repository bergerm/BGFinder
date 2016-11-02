package com.bgonline.bgfinder;

import android.content.Context;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameTable implements Serializable{
    private String tableId;
    private String tableName;
    private String gameName;
    private String location;
    private String date;
    private String player1Id;
    private String player2Id;
    private String player3Id;
    private String player4Id;
    private String player1UserName;
    private String player2UserName;
    private String player3UserName;
    private String player4UserName;

    private String player5Id;
    private String player6Id;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlayer1() {
        return player1Id;
    }

    public void setPlayer1(String player1) {
        this.player1Id = player1;
    }

    public String getPlayer2() {
        return player2Id;
    }

    public void setPlayer2(String player2) {
        this.player2Id = player2;
    }

    public String getPlayer3() {
        return player3Id;
    }

    public void setPlayer3(String player3) {
        this.player3Id = player3;
    }

    public String getPlayer4() {
        return player4Id;
    }

    public void setPlayer4(String player4) {
        this.player4Id = player4;
    }


    public String getPlayer1UserName() {
        return player1UserName;
    }

    public void setPlayer1UserName(String player1) {
        this.player1UserName = player1;
    }

    public String getPlayer2UserName() {
        return player2UserName;
    }

    public void setPlayer2UserName(String player2) {
        this.player2UserName = player2;
    }

    public String getPlayer3UserName() { return player3UserName; }

    public void setPlayer3UserName(String player3) {
        this.player3UserName = player3;
    }

    public String getPlayer4UserName() {
        return player4UserName;
    }

    public void setPlayer4UserName(String player4) {
        this.player4UserName = player4;
    }

    public String getPlayer5() {
        return player5Id;
    }

    public void setPlayer5(String player5) {
        this.player5Id = player5;
    }

    public String getPlayer6() {
        return player6Id;
    }

    public void setPlayer6(String player6) {
        this.player6Id = player6;
    }


    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public GameTable(Context context, String id) {
        tableId = id;
        tableName = context.getResources().getString(R.string.new_table_name);
        gameName = "TBD";
        location = context.getResources().getString(R.string.tbd);
        date = context.getResources().getString(R.string.tbd);
        player1Id = "";
        player2Id = "";
        player3Id = "";
        player4Id = "";
        player1UserName = "";
        player2UserName = "";
        player3UserName = "";
        player4UserName = "";
        player5Id = "";
        player6Id = "";
    }

    public String toJson() {
        Gson gson = new Gson();
        String json = "";
        try {
            json = gson.toJson(this);
        }
        catch (Exception e) {
            String ex = e.toString();
            return "";
        }
        return json;
    }

    public static GameTable fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, GameTable.class);
    }
}
