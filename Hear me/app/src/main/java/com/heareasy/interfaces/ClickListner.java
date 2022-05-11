package com.heareasy.interfaces;

import android.view.View;

public interface ClickListner {
    void deleteItem(int position);
    void renameItem(int position);
    void shareItem(int position);
    void onItemClick(int position);
    void onFavouriteButtonClick(View view, int position);
}
