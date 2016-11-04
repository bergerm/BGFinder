package com.bgonline.bgfinder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.tables_progress_bar);
        progressBar.setVisibility(View.GONE);

        int numOfTables = tablesListAdapter.getCount();
        for (int i = 0; i < numOfTables; i++) {
            //final String tableId = arrayOfTables.get(i).getTableId();
            final String tableId = tablesListAdapter.getItem(i).getTableId();
            final int j = i;
            database.child("usersInTables").child(tableId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 0;

                    GameTable table = (GameTable) tablesListAdapter.getItem(j);

                    for (DataSnapshot player : dataSnapshot.getChildren()) {
                        //String playerId = (String) player.getValue();
                        String playerId = (String) player.getKey();
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

                    for (; count < 4; count++) {
                        switch (count) {
                            case 0:
                                table.setPlayer1("");
                                break;
                            case 1:
                                table.setPlayer2("");
                                break;
                            case 2:
                                table.setPlayer3("");
                                break;
                            case 3:
                                table.setPlayer4("");
                                break;
                            default:
                                return;
                        }
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

                        //String playerId = (String) player.getValue();
                        String playerId = (String) player.getKey();
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
                    //String tableId = tableNumber.getValue().toString();
                    String tableId = tableNumber.getKey().toString();

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

        ProgressBar progressBar = (ProgressBar) tablesView.findViewById(R.id.tables_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        connectedUserId = getArguments().getString("connectedUserId");
        database = FirebaseDatabase.getInstance().getReference();
        //getArrayOfTables(getContext().getApplicationContext());
        arrayOfTables = new ArrayList<GameTable>();

        final ListView tablesList = (ListView)tablesView.findViewById(R.id.tables);
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
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
                final GameTable selectedTable = tablesListAdapter.getItem(pos);
                final Dialog tableOptionsDialog = new Dialog(getActivity());
                tableOptionsDialog.setContentView(R.layout.table_options);
                tableOptionsDialog.setTitle(selectedTable.getTableName());

                Button inviteButton = (Button) tableOptionsDialog.findViewById(R.id.table_option_invite_button);
                inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.child("usersInTables").child(selectedTable.getTableId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() >= 4) {
                                    Toast toast = new Toast(getContext());
                                    toast.setText("Sorry, there is no more room in this table!");
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }

                                final long newPlayerNumber = dataSnapshot.getChildrenCount() + 1;

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Title");

                                final EditText input = new EditText(getContext());
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                builder.setView(input);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        database.child("userNames").child(input.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null) {
                                                    Toast.makeText(getActivity(), "Sorry, there is no user with the selected name!", Toast.LENGTH_LONG).show();
                                                    return;
                                                }

                                                String playerId = dataSnapshot.getValue().toString();
                                                database.child("usersInTables").child(selectedTable.getTableId()).child(playerId).setValue("");
                                                database.child("tablesForUsers").child(playerId).child(selectedTable.getTableId()).setValue("");
                                                switch ((int) newPlayerNumber) {
                                                    case 1:
                                                        selectedTable.setPlayer1(playerId);
                                                        break;
                                                    case 2:
                                                        selectedTable.setPlayer2(playerId);
                                                        break;
                                                    case 3:
                                                        selectedTable.setPlayer3(playerId);
                                                        break;
                                                    case 4:
                                                        selectedTable.setPlayer4(playerId);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                updatePlayersInTables();
                                                tablesListAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(getActivity(), "Sorry, there is no user with the selected name!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                Button editButton = (Button) tableOptionsDialog.findViewById(R.id.table_option_edit_button);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditTableActivity.class);
                        intent.putExtra("EDIT_TABLE_INDEX", pos);
                        intent.putExtra("EDIT_TABLE", selectedTable.toJson());
                        startActivityForResult(intent, 0);
                    }
                });

                Button chatButton = (Button) tableOptionsDialog.findViewById(R.id.table_option_chat_button);
                chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("CONNECTED_USER_ID", connectedUserId);
                        intent.putExtra("TABLE_ID", selectedTable.getTableId());
                        startActivity(intent);
                    }
                });

                Button kickButton = (Button) tableOptionsDialog.findViewById(R.id.table_option_kick_button);
                kickButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog kickPlayerDialog = new Dialog(getActivity());
                        kickPlayerDialog.setContentView(R.layout.kick_user_buttons);
                        kickPlayerDialog.setTitle("Which player would you like to kick?");

                        Button kickPlayer1Button = (Button)kickPlayerDialog.findViewById(R.id.kick_player1_button);
                        Button kickPlayer2Button = (Button)kickPlayerDialog.findViewById(R.id.kick_player2_button);
                        Button kickPlayer3Button = (Button)kickPlayerDialog.findViewById(R.id.kick_player3_button);
                        Button kickPlayer4Button = (Button)kickPlayerDialog.findViewById(R.id.kick_player4_button);
                        Button kickCancelButton = (Button)kickPlayerDialog.findViewById(R.id.kick_player_cancel_button);

                        if (!selectedTable.getPlayer1().isEmpty() && !selectedTable.getPlayer1().equals(connectedUserId)) {
                            kickPlayer1Button.setText(selectedTable.getPlayer1UserName());
                            kickPlayer1Button.setVisibility(View.VISIBLE);
                        }
                        if (!selectedTable.getPlayer2().isEmpty() && !selectedTable.getPlayer2().equals(connectedUserId)) {
                            kickPlayer2Button.setText(selectedTable.getPlayer2UserName());
                            kickPlayer2Button.setVisibility(View.VISIBLE);
                        }
                        if (!selectedTable.getPlayer3().isEmpty() && !selectedTable.getPlayer3().equals(connectedUserId)) {
                            kickPlayer3Button.setText(selectedTable.getPlayer3UserName());
                            kickPlayer3Button.setVisibility(View.VISIBLE);
                        }
                        if (!selectedTable.getPlayer4().isEmpty() && !selectedTable.getPlayer4().equals(connectedUserId)) {
                            kickPlayer4Button.setText(selectedTable.getPlayer4UserName());
                            kickPlayer4Button.setVisibility(View.VISIBLE);
                        }

                        kickPlayer1Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                database.child("usersInTables").child(selectedTable.getTableId()).child(selectedTable.getPlayer1()).removeValue();
                                database.child("tablesForUsers").child(selectedTable.getPlayer1()).child(selectedTable.getTableId()).removeValue();
                                selectedTable.setPlayer1("");
                                updatePlayersInTables();
                                tablesListAdapter.notifyDataSetChanged();
                                kickPlayerDialog.cancel();
                            }
                        });

                        kickPlayer2Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                database.child("usersInTables").child(selectedTable.getTableId()).child(selectedTable.getPlayer2()).removeValue();
                                database.child("tablesForUsers").child(selectedTable.getPlayer2()).child(selectedTable.getTableId()).removeValue();
                                selectedTable.setPlayer2("");
                                updatePlayersInTables();
                                tablesListAdapter.notifyDataSetChanged();
                                kickPlayerDialog.cancel();
                            }
                        });

                        kickPlayer3Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                database.child("usersInTables").child(selectedTable.getTableId()).child(selectedTable.getPlayer3()).removeValue();
                                database.child("tablesForUsers").child(selectedTable.getPlayer3()).child(selectedTable.getTableId()).removeValue();
                                selectedTable.setPlayer3("");
                                updatePlayersInTables();
                                tablesListAdapter.notifyDataSetChanged();
                                kickPlayerDialog.cancel();
                            }
                        });

                        kickPlayer4Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                database.child("usersInTables").child(selectedTable.getTableId()).child(selectedTable.getPlayer4()).removeValue();
                                database.child("tablesForUsers").child(selectedTable.getPlayer4()).child(selectedTable.getTableId()).removeValue();
                                selectedTable.setPlayer4("");
                                updatePlayersInTables();
                                tablesListAdapter.notifyDataSetChanged();
                                kickPlayerDialog.cancel();
                            }
                        });

                        kickCancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                kickPlayerDialog.cancel();
                            }
                        });
                        kickPlayerDialog.show();
                    }
                });

                Button leaveButton = (Button) tableOptionsDialog.findViewById(R.id.table_option_leave_button);
                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Leave " + selectedTable.getTableName() + "?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                database.child("tablesForUsers").child(connectedUserId).child(selectedTable.getTableId()).removeValue();
                                database.child("usersInTables").child(selectedTable.getTableId()).child(connectedUserId).removeValue();

                                database.child("usersInTables").child(selectedTable.getTableId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() == 0) {
                                            database.child("tables").child(selectedTable.getTableId()).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                tablesListAdapter.remove(selectedTable);
                                tablesListAdapter.notifyDataSetChanged();
                                tableOptionsDialog.cancel();
                            }
                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();
                    }
                });

                Button cancelButton = (Button) tableOptionsDialog.findViewById(R.id.table_option_cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tableOptionsDialog.cancel();
                    }
                });



                /*final int itemId = (int) pos;
                final String tableName = tablesListAdapter.getItem((int)itemId).getTableName();
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
                return true;*/
                tableOptionsDialog.show();
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
        if (!(newTableJson == null) && !newTableJson.equals("")) {
            if (!newTableJson.equals("null") && !newTableJson.equals("")) {
                Gson gson = new Gson();
                GameTable newTable = gson.fromJson(newTableJson, GameTable.class);

                String newTableId = getNewTableId();
                newTable.setTableId(newTableId);
                database.child("tables").child(newTableId).child("name").setValue(newTable.getTableName());
                database.child("tables").child(newTableId).child("GameName").setValue(newTable.getGameName());
                database.child("tables").child(newTableId).child("when").setValue(newTable.getDate());
                database.child("tables").child(newTableId).child("where").setValue(newTable.getLocation());

                database.child("usersInTables").child(newTableId).child(connectedUserId).setValue("");
                database.child("tablesForUsers").child(connectedUserId).child(newTableId).setValue("");

                arrayOfTables.add(newTable);
                tablesListAdapter.notifyDataSetChanged();
            }
            return;
        }

        String editTableJson=data.getStringExtra("EDIT_TABLE");
        if (!(editTableJson == null) && !editTableJson.equals("")) {
            if (!editTableJson.equals("null") && !editTableJson.equals("")) {
                Gson gson = new Gson();
                GameTable editTable = gson.fromJson(editTableJson, GameTable.class);

                String tableId = editTable.getTableId();

                database.child("tables").child(tableId).child("name").setValue(editTable.getTableName());
                database.child("tables").child(tableId).child("GameName").setValue(editTable.getGameName());
                database.child("tables").child(tableId).child("when").setValue(editTable.getDate());
                database.child("tables").child(tableId).child("where").setValue(editTable.getLocation());

                database.child("usersInTables").child(tableId).child(connectedUserId).setValue("");
                database.child("tablesForUsers").child(connectedUserId).child(tableId).setValue("");

                int index = data.getExtras().getInt("EDIT_TABLE_INDEX");

                GameTable original = tablesListAdapter.getItem(index);
                original.setGameName(editTable.getGameName());
                original.setTableName(editTable.getTableName());
                original.setDate(editTable.getDate());
                original.setLocation(editTable.getLocation());
                tablesListAdapter.notifyDataSetChanged();
            }
            return;
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
