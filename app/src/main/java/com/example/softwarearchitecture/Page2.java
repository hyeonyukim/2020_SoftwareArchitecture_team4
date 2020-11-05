package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
        ProgressDialog progressDialog;

        protected void onPreExecute(){
            progressDialog = new ProgressDialog(Page2.this);
            progressDialog.setMessage("학생정보 로딩중...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progressDialog.show();
        }

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
            progressDialog.dismiss();
            //백그라운드 스레드로부터 성공 및 실패 여부를 String으로 받아온다.
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
            String knuSessionURL = "http://my.knu.ac.kr";
            String yearSemesterURL = "http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/chkSearchYrTrm.action?search_gubun=&_=";
            String MojorSectionURL = "http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/listCrseCdes2.action?search_open_bndle_cde=1&search_gubun=1&_=";
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
                int last_col=0;
                Elements rowElement = avgGradeInfo.select("#certRecStatsGrid_"+last_col);
                while(rowElement.size()!=0){
                    last_col++;
                    rowElement = avgGradeInfo.select("#certRecStatsGrid_"+last_col);
                }
                double avgGrade = Double.parseDouble(avgGradeInfo.select("#certRecStatsGrid_"+(last_col-1)).select(".grd_avg").text());
                db.execSQL("INSERT INTO Student values(" + sid + ", '" + College + "', '" + Major + "', '" + name + "', " + grade + ", " + avgGrade + ");");

                //학생의 이수과목 및 성적을 볼 수 있는 url에 연결하고 이를 DB의 learnedClass table에 추가함.
                Document learned = Jsoup.connect(subjectURL).method(Connection.Method.GET).cookies(cookies).execute().parse();
                int code = 0;
                //각 수강 과목을 가져와 db의 table들에 추가한다. 더 이상 수강 과목이 없을 때까지 반복한다.
                while (true) {
                    String id = "#certRecEnqGrid_" + code;
                    Elements tableRow = learned.select(id);
                    if (tableRow.size() <= 0)
                        break;
                    //데이터를 가져와 subject table에 추가함.
                    String subjectID = tableRow.select(".subj_cde").attr("currentvalue");
                    String subjectName = tableRow.select(".subj_cnm").attr("currentvalue");
                    int credit = Integer.parseInt(tableRow.select(".unit").attr("currentvalue"));
                    db.execSQL("INSERT OR REPLACE INTO Subject VALUES('" + subjectID + "', '" + subjectName + "', " + credit + ");");
                    //데이터를 가져와 learnedClass table에 추가함.
                    String yearSemester = tableRow.select(".yr_trm").attr("currentvalue");
                    String gubun = tableRow.select(".subj_div_cde").attr("currentvalue");
                    String score = tableRow.select(".rec_rank_cde").attr("currentvalue");
                    db.execSQL("INSERT INTO LearnedClass VALUES('" + subjectID + "', " + sid + ", '" + yearSemester + "', '" + gubun + "', " +
                            "'" + score + "');");
                    code++;
                }

                //다음 학기에 개설되는 수업들의 목록을 가져와 DB의 OpenedClass table에 추가함.
                //knu사이트의 기본 쿠키들을 가져온다.
                response = Jsoup.connect(knuSessionURL).method(Connection.Method.GET).execute();
                cookies = response.cookies();
                //현재 학기가 무엇인지 알아온다. 다음 학기의 시간표를 짜기 위해서는 여기 결과에 한 학기를 추가하도록 수정해야함.
                Document yearSemesterDoc = Jsoup.connect(yearSemesterURL).requestBody("JSON").cookies(cookies).ignoreContentType(true).post();
                String present_year_trm = yearSemesterDoc.body().text().replaceAll("'", "");
                //각 전공에 따르는 고유 번호들을 가져와서 그 중 사용자와 일치하는 고유 번호를 저장한다.
                //전공과 일치하는 항목은 1개 이상일 수 있다. 예를 들어 컴퓨터학과는 컴퓨터학과는 플랫폼 소프트웨어 전공 등 세부 전공이 있기 때문이다.
                //따라서 전공의 이름을 포함하는 모든 전공들의 코드를 저장해야 한다.
                Document MajorSectionDoc = Jsoup.connect(MojorSectionURL).requestBody("JSON").cookies(cookies).ignoreContentType(true).post();
                JSONArray jsonArray = new JSONArray(MajorSectionDoc.body().text());
                ArrayList<Integer> codeList = new ArrayList<Integer>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String crse_nm = jsonObject.getString("open_crse_nm_1");
                    if (crse_nm.contains(Major)) {
                        codeList.add(i);
                    }
                }
                for (int i = 0; i < codeList.size(); i++) {
                    int index = codeList.get(i);
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    String crse_cde = jsonObject.getString("open_crse_cde_1");
                    String rn = jsonObject.getString("rn");
                    openedClassURL = "http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?" +
                            "search_open_crse_cde=" + crse_cde + "&sub=" + rn + "&search_open_yr_trm=" + present_year_trm;
                    Document openedClassDoc = Jsoup.connect(openedClassURL).method(Connection.Method.GET).execute().parse();
                    Elements classInfo = openedClassDoc.select("tr");
                    Elements subjecID = classInfo.select(".th4");
                    Elements gubun = classInfo.select(".th2");
                    Elements credit = classInfo.select(".th6");
                    Elements professor = classInfo.select(".th9");
                    Elements time = classInfo.select(".th17");
                    Elements classroom = classInfo.select(".th11");
                    Elements maxStudent = classInfo.select(".th12");
                    Elements subjectName = classInfo.select(".th5");
                    for (int j = 1; j < subjecID.size(); j++) {
                        db.execSQL("INSERT OR REPLACE INTO Subject VALUES('" + subjecID.get(j).text().substring(0, 7) + "', '" + subjectName.get(j).text() + "', " + credit.get(j).text() + ");");
                        db.execSQL("INSERT OR REPLACE INTO OpenedClass VALUES('" + subjecID.get(j).text() + "', '" + subjecID.get(j).text().substring(0, 7) + "', '" + gubun.get(j).text() + "', " +
                                "'" + professor.get(j).text() + "', '" + time.get(2 * j).text() + "', '" + classroom.get(j).text() + "', " + maxStudent.get(j).text() + ");");
                    }
                }

                //해당 학과의 커리큘럼을 가져와 DB의 Curriculum table에 추가함
                //커리큘럼 검색에 사용되는 정보는 학번 및 전공의 코드 번호인데, 이는 이전의 jsonArray와 codeList를 이용해 알 수 있음.
                for (int i = 0; i < codeList.size(); i++) {
                    int index = codeList.get(i);
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    String crse_cde = jsonObject.getString("open_crse_cde_1");
                    curriculumURL = "http://yes.knu.ac.kr/cour/curr/curriculum/listCurr/listCurrs.action?listCurr.search_tgt_yr="+sid/1000000+"&listCurr.search_crse_cde="+crse_cde+"&_=";
                    Document CurriculumDoc = Jsoup.connect(curriculumURL).method(Connection.Method.GET).execute().parse();

                    Element requirement = CurriculumDoc.select(".courTable").get(2);
                    System.out.println(requirement.html());
//                    requirement = requirement.selectFirst("[rowspan]");
//                    while(requirement.html().length()>0){
//                        requirement.f
//                    }
//                    System.out.println(elective.html());
                    //System.out.println(elective.html());
                }

            } catch (IOException | JSONException e) {
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