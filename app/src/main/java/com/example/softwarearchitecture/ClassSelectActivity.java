package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClassSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_select);
        myDBHelper dbHelper = new myDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT o.subjectFullID, s.subjectName, s.credit, o.time, o.maxStudent, o.professor\n" +
                "FROM Subject s, OpenedClass o, Curriculum c\n" +
                "WHERE s.subjectID = o.subjectSubID\n" +
                "AND s.subjectID NOT IN (SELECT subjectID From LearnedClass)\n" +
                "AND s.subjectID = c.subjectID;", null);

        LinearLayout body = (LinearLayout)findViewById(R.id.P7body);
        result.moveToFirst();
        do {
            String subjectFullID = result.getString(0);
            String subjectName = result.getString(1);
            int credit = result.getInt(2);
            String time = result.getString(3);
            int maxStudent = result.getInt(4);
            String professor = result.getString(5);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout leftside = new LinearLayout(this);
            leftside.setOrientation(LinearLayout.VERTICAL);
            leftside.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            linearLayout.addView(leftside);

            LinearLayout rightside = new LinearLayout(this);
            rightside.setOrientation(LinearLayout.HORIZONTAL);
            leftside.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            linearLayout.addView(leftside);




//            Log.i("fullid", subjectFullID);
//            Log.i("name", subjectName);
//            Log.i("credit", credit+"");
//            Log.i("time", time);
//            Log.i("maxStudent", maxStudent+"");

        }while(result.moveToNext());
    }

    public void onClickButton(View view) {
        Intent P7RecommendIntent = new Intent(ClassSelectActivity.this, RecommendActivity.class);
        startActivity(P7RecommendIntent);
    }
}