package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class RecommendActivity extends AppCompatActivity {

    ArrayList<Timetable>[] timetable;
    final int maxRecommend = 100;
    int timetableCnt;
    TextView[] text;
    int idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page6);
        timetableCnt = generate();
        idx=0;
        TextView index = (TextView)findViewById(R.id.P6index);
        index.setText((idx+1)+"/"+timetableCnt);
//        index.setText((idx+"/1000");
        setTimetable(idx);
    }
//    db.execSQL("CREATE TABLE Subject (subjectID TEXT PRIMARY KEY, subjectName TEXT, " +
//         "credit INTEGER);");
//    db.execSQL("CREATE TABLE OpenedClass (subjectFullID TEXT PRIMARY KEY, subjectSubID TEXT, " +
//            "gubun TEXT, professor TEXT, time TEXT, classroom TEXT, maxStudent INTEGER, " +
//            "FOREIGN KEY(subjectSubID) REFERENCES Subject(subjectID));");
//    db.execSQL("CREATE TABLE SelectedClass(subjectFullID TEXT PRIMARY KEY, " +
//                "FOREIGN KEY(subjectFullID) REFERENCES OpenedClass(subjectFullID));");
    protected int generate(){
        timetable = new ArrayList[maxRecommend];
        timetable[0] = new ArrayList<Timetable>();
        myDBHelper dbHelper = new myDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT o.subjectFullID, s.subjectName, o.time, s.credit, o.professor, o.maxStudent " +
                "FROM SelectedClass c, OpenedClass o, Subject s " +
                "WHERE c.subjectFullID = o.subjectFullID " +
                "AND o.subjectSubID = s.subjectID;", null);
        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++){
            String fullID = cursor.getString(0);
            String name = cursor.getString(1);
            String time = compressTime(cursor.getString(2));
            int credit = cursor.getInt(3);
            String professor = cursor.getString(4);
            int maxStudent = cursor.getInt(5);
            timetable[0].add(new Timetable(fullID, name, time, credit, professor, maxStudent));
            cursor.moveToNext();
        }
        return 1;
    }

    public void setTimetable(int index){
        TableLayout tableLayout =(TableLayout)findViewById(R.id.P6table);
        //tableLayout.setStretchAllColumns(false);

        if(text!=null){
            for(int i=0; i<text.length; i++) {
                tableLayout.removeView(text[i]);
            }
        }

        for(int j=0; j<timetable[index].size(); j++) {
            TableRow row = new TableRow(getApplicationContext());
            text = new TextView[6];
            Timetable subject = timetable[index].get(j);

            for(int i=0; i<text.length; i++) {
                text[i] = new TextView(getApplicationContext());
                text[i].setGravity(Gravity.CENTER);
                text[i].setBackground(getDrawable(R.drawable.tableborder));
//                text[i].setLayoutParams(new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            String fullID = subject.getCode();
            String name = subject.getName();
            String time = subject.getTime();
            int credit = subject.getCredit();
            String professor = subject.getProfessor();
            int maxStudent = subject.getMaxStudent();
            text[0].setText(""+ fullID +"");
            text[1].setText(""+ name +"");
            //text[2].setText(""+ time +"");
            text[3].setText(""+ credit +"");
            text[4].setText(""+ professor +"");
            text[5].setText(""+ maxStudent +"");

            for(int i=0; i<text.length; i++) {
                row.addView(text[i]);
            }
            row.setBackground(getDrawable(R.drawable.tableborder));
            tableLayout.addView(row);
        }
    }

    public void onClickButton(View view) {
        TextView index = (TextView)findViewById(R.id.P6index);
        if(view.getId()==R.id.P6nextButton&&idx<timetableCnt-1){
            idx++;
        }
        if(view.getId()==R.id.P6previousButton&&idx>0){
            idx--;
        }
        index.setText(idx+"/"+timetable.length);
        setTimetable(idx);
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
                res = res + date + startTime + "~" + endTime + "\n";
                date = token1.charAt(0);
                startTime = token1.substring(1);
                endTime = token2;
            }
        }
        res = res + date + startTime + "~" + endTime + "\n";
//        Log.i("res", res);
//        Log.i("time", time);
        return res;
    }
}
class Timetable{
    String code;
    //교과구분
    String name;
    String time;
    int credit;
    String professor;
    int maxStudent;

    public Timetable(String code, String name, String time,
            int credit, String professor, int maxStudent){
        this.code = code;
        this.credit = credit;
        this.name = name;
        this.professor = professor;
        this.time = time;
        this.maxStudent = maxStudent;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public int getMaxStudent() {
        return maxStudent;
    }

    public void setMaxStudent(int maxStudent) {
        this.maxStudent = maxStudent;
    }
}