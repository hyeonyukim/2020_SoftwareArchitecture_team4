package com.example.softwarearchitecture;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//DB를 생성하고 초기화하는 DB생성자 정의
public class myDBHelper extends SQLiteOpenHelper {
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
                "gubun TEXT, grade INTEGER, professor TEXT, time TEXT, classroom TEXT, maxStudent INTEGER, " +
                "FOREIGN KEY(subjectSubID) REFERENCES Subject(subjectID));");
        db.execSQL("CREATE TABLE LearnedClass (subjectID TEXT, sID INTEGER, " +
                "yearSemester TEXT, gubun TEXT, score TEXT, " +
                "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
                "PRIMARY KEY(subjectID, sID));");
        db.execSQL("CREATE TABLE Curriculum (subjectID TEXT, sID INTEGER, " +
                "yearSemester TEXT, " +
                "FOREIGN KEY(subjectID) REFERENCES Subject(subjectID), " +
                "PRIMARY KEY(subjectID, sID));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //이곳에선 테이블이 존재하면 없애고 새로 만들어준다.
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Subject");
        db.execSQL("DROP TABLE IF EXISTS OpenedClass");
        db.execSQL("DROP TABLE IF EXISTS LearnedClass");
        db.execSQL("DROP TABLE IF EXISTS Curriculum");
        onCreate(db);
    }
}