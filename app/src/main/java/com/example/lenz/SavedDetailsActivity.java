package com.example.lenz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SavedDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_details);

        DBHandler dbHandler = new DBHandler(this);
        ArrayList<HashMap<String,String>> scannedTextList = dbHandler.getDetails();
        ListView lv = (ListView) findViewById(R.id.text_list);

        ListAdapter adapter = new SimpleAdapter(SavedDetailsActivity.this, scannedTextList, R.layout.list_row,new String[]{"title","content","time"}, new int[]{R.id.title, R.id.content,R.id.time});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String content = scannedTextList.get(position).get("content");
                String title = scannedTextList.get(position).get("title");
                String time = scannedTextList.get(position).get("time");
                String pos = String.valueOf(position);
                Intent updateIntent = new Intent(SavedDetailsActivity.this,EditActivity.class);
                updateIntent.putExtra("recognizedText",content);
                updateIntent.putExtra("title",title);
                updateIntent.putExtra("time",time);
                updateIntent.putExtra("upd","1");
                Log.i("test",pos);
                startActivity(updateIntent);
                Log.i("testAfter",pos);

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SavedDetailsActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

}