package com.vrcorp.rentalinapp.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vrcorp.rentalinapp.LoginActivity;
import com.vrcorp.rentalinapp.MainActivity;
import com.vrcorp.rentalinapp.R;
import com.vrcorp.rentalinapp.RegisterActivity;
import com.vrcorp.rentalinapp.app.AppController;
import com.vrcorp.rentalinapp.server.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AkunFragment extends Fragment {
    EditText namaPartner, namaPemilik, nohp, alamat, password;
    ProgressDialog pDialog;
    private String url = Url.URL + "ubahprofil.php";
    private String urlGet = Url.URL + "getprofil.php";
    private static final String TAG = AkunFragment.class.getSimpleName();
    SharedPreferences sharedpreferences;
    int success;
    String gnamaPartner, gnohp, galamat, gpassword, string_id;
    Button btn_simpan, btn_logout;
    public AkunFragment() {
        // Required empty public constructor
    }

    public static AkunFragment newInstance(String param1, String param2) {
        AkunFragment fragment = new AkunFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);
        namaPartner=view.findViewById(R.id.nama);
        nohp=view.findViewById(R.id.no_hp);
        alamat=view.findViewById(R.id.alamat);
        password=view.findViewById(R.id.password_input);
        btn_simpan=view.findViewById(R.id.btn_simpan);
        btn_logout=view.findViewById(R.id.btn_logout);
        sharedpreferences = getActivity().getSharedPreferences("rentalinApp", Context.MODE_PRIVATE);
        string_id = sharedpreferences.getString("id", null);
        getProfile(string_id);
        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tnamaPartner, tnamaPemilik, tnohp, talamat, temail, tpassword;;
                tnamaPartner=namaPartner.getText().toString();
                tnamaPemilik=namaPemilik.getText().toString();
                tnohp=nohp.getText().toString();
                talamat=alamat.getText().toString();
                tpassword=password.getText().toString();
                if(tnamaPartner.isEmpty() ||tnohp.isEmpty()||talamat.isEmpty()){
                    Toast.makeText(v.getContext(),"Data tidak boleh kosong",Toast.LENGTH_LONG).show();
                }else{
                    checkLogin(tnamaPartner,talamat,tnohp,tpassword,string_id);
                }
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("session_status", false);
                editor.commit();
                Toast.makeText(getContext(),
                        "Berhasil Logout ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;

    }

    // ------ FUNCTION CEK LOGIN ---------------
    private void checkLogin(final String xnamaPartner, final String xalamat,
                            final String xnohp, final String xpassword, final String xid) {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Mengubah ...");
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
                        //Log.e("Successfully Register!", jObj.toString());
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("session_status", false);
                        editor.commit();
                        Toast.makeText(getContext(),
                                "Registrasi berhasil, silahkan login.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(),
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
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("namaPartner", xnamaPartner);
                params.put("alamat", xalamat);
                params.put("nohp", xnohp);
                params.put("password", xpassword);
                params.put("id", xid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }
    private void getProfile(final String xid){
        final String urll =Url.URL + "getprofil.php?id="+xid;
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        Log.wtf("URL Called", urll + "");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class.getSimpleName(), "Auth Response: " +urll+ response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    for(int i=0;i<jArray.length();i++){
                        JSONObject jsonObject1=jArray.getJSONObject(i);
                        gnamaPartner=jsonObject1.getString("namaPartner");
                        galamat= jsonObject1.getString("alamat");
                        gnohp= jsonObject1.getString("nohp");
                        gpassword =jsonObject1.getString("password");
                    }
                    namaPartner.setText(gnamaPartner);
                    alamat.setText(galamat);
                    nohp.setText(gnohp);
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }

                }catch (JSONException e){e.printStackTrace(); }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
                Toast.makeText(getActivity(), "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
}
