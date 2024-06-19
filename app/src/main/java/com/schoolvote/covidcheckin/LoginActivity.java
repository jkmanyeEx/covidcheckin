package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.JsonReader;
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
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    public String id;
    public String pw;
    public int status;
    public JSONObject user;

    private boolean isConnectionFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonClick(View view) {
        id = ((EditText) findViewById(R.id.li_id)).getText().toString();
        pw = ((EditText) findViewById(R.id.li_pw)).getText().toString();

        System.out.println(id);
        System.out.println(pw);

        AsyncTask.execute(() -> {
            URL url;
            HttpURLConnection login = null;
            try {
                url = new URL("http://covidcheckin-database.herokuapp.com/login/" + id + "/" + pw);
                login = (HttpURLConnection) url.openConnection();

                System.out.println(login.getResponseCode());

                if (login.getResponseCode() == 200) {
                    LoginActivity.this.status = 0;
                    InputStream responseBody = login.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBodyReader);
                    LoginActivity.this.user = jsonObject;
                    System.out.println(jsonObject.toString());
                    login.disconnect();
                    LoginActivity.this.isConnectionFinished  = true;
                } else if (login.getResponseCode() == 404) {
                    LoginActivity.this.status = 1;
                    login.disconnect();
                    LoginActivity.this.isConnectionFinished  = true;
                } else if (login.getResponseCode() == 400) {
                    LoginActivity.this.status = 2;
                    login.disconnect();
                    LoginActivity.this.isConnectionFinished  = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("로그인");
        System.out.println(status);

        while (true) {
            if(isConnectionFinished) {
                if (status == 0) {
                    builder.setMessage("로그인되었습니다.");
                    builder.setPositiveButton("확인", (dialog, id) -> {
                        User user = new User();
                        user.setId((String) LoginActivity.this.user.get("email"));
                        user.setTelnum((String) LoginActivity.this.user.get("telnum"));
                        user.setName((String) LoginActivity.this.user.get("name"));

                        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                        intent.putExtra("user", (Serializable) user);
                        startActivity(intent);
                        finish();
                    });

                    break;
                } else if (status == 1) {
                    builder.setMessage("존재하지 않는 사용자입니다. 아이디를 확인해주세요.");
                    builder.setPositiveButton("확인", (dialog, id) -> {

                    });
                    break;
                } else if (status == 2) {
                    builder.setMessage("잘못된 비밀번호입니다.");
                    builder.setPositiveButton("확인", (dialog, id) -> {

                    });
                    break;
                }
            } else continue;
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onRegisterButtonClick (View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}