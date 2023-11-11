package com.photocleaner.smsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context context;
    ArrayList<SmsData> list;

    public Adapter(Context context, ArrayList<SmsData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        SmsData dataReturn = list.get(position);
        holder.number.setText(dataReturn.getPhone());
        holder.text.setText(dataReturn.getText());
        holder.date.setText(dataReturn.getTime());
        holder.status.setText(dataReturn.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView number, text, date, status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = itemView.findViewById(R.id.phoneNumber);
            text = itemView.findViewById(R.id.msg_text);
            date = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.Status);
        }
    }
}
