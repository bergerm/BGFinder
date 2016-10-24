package com.bgonline.bgfinder;

import android.content.Context;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameTable implements Serializable{
    private int tableId;
    private String tableName;
    private String gameName;
    private String location;
    private String date;
    private String player1;
    private String player2;
    private String player3;
    private String player4;
    private String player5;
    private String player6;

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
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
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer3() {
        return player3;
    }

    public void setPlayer3(String player3) {
        this.player3 = player3;
    }

    public String getPlayer4() {
        return player4;
    }

    public void setPlayer4(String player4) {
        this.player4 = player4;
    }

    public String getPlayer5() {
        return player5;
    }

    public void setPlayer5(String player5) {
        this.player5 = player5;
    }

    public String getPlayer6() {
        return player6;
    }

    public void setPlayer6(String player6) {
        this.player6 = player6;
    }


    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public GameTable(Context context, int id) {
        tableId = id;
        tableName = context.getResources().getString(R.string.new_table_name);
        gameName = "TBD";
        location = context.getResources().getString(R.string.tbd);
        date = context.getResources().getString(R.string.tbd);
        player1 = "me";
        player2 = "";
        player3 = "";
        player4 = "";
        player5 = "";
        player6 = "";

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
