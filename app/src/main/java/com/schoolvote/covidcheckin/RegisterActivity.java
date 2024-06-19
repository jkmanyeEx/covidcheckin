package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    public String id;
    public String pw;
    public String name;
    public String telnum;

    private int status;
    private boolean isConnectionFinished = false;
    private JSONObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onRegister(View view) {
        id = ((EditText) findViewById(R.id.rg_id)).getText().toString();
        pw = ((EditText) findViewById(R.id.rg_pw)).getText().toString();
        name = ((EditText) findViewById(R.id.rg_name)).getText().toString();
        telnum = ((EditText) findViewById(R.id.rg_telnum)).getText().toString();

        AsyncTask.execute(() -> {
            URL url;
            HttpURLConnection register = null;
            try {
                url = new URL("http://covidcheckin-database.herokuapp.com/register/" + id + "/" + pw + "/" + name + "/" + telnum);
                register = (HttpURLConnection) url.openConnection();

                register.setRequestMethod("POST");
                register.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                register.setDoOutput(true);

                System.out.println(register.getResponseCode());

                if (register.getResponseCode() == 200) {
                    RegisterActivity.this.status = 0;
                    InputStream responseBody = register.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBodyReader);
                    RegisterActivity.this.user = jsonObject;
                    System.out.println(jsonObject.toString());
                    register.disconnect();
                    RegisterActivity.this.isConnectionFinished  = true;
                } else if (register.getResponseCode() == 409) {
                    RegisterActivity.this.status = 1;
                    register.disconnect();
                    RegisterActivity.this.isConnectionFinished  = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("회원가입");
        System.out.println(status);

        while (true) {
            if(isConnectionFinished) {
                if (status == 0) {
                    builder.setMessage("회원가입 되었습니다.");
                    builder.setPositiveButton("확인", (dialog, id) -> {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    });
                    break;
                } else if (status == 1) {
                    builder.setMessage("이미 같은 이메일로 생성된 계정이 있습니다. 다시 시도하세요.");
                    builder.setPositiveButton("확인", (dialog, id) -> {

                    });
                    break;
                }
            } else continue;
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}