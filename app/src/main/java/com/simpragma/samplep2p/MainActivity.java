package com.simpragma.samplep2p;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cloudant.p2p.listener.ApplicationUtil;
import com.cloudant.sync.datastore.DocumentException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button saveButton;
    Button refresh;
    EditText text;
ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveButton = (Button)findViewById(R.id.save);
        refresh = (Button)findViewById(R.id.load);
        text = (EditText)findViewById(R.id.text);
        list = (ListView)findViewById(R.id.listView);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO dao = new DAO();
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("name",text.getText().toString());
                try {
                    dao.save(map);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO dao = new DAO();
                List<String> names = dao.getAll();
                String[] namesArray = names.toArray(new String[names.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, namesArray);
                // Assign adapter to ListView
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        try {
            Log.d("TAGGED","Starting server");
            ApplicationUtil.startServer(getApplicationContext());
            Log.d("TAGGED", "Started server");
            ApplicationUtil.startSync("http://192.168.2.78:8182/"+ApplicationUtil.DBNAME);
            Log.d("TAGGED", "Repl done");
        } catch (Exception e) {
            Log.e("TAGGED","Error is",e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
