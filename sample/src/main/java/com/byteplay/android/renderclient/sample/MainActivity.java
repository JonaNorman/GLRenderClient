package com.byteplay.android.renderclient.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Class> classList = new ArrayList<>();

    public MainActivity() {
        super();
        classList.add(RenderClientActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListView();
    }

    private void initListView() {
        ListView listView = findViewById(R.id.listView);
        List<String> nameList = new ArrayList<>();
        for (Class classItem : classList) {
            nameList.add(classItem.getSimpleName());
        }
        listView.setAdapter(new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                nameList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, classList.get(position)));
            }
        });
    }
}