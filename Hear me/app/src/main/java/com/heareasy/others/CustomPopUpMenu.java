package com.heareasy.others;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.heareasy.R;
import com.skydoves.powermenu.CircularEffect;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

public class CustomPopUpMenu {
    public static PowerMenu powerMenu;

   public CustomPopUpMenu(Context context, View view)
    {
        powerMenu = new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("Rename", R.drawable.ic_edit))
                .addItem(new PowerMenuItem("Delete", R.drawable.ic_delete_12))
                .addItem(new PowerMenuItem("Share", R.drawable.ic_share))
                .setAnimation(MenuAnimation.ELASTIC_CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setCircularEffect(CircularEffect.BODY)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setTextGravity(Gravity.CENTER)
                .setSelectedTextColor(Color.BLUE)
                .setMenuColor(Color.WHITE)
                .setDividerHeight(1)
                .build();
       powerMenu.showAsAnchorRightTop(view);
    }
    public CustomPopUpMenu(Context context1, View view1,String s)
    {
        powerMenu = new PowerMenu.Builder(context1)
                .addItem(new PowerMenuItem("Unpair"))
                .setAnimation(MenuAnimation.ELASTIC_CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setCircularEffect(CircularEffect.BODY)
                .setTextColor(ContextCompat.getColor(context1, R.color.colorPrimary))
                .setTextGravity(Gravity.CENTER)
                .setSelectedTextColor(Color.BLUE)
                .setMenuColor(Color.WHITE)
                .setDividerHeight(1)
                .build();
       powerMenu.showAsAnchorRightTop(view1);
    }
}
