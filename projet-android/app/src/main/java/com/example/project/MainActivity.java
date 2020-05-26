package com.example.project;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.bt1);
        Button btn2 = (Button) findViewById(R.id.bt2);

        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.bt1):
                Intent playIntent = new Intent(this, SecondActivity.class);
                playIntent.putExtra("name", "Mon NOM");
                startActivity(playIntent);
                break;
            case (R.id.bt2):
                    Button btn2 = (Button) findViewById(R.id.bt2);
                    btn2.setText("Why u curious ?");
                    btn2.setOnClickListener(this);
                }


    }
}
