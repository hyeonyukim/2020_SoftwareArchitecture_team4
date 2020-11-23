package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class RecommendActivity extends AppCompatActivity {
    ArrayList<Timetable> subjects;
    ArrayList<Long> timetable;
    final int maxRecommend = 100;
    int idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page6);
        generateTimetable background = new generateTimetable();
        background.execute();
    }

    private  class generateTimetable extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RecommendActivity.this);
            progressDialog.setMessage("추천 시간표 생성중...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            generate();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            idx=0;
            TextView index = (TextView)findViewById(R.id.P6index);
            index.setText((idx+1)+"/"+timetable.size());
            if(timetable.size()>idx)
                setTimetable(idx);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDBHelper dbHelper = new myDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM SelectedClass");
    }

    HashMap<Long, Boolean> possibileMap;
    protected void generate(){
        myDBHelper dbHelper = new myDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT o.subjectFullID, s.subjectName, o.time, s.credit, o.professor, o.maxStudent " +
                "FROM SelectedClass c, OpenedClass o, Subject s " +
                "WHERE c.subjectFullID = o.subjectFullID " +
                "AND o.subjectSubID = s.subjectID;", null);

        //데이터베이스에서 가져온 과목들을 집어넣는다.
        subjects = new ArrayList<Timetable>();
        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++){
            String fullID = cursor.getString(0);
            String name = cursor.getString(1);
            String time = cursor.getString(2);
            int credit = cursor.getInt(3);
            String professor = cursor.getString(4);
            int maxStudent = cursor.getInt(5);
            subjects.add(new Timetable(fullID, name, time, credit, professor, maxStudent));
            cursor.moveToNext();
        }

        timetable = new ArrayList<Long>();
        int maxSubjectCnt = fillMap();
        Log.i("maxSubjectCnt", maxSubjectCnt+"");
        maxSubjectCnt = (maxSubjectCnt>6)?6:maxSubjectCnt;
//        for(long i=0; i<Math.pow(2, subjects.size()); i++){
//            if(subjectCount(i, subjects.size())>=maxSubjectCnt) {
//                if (possibileMap.get(i)) {
//                    timetable.add(i);
//                }
//            }
//        }
        Iterator<Long> keySet = possibileMap.keySet().iterator();
        while(keySet.hasNext()){
            long key = keySet.next();
            if(possibileMap.get(key)&&subjectCount(key, subjects.size())>=maxSubjectCnt){
                timetable.add(key);
            }
        }
    }

    public int fillMap(){
        int max=0;
        possibileMap = new HashMap<Long, Boolean>();
        for(long i=0; i<Math.pow(2, subjects.size()); i++){
            int subjectCnt = subjectCount(i, subjects.size());
            if(subjectCnt<=10) {
                if (isPossible(i, subjects.size())) {
                    possibileMap.put(i, true);
                    if(subjectCnt>max)
                        max = subjectCnt;
                }else{
                    possibileMap.put(i, false);
                }
            }
        }
        return max;
    }

//    public int maxSubjectCount(long x, int length){
//        int cnt=subjectCount(x, length);
//        if(cnt == length) {
//            if (isPossible(x, length))
//                return cnt;
//        }
//        long temp = x;
//        for(int i=0; i<length; i++){
//            if(temp%2==1) {
//                if(isPossible(x-(long)Math.pow(2, i), length)) {
//                    return cnt - 1;
//                }
//            }
//            temp/=2;
//        }
//        cnt=0;
//        temp = x;
//        for(long i=0; i<length; i++){
//            if(temp%2==1) {
//                int partial = maxSubjectCount(x-(int)Math.pow(2, i), length);
//                if(cnt<partial)
//                    cnt = partial;
//            }
//            temp/=2;
//        }
//        return cnt;
//    }

    public int subjectCount(long x, int length){
        int cnt =0;
        for(int i=0; i<length; i++){
            if(x%2==1)
                cnt++;
            x/=2;
        }
        return cnt;
    }

    public boolean isPossible(long x, int length){
        ArrayList<Integer> subject = new ArrayList<>();
        for(int i=0; i<length; i++){
            if(x%2==1)
                subject.add(i);
            x/=2;
        }
        for(int i=0; i<subject.size(); i++){
            Timetable a = subjects.get(subject.get(i));
            StringTokenizer a_tok = new StringTokenizer(a.getTime(), " ");
            for(int j=i+1; j<subject.size(); j++){
                Timetable b = subjects.get(subject.get(j));
                if(b.getName().equals(a.getName()))
                    return false;
                while (a_tok.hasMoreTokens()){
                    String a_start = a_tok.nextToken();
                    a_tok.nextToken();
                    String a_end = a_tok.nextToken();
                    StringTokenizer b_tok = new StringTokenizer(b.getTime(), " ");
                    while(b_tok.hasMoreTokens()){
                        String b_start = b_tok.nextToken();
                        b_tok.nextToken();
                        String b_end = b_tok.nextToken();
                        if(a_start.charAt(0)==b_start.charAt(0)){
                            if(a_end.equals(b_end))
                                return false;
                            if(a_start.equals(b_start))
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    public void setTimetable(int index){
        TableLayout tableLayout =(TableLayout)findViewById(R.id.P6table);
        //tableLayout.setStretchAllColumns(false);
        TextView[] text;

        if(tableLayout.getChildCount()!=1){
            int rowCnt = tableLayout.getChildCount();
            for(int i=1; i<rowCnt; i++) {
                tableLayout.removeViewAt(1);
            }
        }

        int length = subjects.size();
        ArrayList<Integer> subject = new ArrayList<>();
        long x = timetable.get(index);
        for(int i=0; i<length; i++){
            if(x%2==1)
                subject.add(i);
            x/=2;
        }

        for(int j=0; j<subject.size(); j++) {
            TableRow row = new TableRow(getApplicationContext());
            text = new TextView[6];
            Timetable rowContent = subjects.get(subject.get(j));

            for(int i=0; i<text.length; i++) {
                text[i] = new TextView(getApplicationContext());
                text[i].setGravity(Gravity.CENTER);
                text[i].setBackground(getDrawable(R.drawable.tableborder));
//                text[i].setLayoutParams(new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            String fullID = rowContent.getCode();
            String name = rowContent.getName();
            String time = rowContent.getTime();
            int credit = rowContent.getCredit();
            String professor = rowContent.getProfessor();
            int maxStudent = rowContent.getMaxStudent();
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
        if(view.getId()==R.id.P6nextButton&&idx<timetable.size()-1){
            idx++;
        }
        if(view.getId()==R.id.P6previousButton&&idx>0){
            idx--;
        }
        index.setText((idx+1)+"/"+timetable.size());
        if(timetable.size()>idx)
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