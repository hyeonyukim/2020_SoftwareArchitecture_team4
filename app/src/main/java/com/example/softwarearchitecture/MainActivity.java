package com.example.softwarearchitecture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button P1Login = (Button) findViewById(R.id.P1Login);
        P1Login.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent P1LoginIntent = new Intent(MainActivity.this, Page2.class);
                        startActivity(P1LoginIntent);
                    }
                }
        );
    }

}