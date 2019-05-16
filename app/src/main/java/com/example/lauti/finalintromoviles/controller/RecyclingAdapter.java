package com.example.lauti.finalintromoviles.controller;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link:https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */
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

        // Mapping text views
        TextView dateText = (TextView) view.findViewById(R.id.dateText);
        TextView bottlesText = (TextView) view.findViewById(R.id.bottlesText);
        TextView tetrabriksText = (TextView) view.findViewById(R.id.tetrabriksText);
        TextView paperboardText = (TextView) view.findViewById(R.id.paperboardText);
        TextView glassText = (TextView) view.findViewById(R.id.glassText);
        TextView cansText = (TextView) view.findViewById(R.id.cansText);

        // Mapping image views
        ImageView bottlesImg = (ImageView) view.findViewById(R.id.bottlesImage);
        ImageView tetrabrikImg = (ImageView) view.findViewById(R.id.tetrabriksImage);
        ImageView paperboardImg = (ImageView) view.findViewById(R.id.paperboardImage);
        ImageView glassImg = (ImageView) view.findViewById(R.id.glassImage);
        ImageView cansImg = (ImageView) view.findViewById(R.id.cansImage);

        // Setting images to image views
        bottlesImg.setImageResource(R.drawable.ic_bottles);
        tetrabrikImg.setImageResource(R.drawable.ic_tetrabriks);
        paperboardImg.setImageResource(R.drawable.ic_paperboard);
        glassImg.setImageResource(R.drawable.ic_glass);
        cansImg.setImageResource(R.drawable.ic_cans);

        dateText.setText(userRecyclingList.get(position).getDate());  // set recycling date

        bottlesText.setText(bottlesText.getText() + " " + userRecyclingList.get(position).getBottles()); // set bottles amount
        tetrabriksText.setText(tetrabriksText.getText() + " " + userRecyclingList.get(position).getTetrabriks()); // set tetrabriks amount
        paperboardText.setText(paperboardText.getText() + " " + userRecyclingList.get(position).getPaperboard()); // set paperboard amount;
        glassText.setText(glassText.getText() + " " + userRecyclingList.get(position).getGlass()); // set glass amount
        cansText.setText(cansText.getText() + " " + userRecyclingList.get(position).getCans()); // set cans amount

        return view;
    }


}
