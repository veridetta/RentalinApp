package com.vrcorp.rentalinapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vrcorp.rentalinapp.app.AppController;
import com.vrcorp.rentalinapp.server.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button btnLogin, btnReg;
    ProgressDialog pDialog;
    private String url = Url.URL + "register.php";
    boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    int success;
    EditText nama, nohp, alamat, email, password;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        sharedpreferences = getSharedPreferences("rentalinApp", Context.MODE_PRIVATE);
        nama = findViewById(R.id.nama);
        nohp=findViewById(R.id.no_hp);
        alamat=findViewById(R.id.alamat);
        email=findViewById(R.id.email_input);
        password=findViewById(R.id.password_input);
        btnLogin=findViewById(R.id.btn_login);
        btnReg=findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent belumLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                belumLogin.putExtra("CEK_LOGIN", "tidak");
                Toast.makeText(RegisterActivity.this, "Harap Login Dahulu!",
                        Toast.LENGTH_LONG).show();
                //SharedPreferences.Editor editor = sharedpreferences.edit();
                //editor.putBoolean("session_status", true);
                startActivity(belumLogin);
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tnama, tnohp, talamat, temail, tpassword;;
                tnama=nama.getText().toString();
                tnohp=nohp.getText().toString();
                talamat=alamat.getText().toString();
                temail=email.getText().toString();
                tpassword=password.getText().toString();
                if(tnama.isEmpty() ||tnohp.isEmpty()||talamat.isEmpty()|| temail.isEmpty()||temail.isEmpty()||tpassword.isEmpty()){
                    Toast.makeText(v.getContext(),"Data tidak boleh kosong",Toast.LENGTH_LONG).show();
                }else{
                    checkLogin(tnama,talamat,tnohp,temail,tpassword);
                }
            }
        });
    }
    // ------ FUNCTION CEK LOGIN ---------------
    private void checkLogin(final String xnama, final String xalamat,
                            final String xnohp, final String xemail, final String xpassword) {
        pDialog = new ProgressDialog(RegisterActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Registering in ...");
        pDialog.show();
        Log.e(TAG, "Register Response: "+url);
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response string: " + response.toString());
                pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    // Check for error node in json
                    success = jObj.getInt("success");
                    if (success == 1) {
                        String id = jObj.getString("id");
                        Log.e("Successfully Register!", jObj.toString());
                        nama.setText("");
                        nohp.setText("");
                        alamat.setText("");
                        email.setText("");
                        password.setText("");
                        Toast.makeText(getApplicationContext(),
                              "Registrasi berhasil, silahkan login.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    Log.wtf(TAG, e.toString());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama", xnama);
                params.put("alamat", xalamat);
                params.put("nohp", xnohp);
                params.put("email", xemail);
                params.put("password", xpassword);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }
    /// -----------------
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
