package com.sevencats.movelist20.Listener;

public interface ListCardListener {
    void onDeleteCard();
    void onClickCard(long id ,String inAddress, String outAddress, String date);
}
