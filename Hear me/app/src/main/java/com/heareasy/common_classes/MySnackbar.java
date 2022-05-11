package com.heareasy.common_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.heareasy.R;

public class MySnackbar {
    public static void showLong(Context ctx, View view, String msg) {

        try
        {

            Snackbar snackbar = Snackbar.make(view, msg, 9000).setAction("Action", null);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark));
            TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(17);
            // tv.setTypeface(Typeface.SANS_SERIF);

            TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action );
            snackbarActionTextView.setTextSize( 20 );
//            MethodFactory.applyLotoBoldToView(ctx,snackbarActionTextView);

            //Typeface font = Typeface.createFromAsset(ctx.getAssets(), "fonts/montserrat_bold.ttf");

            // Typeface font = Typeface.crBLACKeateFromAsset(ctx.getAssets(), "fonts/Raleway-Medium.ttf");

            //   tv.setTypeface(font);
//            MethodFactory.applyLotoBoldToView(ctx,tv);

            snackbar.show();
        }catch (Exception ep)
        {
            ep.printStackTrace();
        }
    }


    public static void show(Context ctx, View view, String msg) {

        try
        {

          Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
          snackbar.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark));
          TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
          tv.setTextColor(Color.WHITE);
          tv.setTextSize(17);
          // tv.setTypeface(Typeface.SANS_SERIF);

          TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action );
          snackbarActionTextView.setTextSize( 20 );
//          MethodFactory.applyLotoBoldToView(ctx,snackbarActionTextView);

          //Typeface font = Typeface.createFromAsset(ctx.getAssets(), "fonts/montserrat_bold.ttf");

          // Typeface font = Typeface.crBLACKeateFromAsset(ctx.getAssets(), "fonts/Raleway-Medium.ttf");

          //   tv.setTypeface(font);
//          MethodFactory.applyLotoBoldToView(ctx,tv);

          snackbar.show();
      }catch (Exception ep)
      {
          ep.printStackTrace();
      }
    }

    public static void showDark(Context ctx, View view, String msg) {

        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null);

        snackbar.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark));
      // snackbar.getView().setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,100));


        TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ctx.getResources().getColor(R.color.white));
        tv.setTextSize(18);
        /*  tv.setTypeface(Typeface.SANS_SERIF);
        tv.setTypeface(Typeface.DEFAULT_BOLD);


        Typeface font = Typeface.createFromAsset(ctx.getAssets(), "fonts/Raleway-Medium.ttf");

        tv.setTypeface(font);*/

//      MethodFactory.applyLotoBoldToView(ctx,tv);

        snackbar.show();
    }

    public static void closeApp(final Activity activity, View view) {

        Snackbar snackbar = Snackbar.make(view, "Press exit to close the App.", Snackbar.LENGTH_LONG).setAction("exit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.black));
        View snakView = snackbar.getView();
        snakView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        snakView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

       TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        tv.setTextSize(17);
        tv.setAllCaps(false);
        tv.setMaxLines(10);
        tv.setPadding(0, 40, 0, 40);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action );
        snackbarActionTextView.setTextSize( 20 );
//        MethodFactory.applyLotoBoldToView(activity,snackbarActionTextView);
//        MethodFactory.applyLotoBoldToView(activity,tv);

        snackbar.show();
    }

    public static void closeApp1(final Activity activity, View view) {

        Snackbar snackbar = Snackbar.make(view, "Press exit to close the App.", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark)).setAction("exit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.finish();
                activity.startActivity(intent);
            }
        });
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
       TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(activity, R.color.white));
        tv.setTextSize(16);
        tv.setAllCaps(false);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        snackbar.show();
    }
}