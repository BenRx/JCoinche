package com.rx.ben.jcoincheapp.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rx.ben.jcoincheapp.R;

public class RecyclerContentViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewView;

    public RecyclerContentViewHolder(View itemView) {
        super(itemView);

        textViewView = itemView.findViewById(R.id.room_name_text);
    }

    public void bind(String room){
        textViewView.setText(room);
    }
}