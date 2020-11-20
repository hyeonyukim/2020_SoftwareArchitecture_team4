package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {

    ArrayList<String>[] timetable;
    int idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page6);
        generate();
        idx=1;
        TextView index = (TextView)findViewById(R.id.P6index);
        index.setText(idx+"/"+timetable.length);
        setTimetable(idx);

    }

    public void setTimetable(int index){
        TableLayout tableLayoout =(TableLayout)findViewById(R.id.P6table);

        for(int i=0; i<timetable[index].size(); i++) {
            TableRow row = new TableRow(getApplicationContext());
            TextView text = new TextView(getApplicationContext());

            row.addView(text);
            tableLayoout.addView(row);
        }

    }

    protected void generate(){

    }

    public void onClickButton(View view) {
        TextView index = (TextView)findViewById(R.id.P6index);
        if(view.getId()==R.id.P6nextButton&&idx<timetable.length){
            idx++;
        }
        if(view.getId()==R.id.P6previousButton&&idx>1){
            idx--;
        }
        index.setText(idx+"/"+timetable.length);
        setTimetable(idx);
    }
}