package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SelfCheckActivity extends AppCompatActivity {

    private TextView textView;

    private int questionIndex = -1;
    private boolean[] questionCheckLists = new boolean[4];
    private String[] questions = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_check);

        textView = findViewById(R.id.sc_ques);

        questions[0] = "37.5도 이상의 발열이 있으십니까?";
        questions[1] =  "기침이나 재채기를 자주 하십니까?";
        questions[2] = "콧물 등의 호흡기 증상이 있습니까?";
        questions[3] = "오한 증세가 나타납니까?";

        reloadText();
    }

    @SuppressLint("SetTextI18n")
    private void reloadText() {
        questionIndex++;
        if (questionIndex < 4) {
            textView.setText(questions[questionIndex]);
        } else if (questionIndex == 4) {
            int yesCount = 0;
            for (int i = 0; i < 4; i++) {
                if (questionCheckLists[i])
                    yesCount++;
            }

            textView.setText("현재 4개의 문항 중 " + yesCount + "개의 문항에 해당한다고 표시하셨습니다. \n\n Yes > 선별진료소 예약 화면 \n No > 메인 화면");
        }
    }

    public void onYesButtonClick (View view) {
        if (questionIndex < 4) {
            questionCheckLists[questionIndex] = true;
            reloadText();
        } else if (questionIndex == 4) {
            startActivity(new Intent(this,  ReserveActivity.class));
        }
    }

    public void onNoButtonClick (View view) {
        if (questionIndex < 4) {
            questionCheckLists[questionIndex] = false;
            reloadText();
        } else if (questionIndex == 4) {
            startActivity(new Intent(this,  MainMenuActivity.class).putExtra("user", ((User) getIntent().getSerializableExtra("user"))));
        }
    }
}