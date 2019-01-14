package com.sevencats.movelist20.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sevencats.movelist20.Listener.MainCardListener;
import com.sevencats.movelist20.MainActivity;
import com.sevencats.movelist20.R;

import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<String> dateList;
    private MainCardListener listener;
    private int current_position = -1;

    public MainRecyclerAdapter(Context context, MainCardListener listener, List<String> dateList) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView date, date_sum;

        public MyViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.move_date);
            date_sum = v.findViewById(R.id.date_sum);
            card = v.findViewById(R.id.card);
        }
    }

    @Override
    public MainRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view, parent, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.date.setText(dateList.get(position));
        holder.date_sum.setText(Double.toString(MainActivity.db.daoMoves().getDatesSum(dateList.get(position))) + " грн.");
        current_position = position;

        if (current_position == position) {
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickDateCard(holder.date.getText().toString(), position);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public void updateList(List<String> list){
        dateList.clear();
        dateList = list;
        notifyDataSetChanged();
    }
}
