package com.sevencats.movelist20.Listener;

public interface ListCardListener {
    void onDeleteCard();
    void onCopyCard(long id ,String inAddress, String outAddress, String date, int flag);
    void onClickCard(long id ,String inAddress, String outAddress, String date, int flag);
}
