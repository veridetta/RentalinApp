package com.vrcorp.rentalinapp.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vrcorp.rentalinapp.MainActivity;
import com.vrcorp.rentalinapp.R;
import com.vrcorp.rentalinapp.RegisterActivity;
import com.vrcorp.rentalinapp.adapter.MobilAdapter;
import com.vrcorp.rentalinapp.model.ModelUtama;
import com.vrcorp.rentalinapp.server.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    ProgressDialog pDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;
    int success;
    String idmobil, kodeMobil, merekMobil, alamat, harga, string_id;
    List<ModelUtama> dbList;
    List<ModelUtama> modelList ;
    private MobilAdapter adapter;
    LinearLayout data, noData;
    RecyclerView order_list;
    View view;
    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mobil, container, false);
        sharedpreferences = getActivity().getSharedPreferences("rentalinApp", Context.MODE_PRIVATE);
        string_id = sharedpreferences.getString("id", null);
        getMobil(string_id);
        return view;
    }
    private void getMobil(final String xid){
        final String urll = Url.URL + "getmobil.php?id="+xid;
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        modelList= new ArrayList<ModelUtama>();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        Log.wtf("URL Called", urll + "");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    if(jArray.length()>0){
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            idmobil=jsonObject1.getString("idmobil");
                            kodeMobil =jsonObject1.getString("kodeMobil");
                            merekMobil= jsonObject1.getString("merekMobil");
                            alamat= jsonObject1.getString("alamat");
                            harga=jsonObject1.getString("harga");
                            ModelUtama model = new ModelUtama();
                            model.setIdOrderan(idmobil);
                            model.setKodemobil(kodeMobil);
                            model.setNamamobil(merekMobil);
                            model.setAlamat(alamat);
                            model.setBiaya(harga);
                            model.setTujuan("mobil");
                            modelList.add(model);
                        }
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        dbList = modelList;
                        order_list =  view.findViewById(R.id.rc_mobil);
                        adapter = new MobilAdapter(getContext(), dbList);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),1);
                        order_list.setLayoutManager(mLayoutManager);
                        //konjugasi_list.setItemAnimator(new DefaultItemAnimator());
                        //DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
                        //konjugasi_list.addItemDecoration(decoration);
                        order_list.setAdapter(adapter);
                        Log.e(MainActivity.class.getSimpleName(), "Auth Response: " +modelList+urll+ response);
                    }else{
                        if(pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
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
