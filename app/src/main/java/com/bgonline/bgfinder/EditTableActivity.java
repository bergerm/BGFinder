package com.bgonline.bgfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.bgonline.bgfinder.databinding.NewTableBinding;

public class EditTableActivity extends AppCompatActivity {

    GameTable table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewTableBinding binding = DataBindingUtil.setContentView(this, R.layout.new_table);

        String tableJson = getIntent().getExtras().getString("EDIT_TABLE");
        final int index = getIntent().getExtras().getInt("EDIT_TABLE_INDEX");
        table = GameTable.fromJson(tableJson);
        binding.setGameTable(table);

        Button saveTableButton = (Button) findViewById(R.id.new_table_save_button);
        saveTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTableJson = table.toJson();
                Intent intent = new Intent();
                intent.putExtra("EDIT_TABLE", newTableJson);
                intent.putExtra("EDIT_TABLE_INDEX", index);
                intent.putExtra("RESULT", "SUCCESS");
                setResult(0, intent);
                finish();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.putExtra("RESULT", "FAILURE");
                setResult(0, intent);
                finish();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
