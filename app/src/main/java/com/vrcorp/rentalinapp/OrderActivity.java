package com.vrcorp.rentalinapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.vrcorp.rentalinapp.app.AppController;
import com.vrcorp.rentalinapp.server.Url;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    private ActionBar toolbar;
    Button btn_simpan;
    EditText cekin, cekout, namaMobil;
    TextView totalHarga;
    ImageView img_cekin, img_cekout;
    String idMobil, idPartner, harga, string_id, merkMobil, hari;
    SharedPreferences sharedpreferences;
    DateTime dcekin, dcekout;
    int success;
    ProgressDialog pDialog;
    private static final String TAG = OrderActivity.class.getSimpleName();
    private String url = Url.URL + "buatorderan.php";
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        toolbar = getSupportActionBar();
        toolbar.hide();
        btn_simpan=findViewById(R.id.btn_simpan);
        cekin = findViewById(R.id.cekin);
        cekout = findViewById(R.id.cekout);
        namaMobil = findViewById(R.id.nama);
        img_cekin= findViewById(R.id.img_cekin);
        img_cekout= findViewById(R.id.img_cekout);
        sharedpreferences = getSharedPreferences("rentalinApp", Context.MODE_PRIVATE);
        string_id = sharedpreferences.getString("id", null);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        idMobil=b.getString("id");
        idPartner=b.getString("alamat");
        harga=b.getString("harga");
        merkMobil=b.getString("merekMobil");
        namaMobil.setText(merkMobil);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        img_cekin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekinSet();
            }
        });
        img_cekout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekoutSet();
            }
        });
        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-YYYY");
                 dcekin = dtf.parseDateTime(cekin.getText().toString());
                dcekout = dtf.parseDateTime(cekout.getText().toString());
                int days = Days.daysBetween(dcekin, dcekout).getDays();
                final Integer totalHarga =  Integer.parseInt(harga)*days;
                //format harga
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                symbols.setDecimalSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
                String prezzo = decimalFormat.format(totalHarga);
                new FancyGifDialog.Builder(OrderActivity.this)
                        .setTitle("Total Harga "+prezzo)
                        .setMessage("Silahkan klik OK untuk melanjutkan")
                        .setNegativeBtnText("Cancel")
                        .setPositiveBtnBackground("#FF4081")
                        .setPositiveBtnText("Ok")
                        .setNegativeBtnBackground("#FFA9A7A8")
                        .setGifResource(R.drawable.money)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                checkLogin(idMobil,idPartner, String.valueOf(totalHarga),string_id,cekin.getText().toString(),
                                        cekout.getText().toString());
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                Toast.makeText(OrderActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
            }
        });
    }
    private void checkLogin(final String xidMobil, final String xidPartner,
                            final String xtotalHarga, final String xid, final String xcekin,
                            final String xcekout) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Mengubah ...");
        pDialog.show();
        Log.e(TAG, "Register Response: "+url+xidMobil+xidPartner+xid+xcekin+xcekout+xtotalHarga);
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
                        //Log.e("Successfully Register!", jObj.toString());
                        Toast.makeText(OrderActivity.this,
                                "Order Berhasil dibuat.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(OrderActivity.this,
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
                Toast.makeText(OrderActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idMobil", xidMobil);
                params.put("idPartner", xidPartner);
                params.put("totalHarga", xtotalHarga);
                params.put("cekin", xcekin);
                params.put("cekout", xcekout);
                params.put("idUser", xid);
                return params;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }
    private void cekinSet(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                cekin.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void cekoutSet(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                cekout.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
