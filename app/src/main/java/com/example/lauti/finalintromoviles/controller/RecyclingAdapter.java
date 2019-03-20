package com.example.lauti.finalintromoviles.controller;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.model.UserRecycling;

import java.util.ArrayList;
import java.util.List;

public class RecyclingAdapter extends BaseAdapter {

    private List<UserRecycling> userRecyclingList = new ArrayList<>();
    private Context context;

    public RecyclingAdapter(Context context, List<UserRecycling> userRecyclingList) {
        this.context = context;
        this.userRecyclingList = userRecyclingList;
    }

    @Override
    public int getCount() {
        return userRecyclingList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_list_view, null);

        /**
         * Here could map images too
         */
        TextView dateText = (TextView) view.findViewById(R.id.dateText);
        TextView bottlesText = (TextView) view.findViewById(R.id.bottlesText);
        TextView tetrabriksText = (TextView) view.findViewById(R.id.tetrabriksText);
        TextView paperboardText = (TextView) view.findViewById(R.id.paperboardText);
        TextView glassText = (TextView) view.findViewById(R.id.glassText);
        TextView cansText = (TextView) view.findViewById(R.id.cansText);

        if (userRecyclingList.size() > 0) {
            dateText.setText(userRecyclingList.get(position).getDate());  // set recycling date
            bottlesText.setText("Botellas:  " + userRecyclingList.get(position).getBottles()); // set bottles amount
            tetrabriksText.setText("Tetrabriks:  " +userRecyclingList.get(position).getTetrabriks()); // set tetrabriks amount
            paperboardText.setText("Cartones:  " + userRecyclingList.get(position).getPaperboard()); // set paperboard amount;
            glassText.setText("Vidrio:  " + userRecyclingList.get(position).getGlass()); // set glass amount
            cansText.setText("Latas:  " + userRecyclingList.get(position).getCans()); // set cans amount
        } else {
            dateText.setText("El usuario no ha registrado reciclados.");
        }

        return view;
    }
}
