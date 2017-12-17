package com.rx.ben.jcoincheapp.views.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rx.ben.jcoincheapp.R;
import com.rx.ben.jcoincheapp.databinding.LobbyRoomCellBinding;
import com.rx.ben.jcoincheapp.views.viewHolders.RecyclerContentViewHolder;

import java.util.List;

public class RecyclerContentAdapter extends RecyclerView.Adapter<RecyclerContentViewHolder> {

    private List<String> mList;

    public RecyclerContentAdapter(List<String> list) {
        mList = list;
    }

    @Override
    public RecyclerContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lobby_room_cell, viewGroup,false);
        return new RecyclerContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerContentViewHolder viewHolder, int position) {
        String name = mList.get(position);
        viewHolder.bind(name);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}