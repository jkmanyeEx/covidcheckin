package com.schoolvote.covidcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.Serializable;

public class MainMenuActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        user = (User) getIntent().getSerializableExtra("user");
        System.out.println(user.toString());
    }

    public void onReserveButtonClick(View view) {
        startActivity(new Intent(this, ReserveActivity.class).putExtra("user", (Serializable) user));
    }

    public void onAboutButtonClick(View view) {
        startActivity(new Intent(this, AboutActivity.class).putExtra("user", (Serializable) user));
    }

    public void onCheckinButtonClick(View view) {
        startActivity(new Intent(this, CheckinActivity.class).putExtra("user", (Serializable) user));
    }

    public void onCheckoutButtonClick(View view) {
        startActivity(new Intent(this, CheckoutActivity.class).putExtra("user", (Serializable) user));
    }

    public void onSelfCheckButtonClick(View view) {
        startActivity(new Intent(this, SelfCheckActivity.class).putExtra("user", (Serializable) user));
    }
}