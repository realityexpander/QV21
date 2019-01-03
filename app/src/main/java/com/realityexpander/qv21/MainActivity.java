package com.realityexpander.qv21;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;

    List<String[]> wellList;
    public static final int EDIT_WELL_ROW_REQUEST = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.item_layout);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),ScrollingActivity.class);
                intent.putExtra( "wellListRow", wellList.get(i));
                intent.putExtra("index", i);

                Log.d("CDA:", Arrays.toString(wellList.get(i)) );
                startActivityForResult(intent, EDIT_WELL_ROW_REQUEST);
            }
        });

        // Need to load data the first time?
        if (wellList == null) {
            InputStream inputStream = getResources().openRawResource(R.raw.welldata);
            WellDataCSVFile wellDataCSVFile = new WellDataCSVFile(inputStream);
            wellList = wellDataCSVFile.read();

            for (String[] wellData : wellList) {
                itemArrayAdapter.add(wellData);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CDA onActivityResult:", requestCode + " " + resultCode + " " + data);

        if (requestCode == EDIT_WELL_ROW_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String[] wellListRow = bundle.getStringArray("wellListRow");
                int wellListIdx = bundle.getInt("index");

                Log.d("CDA in onActivityResult wellListRow=", Arrays.toString(wellListRow) );

                // TODO copy all the items back into our database array
                wellList.set(wellListIdx, wellListRow);
                Log.d("CDA in onActivityResult wellList=", Arrays.toString(wellList.get(wellListIdx)) );

                itemArrayAdapter.clear();
                for (String[] wellData : wellList) {
                    itemArrayAdapter.add(wellData);
                }
//                itemArrayAdapter.notifyDataSetChanged();
                listView.setAdapter(itemArrayAdapter);

            }
        }
    }
}