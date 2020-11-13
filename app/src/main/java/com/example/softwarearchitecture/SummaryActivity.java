package com.example.softwarearchitecture;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page5);
        myDBHelper dbHelper = new myDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT Average_Score FROM Student", null);
        Cursor learn_table = db.rawQuery("SELECT l.yearsemester, l.gubun, l.subjectID, s.subjectName, s.credit, l.score FROM LearnedClass l, Subject s WHERE l.subjectID=s.subjectID", null);
        HashMap<Double, Double> score45_table = new HashMap<>();


        if (result.moveToFirst()) {
            double score43 = result.getDouble(0);
            TextView p5score43 = (TextView) findViewById(R.id.P5score43);
            p5score43.setText("학점 :  " + score43 + "/4.3");

            int score43_integer = (int) score43;//4.3학점의 정수부분
            double score43_double = score43 - (double) score43_integer;//4.3학점의 소수부분
            double score43_double_cal = 0;
            score43_double = Math.round(score43_double * 100) / 100.0;
            if (score43_double < 0.3) {//소수부분이 0.3보다 작을 경우
                score43_double_cal = score43_double + 1;
            } else if (score43_double >= 0.3) {//소수부분이 0.3보다 큰 경우
                score43_double_cal = score43_double;
            }
            putMap(score45_table);
            double score45_double = score45_table.get(score43_double_cal);//4.3 학점의 소수부분을 토대로 4.5학점으로 변환

            double score45 = 0.0; //초기값 설정

            if (score43_double < 0.3) {//0.3보다 작을 경우
                score45 = score45_double + score43_integer - 1;
            } else if (score43_double >= 0.3) {//0.3보다 큰 경우
                score45 = score45_double + score43_integer;
            }
            TextView p5score45 = (TextView) findViewById(R.id.P5score45);
            p5score45.setText("학점 :  " + score45 + "/4.5");
        }

        learn_table.moveToFirst();

        do {
            String year = learn_table.getString(0);
            TextView p5_year = (TextView) findViewById(R.id.sub_Year);
            p5_year.setText(""+ year +"");

            String distinct = learn_table.getString(1);
            TextView p5_distinct = (TextView) findViewById(R.id.sub_Gubun);
            p5_distinct.setText(""+ distinct +"");

            String code = learn_table.getString(2);
            TextView p5_code = (TextView) findViewById(R.id.sub_Code);
            p5_code.setText(""+ code +"");

            String name = learn_table.getString(3);
            TextView p5_name = (TextView) findViewById(R.id.sub_Name);
            p5_name.setText(""+ name +"");


            int credit = learn_table.getInt(4);
            TextView p5_credit = (TextView) findViewById(R.id.sub_Credit);
            p5_credit.setText(""+ credit +"");

            String score = learn_table.getString(5);
            TextView p5_score = (TextView) findViewById(R.id.sub_Score);
            p5_score.setText(""+ score +"");

        } while(learn_table.moveToNext());
    }

    protected void putMap(HashMap<Double, Double> score45_table){
        score45_table.put(0.3, 0.5);
        score45_table.put(0.31, 0.51);
        score45_table.put(0.32, 0.52);
        score45_table.put(0.33, 0.54);
        score45_table.put(0.34, 0.55);
        score45_table.put(0.35, 0.56);
        score45_table.put(0.36, 0.57);
        score45_table.put(0.37, 0.59);
        score45_table.put(0.38, 0.60);
        score45_table.put(0.39, 0.61);
        score45_table.put(0.40, 0.62);
        score45_table.put(0.41, 0.64);
        score45_table.put(0.42, 0.65);
        score45_table.put(0.43, 0.66);
        score45_table.put(0.44, 0.67);
        score45_table.put(0.45, 0.69);
        score45_table.put(0.46, 0.70);
        score45_table.put(0.47, 0.71);
        score45_table.put(0.48, 0.72);
        score45_table.put(0.49, 0.74);
        score45_table.put(0.50, 0.75);
        score45_table.put(0.51, 0.76);
        score45_table.put(0.52, 0.77);
        score45_table.put(0.53, 0.79);
        score45_table.put(0.54, 0.80);
        score45_table.put(0.55, 0.81);
        score45_table.put(0.56, 0.82);
        score45_table.put(0.57, 0.84);
        score45_table.put(0.58, 0.85);
        score45_table.put(0.59, 0.86);
        score45_table.put(0.60, 0.87);
        score45_table.put(0.61, 0.89);
        score45_table.put(0.62, 0.90);
        score45_table.put(0.63, 0.91);
        score45_table.put(0.64, 0.92);
        score45_table.put(0.65, 0.94);
        score45_table.put(0.66, 0.95);
        score45_table.put(0.67, 0.96);
        score45_table.put(0.68, 0.97);
        score45_table.put(0.69, 0.99);
        score45_table.put(0.70, 1.00);
        score45_table.put(0.71, 1.01);
        score45_table.put(0.72, 1.02);
        score45_table.put(0.73, 1.03);
        score45_table.put(0.74, 1.03);
        score45_table.put(0.75, 1.04);
        score45_table.put(0.76, 1.05);
        score45_table.put(0.77, 1.06);
        score45_table.put(0.78, 1.07);
        score45_table.put(0.79, 1.08);
        score45_table.put(0.80, 1.08);
        score45_table.put(0.81, 1.09);
        score45_table.put(0.82, 1.10);
        score45_table.put(0.83, 1.11);
        score45_table.put(0.84, 1.12);
        score45_table.put(0.85, 1.13);
        score45_table.put(0.86, 1.13);
        score45_table.put(0.87, 1.14);
        score45_table.put(0.88, 1.15);
        score45_table.put(0.89, 1.16);
        score45_table.put(0.90, 1.17);
        score45_table.put(0.91, 1.18);
        score45_table.put(0.92, 1.18);
        score45_table.put(0.93, 1.19);
        score45_table.put(0.94, 1.20);
        score45_table.put(0.95, 1.21);
        score45_table.put(0.96, 1.22);
        score45_table.put(0.97, 1.23);
        score45_table.put(0.98, 1.23);
        score45_table.put(0.99, 1.24);
        score45_table.put(1.00, 1.25);
        score45_table.put(1.01, 1.26);
        score45_table.put(1.02, 1.27);
        score45_table.put(1.03, 1.28);
        score45_table.put(1.04, 1.28);
        score45_table.put(1.05, 1.29);
        score45_table.put(1.06, 1.30);
        score45_table.put(1.07, 1.31);
        score45_table.put(1.08, 1.32);
        score45_table.put(1.09, 1.33);
        score45_table.put(1.10, 1.33);
        score45_table.put(1.11, 1.34);
        score45_table.put(1.12, 1.35);
        score45_table.put(1.13, 1.36);
        score45_table.put(1.14, 1.37);
        score45_table.put(1.15, 1.38);
        score45_table.put(1.16, 1.38);
        score45_table.put(1.17, 1.39);
        score45_table.put(1.18, 1.40);
        score45_table.put(1.19, 1.41);
        score45_table.put(1.20, 1.42);
        score45_table.put(1.21, 1.43);
        score45_table.put(1.22, 1.43);
        score45_table.put(1.23, 1.44);
        score45_table.put(1.24, 1.45);
        score45_table.put(1.25, 1.46);
        score45_table.put(1.26, 1.47);
        score45_table.put(1.27, 1.48);
        score45_table.put(1.28, 1.48);
        score45_table.put(1.29, 1.49);
    }
}