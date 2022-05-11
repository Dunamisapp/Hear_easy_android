package com.heareasy.common_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.heareasy.R;


public class MyToast {

    public static void display(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast_layout, null);

        Toast mtoast = new Toast(context);
        TextView tv = view.findViewById(R.id.toastId);
        tv.setText(message);
        //mtoast.setGravity(Gravity.BOTTOM, 0, 0);
        mtoast.setView(view);
        mtoast.setDuration(Toast.LENGTH_LONG);
        mtoast.show();
    }
}
