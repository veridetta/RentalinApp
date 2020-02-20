package com.vrcorp.rentalinapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vrcorp.rentalinapp.R;
import com.vrcorp.rentalinapp.model.ModelUtama;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class OrderanAdapter extends RecyclerView.Adapter<OrderanAdapter.MyViewHolder> {

    private Context context;
    List<ModelUtama> notesList, konjugasi;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView idOrderan, status, cekin, cekout, supir, pendapatan, namaMobil;
        public CardView ly_menu;
        LinearLayout ly_button;

        public MyViewHolder(View view) {
            super(view);
            idOrderan = view.findViewById(R.id.id_orderan);
            status = view.findViewById(R.id.status);
            cekin = view.findViewById(R.id.cekin);
            cekout = view.findViewById(R.id.cekout);
            supir = view.findViewById(R.id.supir);
            pendapatan = view.findViewById(R.id.pendapatan);
            namaMobil= view.findViewById(R.id.namaMobil);
            ly_menu = view.findViewById(R.id.card_menu);
            ly_button = view.findViewById(R.id.ly_button);
        }
    }


    public OrderanAdapter(Context context, List<ModelUtama> notesList) {
        this.context = context;
        this.notesList = notesList;
        this.konjugasi = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderan_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ModelUtama student = notesList.get(position);
        holder.idOrderan.setText(student.getIdOrderan());
        holder.status.setText(student.getStatus());
        holder.cekin.setText("Cek in: "+student.getCekin());
        holder.cekout.setText("Cek out: "+student.getCekout());
        holder.supir.setText("Supir: "+student.getSupir());
        holder.namaMobil.setText(student.getNamamobil());
        if(student.getStatus().equals("Menunggu Konfirmasi")){
            holder.ly_button.setVisibility(View.VISIBLE);
        }else{
            holder.ly_button.setVisibility(View.GONE);
        }
        //format harga
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
        String prezzo = decimalFormat.format(Integer.parseInt(student.getBiaya()));
        holder.pendapatan.setText(prezzo);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

}