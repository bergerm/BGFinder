package com.bgonline.bgfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class TablesFragment extends SynchronizedLoadFragment {
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onUpdateTablesArray(ArrayList<GameTable> tables);
    }

    DatabaseReference database;
    String connectedUserId;

    private ArrayList<GameTable> arrayOfTables;
    private TablesListAdapter tablesListAdapter;

    private static final String TAG = "BGFinderTablesFragment";

    public static TablesFragment newInstance() {
        return new TablesFragment();
    }

    public TablesFragment() {
    }

    private void updatePlayersInTables() {
        int numOfTables = tablesListAdapter.getCount();
        for (int i = 0; i < numOfTables; i++) {
            //final String tableId = arrayOfTables.get(i).getTableId();
            final String tableId = tablesListAdapter.getItem(i).getTableId();
            final int j = i;
            database.child("usersInTables").child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (DataSnapshot player : dataSnapshot.getChildren()) {
                        GameTable table = (GameTable) tablesListAdapter.getItem(j);

                        String playerId = (String) player.getValue();
                        switch (count) {
                            case 0:
                                table.setPlayer1(playerId);
                                break;
                            case 1:
                                table.setPlayer2(playerId);
                                break;
                            case 2:
                                table.setPlayer3(playerId);
                                break;
                            case 3:
                                table.setPlayer4(playerId);
                                break;
                            default:
                                return;
                        }

                        count++;
                    }
                    tablesListAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            database.child("usersInTables").child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (DataSnapshot player : dataSnapshot.getChildren()) {
                        final GameTable table = (GameTable) tablesListAdapter.getItem(j);

                        String playerId = (String) player.getValue();
                        switch (count) {
                            case 0:
                                table.setPlayer1(playerId);
                                database.child("users").child(playerId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        table.setPlayer1UserName(dataSnapshot.getValue().toString());
                                        tablesListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case 1:
                                table.setPlayer2(playerId);
                                database.child("users").child(playerId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        table.setPlayer2UserName(dataSnapshot.getValue().toString());
                                        tablesListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case 2:
                                table.setPlayer3(playerId);
                                database.child("users").child(playerId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        table.setPlayer3UserName(dataSnapshot.getValue().toString());
                                        tablesListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case 3:
                                table.setPlayer4(playerId);
                                database.child("users").child(playerId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        table.setPlayer4UserName(dataSnapshot.getValue().toString());
                                        tablesListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            default:
                                return;
                        }
                        count++;
                    }
                    tablesListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void downloadArrayOfTables() {
        if (database == null) {
            return;
        }
        arrayOfTables = new ArrayList<GameTable>();
        database.child("tablesForUsers").child(connectedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tableNumber: dataSnapshot.getChildren()) {
                    String tableId = tableNumber.getValue().toString();

                    database.child("tables").child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String tableId = dataSnapshot.getKey();
                            GameTable newTable = new GameTable(getContext(), tableId);
                            newTable.setTableName((String)dataSnapshot.child("name").getValue());
                            newTable.setGameName((String)dataSnapshot.child("GameName").getValue());
                            newTable.setDate((String)dataSnapshot.child("when").getValue());
                            newTable.setLocation((String)dataSnapshot.child("where").getValue());
                            //arrayOfTables.add(newTable);
                            tablesListAdapter.add(newTable);
                            tablesListAdapter.notifyDataSetChanged();
                            updatePlayersInTables();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<GameTable>getArrayOfTables(Context context) {
        if (arrayOfTables != null) {
            return arrayOfTables;
        }

        String json = "";
        try {
            InputStream inputStream = context.openFileInput("arrayOfTables.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                json = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            // LOG here
        }

        if (!json.equals("null") && !json.equals("")) {
            Gson gson = new Gson();
            arrayOfTables = gson.fromJson(json, new TypeToken<ArrayList<GameTable>>() {
            }.getType());
        }
        else {
            arrayOfTables = new ArrayList<GameTable>();
        }

        return arrayOfTables;
    }

    String getNewTableId() {
        return database.child("tables").push().getKey();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View tablesView = inflater.inflate(R.layout.tables_list, container, false);
        connectedUserId = getArguments().getString("connectedUserId");
        database = FirebaseDatabase.getInstance().getReference();
        //getArrayOfTables(getContext().getApplicationContext());
        arrayOfTables = new ArrayList<GameTable>();

        ListView tablesList = (ListView)tablesView.findViewById(R.id.tables);
        tablesListAdapter = new TablesListAdapter(container.getContext(), R.layout.game_table, arrayOfTables);
        tablesList.setAdapter(tablesListAdapter);

        Button newTableButton = (Button) tablesView.findViewById(R.id.new_table_button);
        newTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewTableActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        tablesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int itemId = (int) pos;
                String tableName = tablesListAdapter.getItem((int)itemId).getTableName();
                String dialogString = "Remove " + tableName + "?";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(dialogString);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameTable removedTable = tablesListAdapter.getItem(itemId);
                        tablesListAdapter.remove(removedTable);
                        tablesListAdapter.notifyDataSetChanged();

                        database.child("tables").child(removedTable.getTableId()).removeValue();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });

//        currentLayoutId = R.layout.tables_list;
        downloadArrayOfTables();
        return tablesView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        String result = data.getStringExtra("RESULT");
        if (!result.equals("SUCCESS")) {
            return;
        }

        String newTableJson=data.getStringExtra("NEW_TABLE");
        if (!newTableJson.equals("null") && !newTableJson.equals("")) {
            Gson gson = new Gson();
            GameTable newTable = gson.fromJson(newTableJson, GameTable.class);

            String newTableId = getNewTableId();
            newTable.setTableId(newTableId);
            database.child("tables").child(newTableId).child("name").setValue(newTable.getTableName());
            database.child("tables").child(newTableId).child("GameName").setValue(newTable.getGameName());
            database.child("tables").child(newTableId).child("when").setValue(newTable.getDate());
            database.child("tables").child(newTableId).child("where").setValue(newTable.getLocation());

            database.child("usersInTables").child(newTableId).child("0").setValue(connectedUserId);

            arrayOfTables.add(newTable);
            tablesListAdapter.notifyDataSetChanged();
        }
    }

    private void saveTables() {
        Gson gson = new Gson();
        String json = gson.toJson(arrayOfTables);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().getApplicationContext().openFileOutput("arrayOfTables.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Error opening gamesFile for write.");
        }

        mCallback.onUpdateTablesArray(arrayOfTables);
    }

    @Override
    public void onPause() {
        saveTables();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        saveTables();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        //downloadArrayOfTables();
    }
}
