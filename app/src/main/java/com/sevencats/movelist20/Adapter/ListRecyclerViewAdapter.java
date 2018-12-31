package com.sevencats.movelist20.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevencats.movelist20.Listener.ListCardListener;
import com.sevencats.movelist20.Listener.OnSwipeTouchListener;
import com.sevencats.movelist20.MainActivity;
import com.sevencats.movelist20.R;

import static com.sevencats.movelist20.MainActivity.db;

public class ListRecyclerViewAdapter extends CursorRecyclerViewAdapter<ListRecyclerViewAdapter.ViewHolder> {

    Context context;
    ListCardListener listener;
    Cursor cursor;

    public ListRecyclerViewAdapter(Context context, Cursor cursor, ListCardListener listener) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView inAddress;
        public TextView outAddress;
        public CardView card_view;
        public ImageView removeCard;

        public ViewHolder(View view) {
            super(view);
            inAddress = view.findViewById(R.id.inAddress);
            outAddress = view.findViewById(R.id.outAddress);
            card_view = view.findViewById(R.id.card);
            removeCard = view.findViewById(R.id.delete_card_btn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_view, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,  Cursor cursor) {
        final Animation card_anim = AnimationUtils.loadAnimation(context,R.anim.card_anim);
        final ListItem myItem = ListItem.fromCursor(cursor);
        viewHolder.inAddress.setText(myItem.getInAddress());
        viewHolder.outAddress.setText(myItem.getOutAddress());


        viewHolder.removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.db.daoMoves().delRec(myItem.getId());
                viewHolder.card_view.startAnimation(card_anim);
            }
        });

        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickCard(myItem.getId(),myItem.getInAddress(),myItem.getOutAddress(),myItem.getDate());
            }
        });

        viewHolder.card_view.setOnTouchListener(new OnSwipeTouchListener(){
            public boolean onSwipeRight(){
                MainActivity.db.daoMoves().delRec(myItem.getId());
                viewHolder.card_view.startAnimation(card_anim);
                return true;
            }
        });

        card_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewHolder.removeCard.setOnClickListener(null);
                viewHolder.card_view.setOnClickListener(null);
                viewHolder.card_view.setOnTouchListener(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateMoves(db.daoMoves().getDateMoves(myItem.getDate()));
                listener.onDeleteCard();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void updateMoves(Cursor cursor) {
        swapCursor(cursor);
    }
}
