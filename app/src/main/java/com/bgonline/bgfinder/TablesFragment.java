package com.bgonline.bgfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class TablesFragment extends SyncrhonizedLoadFragment {
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onUpdateTablesArray(ArrayList<GameTable> tables);
    }

    private ArrayList<GameTable> arrayOfTables;
    private TablesListAdapter tablesListAdapter;

    private static final String TAG = "BGFinderTablesFragment";

    public static TablesFragment newInstance() {
        return new TablesFragment();
    }

    public TablesFragment() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View tablesView = inflater.inflate(R.layout.tables_list, container, false);
        getArrayOfTables(getContext().getApplicationContext());

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
                String tableName = arrayOfTables.get((int)itemId).getTableName();
                String dialogString = "Remove " + tableName + "?";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(dialogString);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayOfTables.remove(itemId);
                        tablesListAdapter.notifyDataSetChanged();
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
    }
}
