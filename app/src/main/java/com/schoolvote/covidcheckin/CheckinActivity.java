package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckinActivity extends AppCompatActivity {

    private String placeCode;
    private User user;
    private SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss.SSS");
    private boolean isConnectionFinished = false;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

         editText = findViewById(R.id.ci_et);

        user = (User) getIntent().getSerializableExtra("user");
    }

    public void finishState() {
        Toast.makeText(this, "체크인 되었습니다.", Toast.LENGTH_LONG).show();
        finish();
    }

    public void onCheckin (View view) {
        AsyncTask.execute(() -> {
            URL url;
            HttpURLConnection checkIn = null;

            placeCode = editText.getText().toString();
            try {
                url = new URL("https://covidcheckin-database.herokuapp.com/checkin/" + placeCode + "/" + user.getId() + "/" + sDate.format(new Date()));
                checkIn = (HttpURLConnection) url.openConnection();

                checkIn.setRequestMethod("POST");
                checkIn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                checkIn.setDoOutput(true);

                System.out.println(checkIn.getResponseCode());
                checkIn.disconnect();
                isConnectionFinished = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("체크인");

        while (true) {
            if (isConnectionFinished) {
                builder.setMessage("체크인되었습니다.");
                builder.setPositiveButton("확인", (dialog, id) -> {
                    Intent intent = new Intent(this, MainMenuActivity.class);
                    intent.putExtra("user", (Serializable) user);
                    startActivity(intent);
                    finish();
                });
                break;
            }
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}