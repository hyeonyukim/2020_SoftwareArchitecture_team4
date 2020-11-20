package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {

    Timetable[] timetable;
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
        TableLayout tableLayout =(TableLayout)findViewById(R.id.P6table);

        for(int j=0; j<timetable.length; j++) {
            TableRow row = new TableRow(getApplicationContext());
            TextView[] text = new TextView[6];
            Timetable subject = timetable[j];

            for(int i=0; i<text.length; i++) {
                text[i] = new TextView(getApplicationContext());
                text[i].setGravity(Gravity.CENTER);
                text[i].setBackground(getDrawable(R.drawable.tableborder));
            }
            //내용 채우기
            String year = subject.getYear();
            text[0].setText(""+ year +"");
            String distinct = subject.getDistinct();
            text[1].setText(""+ distinct +"");
            String code =subject.getCode();
            text[2].setText(""+ code +"");
            String name = subject.getName();
            text[3].setText(""+ name +"");
            int credit = subject.getCredit();
            text[4].setText(""+ credit +"");
            String score = subject.getScore();
            text[5].setText(""+ score +"");

            for(int i=0; i<text.length; i++) {
                row.addView(text[i]);
            }
            row.setBackground(getDrawable(R.drawable.tableborder));
            tableLayout.addView(row);
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

    //DB를 생성하고 초기화하는 DB생성자 정의
    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context){
            super(context, "groupDB", null, 39);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            //groupTBL이라는 테이블이름으로 gName, gNumber 필드를 생성해주자
            db.execSQL("PRAGMA foreign_keys=ON");
            db.execSQL("CREATE TABLE Student (sID INTEGER PRIMARY KEY, College TEXT, " +
                    "Major TEXT, Name TEXT, Grage INTEGER, Average_Score REAL);");
            db.execSQL("CREATE TABLE Subject (subjectID TEXT PRIMARY KEY, subjectName TEXT, " +
                    "credit INTEGER);");
            db.execSQL("CREATE TABLE OpenedClass (subjectFullID TEXT PRIMARY KEY, subjectSubID TEXT, " +
                    "gubun TEXT, professor TEXT, time TEXT, classroom TEXT, maxStudent INTEGER, " +
                    "FOREIGN KEY(subjectSubID) REFERENCES Subject(subjectID));");
            db.execSQL("CREATE TABLE LearnedClass (subjectID TEXT, sID INTEGER, " +
                    "yearSemester TEXT, gubun TEXT, score TEXT, " +
                    "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
                    "FOREIGN KEY(sID) REFERENCES Student(sID), " +
                    "PRIMARY KEY(subjectID, sID, yearSemester));");
            db.execSQL("CREATE TABLE Curriculum (subjectID TEXT, sID INTEGER, " +
                    "yearSemester TEXT, " +
                    "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
                    "PRIMARY KEY(subjectID, sID));");
            db.execSQL("CREATE TABLE SelectedClass(subjectFullID TEXT PRIMARY KEY, " +
                    "FOREIGN KEY(subjectFullID) REFERENCES OpenedClass(subjectFullID));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //이곳에선 테이블이 존재하면 없애고 새로 만들어준다.
            db.execSQL("DROP TABLE IF EXISTS Student");
            db.execSQL("DROP TABLE IF EXISTS Subject");
            db.execSQL("DROP TABLE IF EXISTS OpenedClass");
            db.execSQL("DROP TABLE IF EXISTS LearnedClass");
            db.execSQL("DROP TABLE IF EXISTS Curriculum");
            db.execSQL("DROP TABLE IF EXISTS SelectedClass");
            onCreate(db);
        }
    }
}

//    String year = learn_table.getString(0);
//            text[0].setText(""+ year +"");
//                    String distinct = learn_table.getString(1);
//                    text[1].setText(""+ distinct +"");
//                    String code = learn_table.getString(2);
//                    text[2].setText(""+ code +"");
//                    String name = learn_table.getString(3);
//                    text[3].setText(""+ name +"");
//                    int credit = learn_table.getInt(4);
//                    text[4].setText(""+ credit +"");
//                    String score = learn_table.getString(5);
//                    text[5].setText(""+ score +"");

class Timetable{
    String year;
    //교과구분
    String distinct;
    String code;
    String name;
    int credit;
    String score;
    public Timetable(String year, String distinct, String code, String name,
                     int credit, String score){
        this.code = code;
        this.credit = credit;
        this.distinct = distinct;
        this.name = name;
        this.score = score;
        this.year = year;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDistinct() {
        return distinct;
    }

    public void setDistinct(String distinct) {
        this.distinct = distinct;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}