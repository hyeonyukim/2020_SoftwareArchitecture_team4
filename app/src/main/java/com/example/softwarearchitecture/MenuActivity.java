package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page4);

        final ImageButton P4Summary = (ImageButton) findViewById(R.id.P4Summary);
        P4Summary.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent P4SummaryIntent = new Intent(MenuActivity.this, SummaryActivity.class);
                        startActivity(P4SummaryIntent);
                    }
                }
        );

        final ImageButton P4Recommend = (ImageButton) findViewById(R.id.P4Recommend);
        P4Recommend.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent P4RecommendIntent = new Intent(MenuActivity.this, ClassSelectActivity.class);
                        startActivity(P4RecommendIntent);
                    }
                }
        );
    }
}