package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = findViewById(R.id.ab_tv);
        User user = (User) getIntent().getSerializableExtra("user");

        textView.setText(
                "이메일: " + "\n" + user.getId() + "\n" + "\n"
                + "전화번호: " + "\n" + user.getTelnum() + "\n" + "\n"
                + "실명: " + "\n" + user.getName()
        );
    }
}