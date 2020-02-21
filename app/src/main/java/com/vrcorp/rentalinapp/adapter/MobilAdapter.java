package com.vrcorp.rentalinapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.vrcorp.rentalinapp.OrderActivity;
import com.vrcorp.rentalinapp.R;
import com.vrcorp.rentalinapp.model.ModelUtama;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class MobilAdapter extends RecyclerView.Adapter<MobilAdapter.MyViewHolder> {
    private Context context;
    List<ModelUtama> notesList, konjugasi;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView idmobil, merekMobil, harga;
        public CardView ly_menu;
        ImageView img;

        public MyViewHolder(View view) {
            super(view);
            merekMobil = view.findViewById(R.id.nama_mobil);
            harga = view.findViewById(R.id.hargamobil);
            ly_menu = view.findViewById(R.id.card_menu);
        }
    }


    public MobilAdapter(Context context, List<ModelUtama> notesList) {
        this.context = context;
        this.notesList = notesList;
        this.konjugasi = notesList;
    }

    @Override
    public MobilAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mobil_list, parent, false);

        return new MobilAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MobilAdapter.MyViewHolder holder, int position) {
        final ModelUtama student = notesList.get(position);
        holder.merekMobil.setText(student.getNamamobil());
        holder.harga.setText(student.getBiaya());
        //format harga
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
        String prezzo = decimalFormat.format(Integer.parseInt(student.getBiaya()));
        holder.harga.setText(prezzo);
        holder.ly_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(student.getTujuan().equals("mobil")){
                    Intent intent = new Intent(v.getContext(), OrderActivity.class);
                    intent.putExtra("jenis","update");
                    intent.putExtra("id",student.getIdOrderan());
                    intent.putExtra("alamat",student.getAlamat());
                    intent.putExtra("merekMobil",student.getNamamobil());
                    intent.putExtra("harga",student.getBiaya());
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
