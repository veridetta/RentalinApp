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
import com.vrcorp.rentalinapp.adapter.OrderanAdapter;
import com.vrcorp.rentalinapp.model.ModelUtama;
import com.vrcorp.rentalinapp.server.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OrderanFragment extends Fragment {
    ProgressDialog pDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;
    int success;
    String namaMobil, idOrderan, status, cekin, cekout, supir, pendapatan, string_id;
    List<ModelUtama> dbList;
    List<ModelUtama> modelList = new ArrayList<ModelUtama>();
    private OrderanAdapter adapter;
    LinearLayout data, noData;
    RecyclerView order_list;
    View view;

    public OrderanFragment() {
        // Required empty public constructor
    }

    public static OrderanFragment newInstance(String param1, String param2) {
        OrderanFragment fragment = new OrderanFragment();
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
        view = inflater.inflate(R.layout.fragment_orderan, container, false);
        noData = view.findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);
        sharedpreferences = getActivity().getSharedPreferences("rentalinApp", Context.MODE_PRIVATE);
        string_id = sharedpreferences.getString("id", null);
        getOrderan(string_id, (ViewGroup) view);
        return view;
    }
    private void getOrderan(final String xid, final ViewGroup view){
        final String urll = Url.URL + "getorderan.php?id="+xid;
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
                dbList= new ArrayList<ModelUtama>();
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    if(jsonObject.getInt("total")>0){
                        noData.setVisibility(View.GONE);
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            idOrderan=jsonObject1.getString("idOrderan");
                            namaMobil =jsonObject1.getString("namaMobil");
                            cekin= jsonObject1.getString("cekin");
                            cekout= jsonObject1.getString("cekout");
                            pendapatan=jsonObject1.getString("pendapatan");
                            status=jsonObject1.getString("status");
                            supir=jsonObject1.getString("supir");
                            ModelUtama model = new ModelUtama();
                            model.setIdOrderan(idOrderan);
                            model.setNamamobil(namaMobil);
                            model.setCekin(cekin);
                            model.setCekout(cekout);
                            model.setBiaya(pendapatan);
                            model.setSupir(supir);
                            model.setStatus(status);
                            modelList.add(model);
                        }

                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        dbList = modelList;
                        order_list =  view.findViewById(R.id.rc_order);
                        adapter = new OrderanAdapter(getContext(), dbList);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),1);
                        order_list.setLayoutManager(mLayoutManager);
                        //konjugasi_list.setItemAnimator(new DefaultItemAnimator());
                        //DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
                        //konjugasi_list.addItemDecoration(decoration);
                        order_list.setAdapter(adapter);
                    }else{
                        if(pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        noData.setVisibility(View.VISIBLE);
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
