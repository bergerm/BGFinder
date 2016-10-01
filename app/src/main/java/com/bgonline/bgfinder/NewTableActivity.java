package com.bgonline.bgfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.bgonline.bgfinder.databinding.NewTableBinding;
import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.Button;

public class NewTableActivity extends AppCompatActivity {

    GameTable newTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewTableBinding binding = DataBindingUtil.setContentView(this, R.layout.new_table);

        newTable = new GameTable(NewTableActivity.this, 0);
        binding.setGameTable(newTable);

        Button saveTableButton = (Button) findViewById(R.id.new_table_save_button);
        saveTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTableJson = newTable.toJson();
                Intent intent = new Intent();
                intent.putExtra("NEW_TABLE", newTableJson);
                setResult(0, intent);
                finish();
            }
        });
    }
}
