package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.simple.JSONArray;
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

public class CheckoutActivity extends AppCompatActivity {

    private LinearLayout containerLinear;
    private User user;
    private boolean isListConnectionFinished = false;
    private boolean isCheckoutConnectionFinished = false;
    private JSONArray checkoutList = null;
    private SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss.SSS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        containerLinear = findViewById(R.id.ln_co);
        user = (User) getIntent().getSerializableExtra("user");

        AsyncTask.execute(() -> {
            URL url;
            HttpURLConnection checkOut = null;
            try {
                url = new URL("http://covidcheckin-database.herokuapp.com/query/checkoutquery/" + user.getId() + "/a");
                checkOut = (HttpURLConnection) url.openConnection();
                System.out.println(checkOut.getResponseCode());
                InputStream responseBody = checkOut.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(responseBodyReader);
                System.out.println(jsonArray.toString());
                checkOut.disconnect();
                checkoutList = jsonArray;
                CheckoutActivity.this.isListConnectionFinished = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        while (true) {
            if(isListConnectionFinished) {
                System.out.println(checkoutList.toString());
                for (int i = 0; i < checkoutList.toArray().length; i++) {
                    TextView textView = new TextView(CheckoutActivity.this);
                    textView.setText((String) ((JSONObject) (checkoutList.toArray()[i])).get("place"));
                    textView.setTextSize(48);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            String text = ((TextView) v).getText().toString();
                            JSONObject curCheckoutJson = null;
                            for (int i = 0; i < checkoutList.toArray().length; i++) {
                                System.out.println(checkoutList.toArray()[i].toString());
                                if ((((JSONObject) (checkoutList.toArray()[i])).get("place")) == text) {
                                    curCheckoutJson = ((JSONObject) (checkoutList.toArray()[i]));
                                }
                            }

                            JSONObject finalCurCheckoutJson = curCheckoutJson;
                            AsyncTask.execute(() -> {
                                URL url;
                                HttpURLConnection checkIn = null;
                                try {
                                    url = new URL("https://covidcheckin-database.herokuapp.com/checkout/" + (String) finalCurCheckoutJson.get("place") + "/" + (String) finalCurCheckoutJson.get("id") + "/" + (String) finalCurCheckoutJson.get("checkinTime") +  "/" + sDate.format(new Date()));
                                    checkIn = (HttpURLConnection) url.openConnection();

                                    checkIn.setRequestMethod("POST");
                                    checkIn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                    checkIn.setDoOutput(true);

                                    System.out.println(checkIn.getResponseCode());
                                    checkIn.disconnect();
                                    isCheckoutConnectionFinished = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                            builder.setTitle("체크아웃");

                            while (true) {
                                if (isCheckoutConnectionFinished) {
                                    builder.setMessage("체크아웃 되었습니다.");
                                    builder.setPositiveButton("확인", (dialog, id) -> {
                                        Intent intent = new Intent(CheckoutActivity.this, MainMenuActivity.class);
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
                    });

                    if ((boolean) ((JSONObject) checkoutList.toArray()[i]).get("isCheckedOut")) {
                        containerLinear.addView(textView);
                    }
                }
                break;
            } else continue;
        }
    }


}