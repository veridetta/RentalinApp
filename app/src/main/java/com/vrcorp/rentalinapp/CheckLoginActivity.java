package com.vrcorp.rentalinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class CheckLoginActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String kode_login, id;
    Boolean session=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_login);
        //SharedPreferences.Editor editor = sharedpreferences.edit();
        //editor.putBoolean("session_status", true);
        sharedpreferences = getSharedPreferences("rentalinApp", Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean("session_status", false);
        if(session){

            Intent sudahLogin = new Intent(CheckLoginActivity.this, MainActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, message);
            Toast.makeText(CheckLoginActivity.this, "Selamat Datang Kembali",
                    Toast.LENGTH_LONG).show();
            startActivity(sudahLogin);

        }else{
            Intent belumLogin = new Intent(CheckLoginActivity.this, LoginActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, message);
            belumLogin.putExtra("CEK_LOGIN", "tidak");
            Toast.makeText(CheckLoginActivity.this, "Harap Login Dahulu!",
                    Toast.LENGTH_LONG).show();
            startActivity(belumLogin);
        }
    }
}