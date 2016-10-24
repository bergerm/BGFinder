package com.bgonline.bgfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GamesFragment extends SyncrhonizedLoadFragment {
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onUpdateGamesArray(ArrayList<String> games);
    }

    private ArrayList<String> arrayOfGames;

    private static final String TAG = "BGFinderGamesFragment";

    public static GamesFragment newInstance() {
        return new GamesFragment();
    }

    public GamesFragment() {
    }

    public ArrayList<String> getArrayOfGames(Context context) {
        if (arrayOfGames != null) {
            return arrayOfGames;
        }

        String json = "";
        try {
            InputStream inputStream = context.openFileInput("arrayOfGames.json");

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
            arrayOfGames = gson.fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        else {
            arrayOfGames = new ArrayList<String>();
        }

        return arrayOfGames;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getArrayOfGames(getContext().getApplicationContext());

        View gameListView = inflater.inflate(R.layout.games_list, null);

        ListView list = (ListView) gameListView.findViewById(R.id.list_of_games);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1 , arrayOfGames);
        list.setAdapter(adapter);

        Button addGameButton = (Button) gameListView.findViewById(R.id.game_list_add_game);
        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add new game!");

                // Set up the input
                final EditText input = new EditText(getContext().getApplicationContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayOfGames.add(input.getText().toString());
                        Collections.sort(arrayOfGames, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                return s1.compareToIgnoreCase(s2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                try {
                    builder.show();
                }
                catch (Exception e) {
                    //LOG ERROR
                }
            }
        });

        final FrameLayout mainLayout = (FrameLayout)container.getRootView();
        Button importGamesButton = (Button) gameListView.findViewById(R.id.game_list_import_games);
        importGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handleNavigateNotImplemented(mainLayout,null);
            }
        });

        ListView listView = (ListView) gameListView.findViewById(R.id.list_of_games);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int itemId = (int) pos;
                String gameName = arrayOfGames.get((int)itemId);
                String dialogString = "Remove " + gameName + "?";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(dialogString);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayOfGames.remove(itemId);
                        Collections.sort(arrayOfGames, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                return s1.compareToIgnoreCase(s2);
                            }
                        });
                        adapter.notifyDataSetChanged();
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

        return gameListView;
    }

    private void saveGames() {
        Gson gson = new Gson();
        String json = gson.toJson(arrayOfGames);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().getApplicationContext().openFileOutput("arrayOfGames.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Error opening gamesFile for write.");
        }

        mCallback.onUpdateGamesArray(arrayOfGames);
    }

    @Override
    public void onPause() {
        saveGames();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        saveGames();
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
