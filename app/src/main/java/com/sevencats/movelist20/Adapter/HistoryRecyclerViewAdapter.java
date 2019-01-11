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

import com.sevencats.movelist20.Adapter.data.HistoryItem;
import com.sevencats.movelist20.Listener.HistoryCardListener;
import com.sevencats.movelist20.Listener.OnSwipeTouchListener;
import com.sevencats.movelist20.MainActivity;
import com.sevencats.movelist20.R;

import static com.sevencats.movelist20.MainActivity.db;

public class HistoryRecyclerViewAdapter extends CursorRecyclerViewAdapter<HistoryRecyclerViewAdapter.ViewHolder> {

    Context context;
    Cursor cursor;
    HistoryCardListener listener;

    public HistoryRecyclerViewAdapter(Context context, Cursor cursor, HistoryCardListener listener) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView from, to, mail, sum;
        public ImageView removeCard, resendMail;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            from = view.findViewById(R.id.fromDate);
            to = view.findViewById(R.id.toDate);
            mail = view.findViewById(R.id.mail);
            sum = view.findViewById(R.id.sum);
            removeCard = view.findViewById(R.id.remove_card);
            resendMail = view.findViewById(R.id.resend_mail);
            cardView = view.findViewById(R.id.card);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_card_view, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,  Cursor cursor) {
        final Animation card_anim = AnimationUtils.loadAnimation(context,R.anim.card_anim);
        final HistoryItem history = HistoryItem.fromCursor(cursor);

        viewHolder.from.setText(history.getFrom());
        viewHolder.to.setText(history.getTo());
        viewHolder.mail.setText(history.getMail());
        viewHolder.sum.setText(String.valueOf(history.getSum()));

        viewHolder.resendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.resendMailOnClick(history.getFrom(),history.getTo(),history.getMail());
            }
        });

        viewHolder.removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.cardView.startAnimation(card_anim);
                MainActivity.db.daoSendHistory().delRec(history.getId());
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.cardView.setOnTouchListener(new OnSwipeTouchListener(){
            public boolean onSwipeRight(){
                MainActivity.db.daoSendHistory().delRec(history.getId());
                viewHolder.cardView.startAnimation(card_anim);
                return true;
            }
        });

        card_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewHolder.removeCard.setOnClickListener(null);
                viewHolder.cardView.setOnTouchListener(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateMoves(db.daoSendHistory().getSendHistory());
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
