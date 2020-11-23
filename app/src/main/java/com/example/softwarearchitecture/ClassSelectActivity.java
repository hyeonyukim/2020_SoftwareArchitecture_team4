package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.softwarearchitecture.databinding.ActivityClassSelectBinding;
import com.example.softwarearchitecture.databinding.ItemBinding;

import java.sql.Time;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClassSelectActivity extends AppCompatActivity{
    ItemBinding binding;
    ActivityClassSelectBinding parentbinding;
    ArrayList<String> subjectIDs;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_select);
        myDBHelper dbHelper = new myDBHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT o.subjectFullID, s.subjectName, s.credit, o.time, o.maxStudent, o.professor\n" +
                "FROM Subject s, OpenedClass o, Curriculum c\n" +
                "WHERE s.subjectID = o.subjectSubID\n" +
                "AND s.subjectID NOT IN (SELECT subjectID From LearnedClass)\n" +
                "AND s.subjectID = c.subjectID;", null);
        result.moveToFirst();
        subjectIDs = new ArrayList<String>();

        //스피너 항목 추가.
        Spinner spinner = (Spinner)findViewById(R.id.P7spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.credits, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                credits = i + 5;
                //Log.i("Spinner", credits + "과목");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                credits = 5;
            }
        });

        do {
            final String subjectFullID = result.getString(0);
            String subjectName = result.getString(1);
            int credit = result.getInt(2);
            String time = compressTime(result.getString(3));
            int maxStudent = result.getInt(4);
            String professor = result.getString(5);
            subjectIDs.add(subjectFullID);

            final LinearLayout body = (LinearLayout) findViewById(R.id.P7body);
            final LinearLayout item=(LinearLayout)LayoutInflater.from(ClassSelectActivity.this)
                    .inflate(R.layout.item, null);
            TextView title = (TextView)item.findViewById(R.id.ItemtextView1);
            TextView subscript = (TextView)item.findViewById(R.id.ItemtextView2);
            TextView subscript2 = (TextView)item.findViewById(R.id.ItemtextView3);
            Button deleteButton = (Button)item.findViewById(R.id.ItemNoButton);
            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    body.removeView(item);
                    subjectIDs.remove(subjectFullID);
//                    System.out.println("DELTED");
//                    for(int i=0; i<subjectIDs.size(); i++){
//                        Log.i(i+"", subjectIDs.get(i));
//                    }
                }
            });
            title.setText(subjectName+"("+subjectFullID+")");
            subscript.setText("교수:" + professor + ", 정원:" + maxStudent + "명, " + credit + "학점");
            subscript2.setText("강의 시간: " + time);
            body.addView(item);
        }while(result.moveToNext());
    }

    int credits;

    public void onClickButton(View view) {
        for(int i=0; i<subjectIDs.size(); i++){
            db.execSQL("INSERT INTO SelectedClass VALUES('"+subjectIDs.get(i)+"')");
        }
        Intent P7RecommendIntent = new Intent(ClassSelectActivity.this, RecommendActivity.class);
        P7RecommendIntent.putExtra("credits", credits);
        Log.i("Spinner", credits + "과목");
        startActivity(P7RecommendIntent);
    }



    public String compressTime(String time){
        String res="";
        StringTokenizer stk = new StringTokenizer(time, " ");
        char date = 0;
        String startTime=null, endTime=null;

        while(stk.hasMoreTokens()){
            String token1 = stk.nextToken();
            stk.nextToken();
            String token2 = stk.nextToken();
            //처음인 경우
            if(startTime==null&&endTime==null&&date==0){
                date = token1.charAt(0);
                startTime = token1.substring(1);
                endTime = token2;
            }
            else if(date==token1.charAt(0) && endTime.equals(token1.substring(1))){
                endTime = token2;
            }
            else{
                res = res + date + startTime + "~" + endTime + " ";
                date = token1.charAt(0);
                startTime = token1.substring(1);
                endTime = token2;
            }
        }
        res = res + date + startTime + "~" + endTime + " ";
//        Log.i("res", res);
//        Log.i("time", time);
        return res;
    }
}