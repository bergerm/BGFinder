package com.bgonline.bgfinder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Manu on 9/24/2016.
 */

public class GameTable {
    private int tableId;
    private String tableName;
    private String location;
    private Date date;
    private ArrayList<Player> playerList;
    private Context context;

    public GameTable(Context context, int id) {
        this.context = context;
        tableId = id;
        tableName = context.getResources().getString(R.string.new_table_name);
        location = context.getResources().getString(R.string.tbd);
        date = null;
        playerList = new ArrayList<Player>();
    }
}
