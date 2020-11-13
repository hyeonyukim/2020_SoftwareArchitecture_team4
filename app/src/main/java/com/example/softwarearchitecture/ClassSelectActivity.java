package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ClassSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_select);
//        TextView a = new TextView("")
    }

    public void onClickButton(View view) {
        Intent P7RecommendIntent = new Intent(ClassSelectActivity.this, RecommendActivity.class);
        startActivity(P7RecommendIntent);
    }
}