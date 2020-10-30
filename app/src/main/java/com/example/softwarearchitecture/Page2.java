package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Page2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        //로그인 버튼을 눌렀을 때
        final Button P2Login = (Button) findViewById(R.id.P2Login);
        P2Login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //네트워크 연결 상태 확인
                        if(haveNetworkConnection(getApplicationContext())){
                            //비동기 클래스를 사용해 사용자 관련 정보들을 가져온다.
                            FetchItemTask ft = new FetchItemTask();
                            ft.execute();
                        }
                        else{

                        }
                    }
                }
        );
    }

    //네트워크와 통신하는 과정이므로 비동기 방식을 사용해야 함.
    private  class FetchItemTask extends AsyncTask<Void, Void, String> {
        String loginAction = "https://yes.knu.ac.kr/comm/comm/support/login/login.action";
        Connection.Response response;
        Document doc;

        //백그라운드에서 실행하는 내용
        @Override
        protected String doInBackground(Void... voids) {
            try {
                //버튼으로부터 입력한 아이디와 비밀번호를 가져온다.
                EditText idbtn = (EditText) findViewById(R.id.P2ID);
                EditText pwdbtn = (EditText) findViewById(R.id.P2Password);
                String id = idbtn.getText().toString();
                String pwd = pwdbtn.getText().toString();

                //Get the login form with the cookies
                Connection.Response loginForm = Jsoup.connect(loginAction)
                        .method(Connection.Method.GET).timeout(10000).execute();

                //Fill the id, pwd and the other things.
                //Request login
                String url = "https://yes.knu.ac.kr/comm/comm/support/login/login.action?" +
                        "user.usr_id=" + id +
                        "&user.passwd=" + URLEncoder.encode(pwd) +
                        "&user.user_div=&user.stu_persnl_nbr=";
                response = Jsoup.connect(url).method(Connection.Method.GET).execute();
                doc = response.parse();

                //로그인에 성공한다면 class=box01인 div를 찾을 수 있다.
                //해당 div에는 (이름, 학번)형태의 text가 저장되어 있다.(홈페이지 상단에 위치)
                Elements identity = doc.select(".box01");

                //로그인에 성공한 경우
                if (identity.size() > 0) {
                    //이름과 학번을 분리해서 저장한다.
                    StringTokenizer tk = new StringTokenizer(identity.text(), "(/)");
                    tk.nextToken();
                    String name = tk.nextToken().replaceAll(" ", "");
                    int sid = Integer.parseInt(tk.nextToken().replaceAll(" ", ""));
                    //로그인 세션을 이용해 학생 정보및 수업 정보들을 DB에 저장한다.
                    saveUserInfo(name, sid);
                    return "Success";
                }
                //로그인에 실패한 경우
                else {
                    return "Fail";
                }
            } catch (IOException e) {
                Log.i("fail", "fail");
                e.printStackTrace();
            }
            return "Fail";
        }

        //포그라운드에서 실행하는 내용
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //백그라운드 스레드로부터 성공 및 실패 여부를 String으로 받아온다.
            Log.i("post", s);
            //로그인과 모든 정보를 저장하는 데 성공한 경우
            if (s.equals("Success")) {
                //다음 페이지로 넘어감.
                Intent P2LoginIntent = new Intent(Page2.this, Page4.class);
                startActivity(P2LoginIntent);
            }
            //실패한 경우
            else {
                Toast.makeText(getApplicationContext(),
                        "사용자 정보가 잘못되었습니다.\n 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }

//        db.execSQL("CREATE TABLE Subject (subjectID TEXT PRIMARY KEY, subjectName TEXT, " +
//                "credit INTEGER);");
//        db.execSQL("CREATE TABLE OpenedClass (subjectFullID TEXT PRIMARY KEY, subjectSubID TEXT, " +
//                "gubun TEXT, grade INTEGER, professor TEXT, time TEXT, classroom TEXT, maxStudent INTEGER, " +
//                "FOREIGN KEY(subjectSubID) REFERENCES Subject(subjectID));");
//        db.execSQL("CREATE TABLE LearnedClass (subjectID TEXT, sID INTEGER, " +
//                "yearSemester TEXT, gubun TEXT, score TEXT, " +
//                "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
//                "PRIMARY KEY(subjectID, sID));");
//        db.execSQL("CREATE TABLE Curriculum (subjectID TEXT, sID INTEGER, " +
//                "yearSemester TEXT, " +
//                "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
//                "PRIMARY KEY(subjectID, sID));");

        //로그인에 성공하면 성공한 세션을 이용해 학생 정보를 가져와 DB에 저장한다.
        protected void saveUserInfo(String name, int sid){
            //학생의 정보를 받아오는 url
            String subjectURL = "https://yes.knu.ac.kr/cour/scor/certRec/certRecEnq/listCertRecEnqs.action?" +
                    "certRecEnq.recDiv=1&id=certRecEnqGrid&columnsProperty=certRecEnqColumns&rowsProperty=certRecEnqs&" +
                    "emptyMessageProperty=certRecEnqNotFoundMessage&viewColumn=yr_trm%2Csubj_div_cde%2Csubj_cde%2Csubj_nm%2Cunit%2Crec_rank_cde&" +
                    "checkable=false&showRowNumber=false&paged=false&serverSortingYn=false&lastColumnNoRender=false&_=";
            String studentURL =  "https://yes.knu.ac.kr/stud/stud/infoMngt/basisMngt/listBasisMngts.action?" +
                    "basisMngt.stu_nbr="+sid+"&id=basisMngtGrid&columnsProperty=basisMngtColumns&rowsProperty=basisMngts&emptyMessageProperty=basisMngtNotFoundMessage&" +
                    "viewColumn=null&checkable=false&showRowNumber=false&paged=false&serverSortingYn=false&lastColumnNoRender=false&_=";
            String avgGradeURL = "https://yes.knu.ac.kr/cour/scor/certRec/certRecEnq/listCertRecStatses.action?" +
                    "certRecStats.recDiv=1&certRecStats.gubun=2&id=certRecStatsGrid&columnsProperty=certRecStatsColumns&rowsProperty=certRecStatses&" +
                    "emptyMessageProperty=certRecStatsNotFoundMessage&viewColumn=%2Cgrd_avg&checkable=false&showRowNumber=false&paged=false&serverSortingYn=false&lastColumnNoRender=false&_=";
            String openedClassURL = "";
            String curriculumURL = "";

            //DB를 생성한다. 이미 이전에 쓰이던 DB가 있으면 삭제하고 다시 만든다.
            myDBHelper dbHelper = new myDBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.onUpgrade(db, 39, 39);
            try {
                //로그인 세션은 쿠키에 저장되어 있으므로 이를 저장해둔다.
                Map<String, String> cookies = response.cookies();

                //학생의 기본 정보를 볼 수 있는 url및 성적을 확인할 수 있는 url에 연결하고, DB의 Student table에 추가함.
                Document basicInfo = Jsoup.connect(studentURL).method(Connection.Method.GET).cookies(cookies).execute().parse();
                int grade = Integer.parseInt(basicInfo.select(".gde_cde").select("[currentvalue]").attr("currentvalue"));
                StringTokenizer tok = new StringTokenizer(basicInfo.select(".pstn_crse_cde_nm2").select("[currentvalue]").attr("currentvalue"), " ");
                String College = tok.nextToken();
                String Major = tok.nextToken();

                Document avgGradeInfo = Jsoup.connect(avgGradeURL).method(Connection.Method.GET).cookies(cookies).execute().parse();
                double avgGrade = Double.parseDouble(avgGradeInfo.select("#certRecStatsGrid_6").select(".grd_avg").text());
                db.execSQL("INSERT INTO Student values("+sid+", '" + College + "', '" + Major + "', '" + name + "', " + grade + ", " + avgGrade + ");");

                //학생의 이수과목 및 성적을 볼 수 있는 url에 연결하고 이를 DB의 learnedClass table에 추가함.
                Document learned = Jsoup.connect(subjectURL).method(Connection.Method.GET).cookies(cookies).execute().parse();
                int code=0;
                //각 수강 과목을 가져와 db의 table들에 추가한다. 더 이상 수강 과목이 없을 때까지 반복한다.
                while(true){
                    String id = "cerRecEnqGrid_" + code;
                    Elements tableRow = learned.select(id);
                    if(tableRow.size()<=0)
                        break;

//                    db.execSQL("CREATE TABLE Subject (subjectID TEXT PRIMARY KEY, subjectName TEXT, " +
//                            "credit INTEGER);");
//                    db.execSQL("CREATE TABLE LearnedClass (subjectID TEXT, sID INTEGER, " +
//                            "yearSemester TEXT, gubun TEXT, score TEXT, " +
//                            "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
//                            "PRIMARY KEY(subjectID, sID));");

                    //데이터를 가져와 subject table에 추가함.
                    String subjectID = tableRow.select(".subj_cde").attr("currentvalue");
                    String subjectName = tableRow.select(".subj_cnm").attr("currentvalue");
                    int credit = Integer.parseInt(tableRow.select(".unit").attr("currentvalue"));
                    db.execSQL("INSERT INTO Subject VALUES('"+subjectID+"', '"+subjectName +"', "+credit+");");

                    //데이터를 가져와 learnedClass table에 추가함.
                    String yearSemester = tableRow.select(".yr_trm").attr("currentvalue");
                    String gubun = tableRow.select(".subj_div_cde").attr("currentvalue");
                    String score = tableRow.select(".rec_rank_cde").attr("currentvalue");
                    db.execSQL("INSERT INTO LearnedClass VALUES('"+subjectID+"', "+sid+", '"+yearSemester+"', '"+gubun+"', " +
                            ""+score+");");
                    code++;
                }

                System.out.println(learned.html());

                //다음 학기에 개설되는 수업들의 목록을 가져와 DB의 OpenedClass table에 추가함.

                //해당 학과의 커리큘럼을 가져와 DB의 Curriculum table에 추가함

            } catch (IOException e) {
                Log.i("get_fail", "Getting student info has failed");
                e.printStackTrace();
            }
        }
    }

    //네트워크 연결 상태 확인 함수
    protected boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                haveConnectedWifi = ni.isConnected();
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                haveConnectedMobile = ni.isConnected();
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}