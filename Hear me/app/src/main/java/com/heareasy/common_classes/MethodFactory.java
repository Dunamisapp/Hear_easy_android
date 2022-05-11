package com.heareasy.common_classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.heareasy.R;
import com.heareasy.activity.SignInActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 08-08-2017.
 */

public class MethodFactory {


    public static int WIDTH = 500;

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


   /* public static String encodeTobase64(Bitmap image) {
        Bitmap bitmap_image = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));

        bitmap_image.compress(Bitmap.CompressFormat.PNG, 100, baos);



        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }*/

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static String getOnePlacesDecimal(double d) {
        return String.format("%.2f", d);

    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static int getPosInList(String mCountryName, List<String> myList) {
        if (myList != null || !myList.isEmpty()) {
            for (int i = 0; i < myList.size(); ++i) {
                if (myList.get(i).toLowerCase().trim().equalsIgnoreCase(mCountryName)) {
                    return i;
                }
            }
        }
        return -1;

    }


    @SuppressLint("MissingPermission")
    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        try {
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }


    public static void showAlertDialog(String message, Context context) {
        AlertDialog logoutDialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setTitle(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                }).create();//.create().show();
        logoutDialog.show();
        Button nbutton = logoutDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        Button pbutton = logoutDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(context.getResources().getColor(R.color.colorPrimary));

    }


    public static String getStringFromList(ArrayList<String> mArrayList) {
        String tempString = mArrayList.toString().replaceAll("\\[|\\]", "").replaceAll(" ", "").replace(",,", ",");
        String res = "";
        if (!TextUtils.isEmpty(tempString)) {
            if (tempString.charAt(tempString.length() - 1) == ',') {
                tempString = tempString.substring(0, tempString.length() - 1);
            }
            return tempString;
        }
        return "";
    }

    // Login & signup   Validation
    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isPasswdAlphaNumeric(String password) {
        boolean isValid = false;
        String expression = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,17}$";
        String expression1 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
        CharSequence inPasswd = password;

        Pattern pattern = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inPasswd);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;


    }


    public static boolean isPasswdValid(String password) {
        boolean isValid = false;
        String expression = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        String expression1 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
        CharSequence inPasswd = password;

        Pattern pattern = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inPasswd);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;


    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPass(CharSequence target) {


        if (TextUtils.isEmpty(target)) {
            return false;
        } else if (target.length() < 6) {
            return false;
        } else {
            return true;
        }

    }


    public static final boolean isPasswdord8ChargsLong(String passwd) {
        if (TextUtils.isEmpty(passwd)) {
            return false;
        } else if (passwd.length() < 8) {
            return false;
        } else {
            return true;
        }
    }

    public final static boolean isValidTelephone(String target) {

        if (TextUtils.isEmpty(target)) {
            return false;
        } else if (target.length() < 5) {
            return false;
        } else
            return true;
    }

    public static boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        } else if (!name.matches("[A-Z][a-zA-Z]*"))
            return false;
        else return true;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public final static boolean isValidTelephone(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            return false;
        } else if (target.length() < 10) {
            return false;
        } else
            return true;

    }


    public static void animateNavigationView(final FragmentActivity mActivity,
                                             final FrameLayout contentView,
                                             DrawerLayout drawer, final NavigationView navigationView) {

        drawer.setScrimColor(Color.TRANSPARENT);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawer, float slideOffset) {

                                         //MethodFactory.setNavigationViewAnimation(mActivity, drawer);
                                         contentView.setX(navigationView.getWidth() * slideOffset);
                                         RelativeLayout.LayoutParams lp =
                                                 (RelativeLayout.LayoutParams) contentView.getLayoutParams();
                                         lp.height = drawer.getHeight() -
                                                 (int) (drawer.getHeight() * slideOffset * 0.3f);
                                         lp.topMargin = (drawer.getHeight() - lp.height) / 2;
                                         contentView.setLayoutParams(lp);

                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                     }
                                 }
        );


    }


    public static class TypefaceSpan extends MetricAffectingSpan {

        private final Typeface typeface;

        public TypefaceSpan(Typeface typeface) {
            this.typeface = typeface;
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(typeface);
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(typeface);
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

    }

    public static SpannableString getSpannedString(Typeface typeface, CharSequence string) {
        SpannableString s = new SpannableString(string);
        s.setSpan(new TypefaceSpan(typeface), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }


    public static Activity activity;
    public static boolean share = false, onBackPress = false, allowPermitionExternalStorage = false, slider = false, loginBack = false;
    public static Bitmap mbitmap;
    private static File file;
    private String filename;

    public static Typeface scriptable;

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "login";
    public String pref_login = "pref_login";
    private String firstTime = "firstTime";
    public String profileId = "profileId";
    public String userEmail = "userEmail";
    public String userPassword = "userPassword";
    public String userName = "userName";


    public static void fullScreen(Activity activity) {

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void closeApp(final Activity mActivity, View view) {
        Snackbar snackbar = Snackbar.make(view, "Press close to exit the App.",
                Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.finish();
                mActivity.startActivity(intent);
            }
        });
        View snakView = snackbar.getView();
        snackbar.setActionTextColor(ContextCompat.getColor(mActivity, R.color.white));
        snakView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        snakView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        tv.setTextSize(18);
        tv.setAllCaps(false);
        tv.setMaxLines(10);
        tv.setPadding(0, 40, 0, 40);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
        snackbarActionTextView.setTextSize(20);
        //MethodFactory.applyLotoBoldToView(activity,tv);
        snackbar.show();
    }


    public static String round(double number, int decimals) {
        StringBuilder sb = new StringBuilder(decimals + 2);
        sb.append("#.");
        for (int i = 0; i < decimals; i++) {
            sb.append("0");
        }
        if (number < 1) {
            return "0" + new DecimalFormat(sb.toString()).format(number);

        } else {
            return new DecimalFormat(sb.toString()).format(number);
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public static void openMail(Context mActivity, String emailAddress) {

        // Intent intent = new Intent(Intent.ACTION_SEND);
        try {
          /*  Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            String[] to = {emailAddress};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "John Bull");
            intent.putExtra(Intent.EXTRA_TEXT, "From John Bull");
            intent.setType("message/rfc822");
            mActivity.startActivity(Intent.createChooser(intent, "Send Email"));*/
            Intent intent = new Intent(Intent.ACTION_SEND);
            String[] recipients = {emailAddress};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, "John Bull");
            intent.putExtra(Intent.EXTRA_TEXT, "From John Bull");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            mActivity.startActivity(Intent.createChooser(intent, "Send mail"));
        } catch (Exception ep) {
            ep.printStackTrace();
        }
    }

    public static void openWebsite(Context mActivity, String websiteLink) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(websiteLink));
            mActivity.startActivity(Intent.createChooser(i, "Open Website"));
        } catch (Exception ep) {
            ep.printStackTrace();
        }
    }


    private static void makeCall(Context activity, String contact) {
        String phone = contact;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        activity.startActivity(intent);

    }

    public static void printError() {

    }

    public static void forceLogoutDialog(final FragmentActivity activity) {
        MyToast.display(activity, "Your login session has been expired");
        new SessionManager(activity).logoutUser();
        activity.startActivity(new Intent(activity, SignInActivity.class));
        activity.finish();
        /*AlertDialog logoutDialog = new AlertDialog.Builder(activity, R.style.CustomDialogTheme)
                .setTitle("Another user logged in with this ID")
                .setCancelable(false)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                }).create();//.create().show();
        logoutDialog.show();
        Button nbutton = logoutDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        Button pbutton = logoutDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(activity.getResources().getColor(R.color.colorPrimary));*/
    }

    public static void applyCremsonFontToView(Context mCtx, View childView) {

        String tag = "";
        if (childView instanceof TextView) {
            tag = AppConstants.TAG_TEXTVIEW;
        } else if (childView instanceof EditText) {
            tag = AppConstants.TAG_EDITTEXT;
        } else if (childView instanceof Button) {
            tag = AppConstants.TAG_BUTTON;
        } else if (childView instanceof RadioButton) {
            tag = AppConstants.TAG_RADIO_BUTTON;
        } else {
            tag = "";
        }
        Typeface font = Typeface.createFromAsset(mCtx.getAssets(), AppConstants.COMMON_APP_FONT);

        switch (tag) {
            case AppConstants.TAG_TEXTVIEW:
                ((TextView) childView).setTypeface(font);
                break;
            case AppConstants.TAG_EDITTEXT:
                ((EditText) childView).setTypeface(font);
                ((EditText) childView).setTypeface(font);

                break;
            case AppConstants.TAG_BUTTON:
                ((Button) childView).setTypeface(font);
                break;

            case AppConstants.TAG_RADIO_BUTTON:
                ((RadioButton) childView).setTypeface(font);
                break;


        }

    }

    public static void applyLotoBoldToView(Context mCtx, View childView) {
        String tag = "";
        if (childView instanceof TextView) {
            tag = AppConstants.TAG_TEXTVIEW;
        } else if (childView instanceof EditText) {
            tag = AppConstants.TAG_EDITTEXT;
        } else if (childView instanceof Button) {
            tag = AppConstants.TAG_BUTTON;
        } else if (childView instanceof RadioButton) {
            tag = AppConstants.TAG_RADIO_BUTTON;
        } else {
            tag = "";
        }
        Typeface font = Typeface.createFromAsset(mCtx.getAssets(), AppConstants.LOTO_BOLD);

        switch (tag) {
            case AppConstants.TAG_TEXTVIEW:
                ((TextView) childView).setTypeface(font);
                break;
            case AppConstants.TAG_EDITTEXT:
                ((EditText) childView).setTypeface(font);
                ((EditText) childView).setTypeface(font);

                break;
            case AppConstants.TAG_BUTTON:
                ((Button) childView).setTypeface(font);
                break;

            case AppConstants.TAG_RADIO_BUTTON:
                ((RadioButton) childView).setTypeface(font);
                break;
        }
    }


    public static void closeKeyboard(Context mActivity, View v) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }


    public static void openBrowser(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);

    }

    public static String getAndroidUniqueID(Activity mActivity) {
        return Settings.Secure.getString(mActivity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void setUnderLineText(TextView tvFullDescription, String view_full_description) {

        SpannableString content = new SpannableString(view_full_description);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvFullDescription.setText(content);
    }


    public static int density(Context context) {
        double density = context.getResources().getDisplayMetrics().density;
        String value = String.format("%.0f", density);
        return Integer.parseInt(value);


    }


    public static void shareApp(FragmentActivity mActivity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Download JOHN BULL : https://play.google.com/store/apps/details?id=" + mActivity.getPackageName());
        sendIntent.setType("text/plain");
        mActivity.startActivity(sendIntent);

    }


    public static int getRandomColor() {

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#9e0e70"));
        colors.add(Color.parseColor("#ff331a"));
        colors.add(Color.parseColor("#FFFF00"));
        colors.add(Color.parseColor("#000000"));
        colors.add(Color.parseColor("#ffffff"));
        colors.add(Color.parseColor("#e568fe"));

        return colors.get(new Random().nextInt(colors.size()));

    }

    public static void applyColorToHamburgerToggle(Context mainActivity, ActionBarDrawerToggle toggle) {
        toggle.getDrawerArrowDrawable().setColor(mainActivity.getResources().getColor(R.color.colorPrimary));


    }

    public static void setNavigationViewAnimation(FragmentActivity context, final View navigationView) {


        Animation hold = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        navigationView.startAnimation(hold);
        hold.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                navigationView.clearAnimation();
                // mCard.startAnimation(getScaleAnimation());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void login() {
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(pref_login, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }

    public static void disableEnableControls(boolean enable, ViewGroup vg) {

        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
            if (enable) {
                if (child instanceof EditText) {
                    ((EditText) child).setSelection(((EditText) child).getText().toString().length());
                }
            }
        }
    }

    public static void showtoastshrt(Context cntxt, String msg) {
        Toast.makeText(cntxt, msg, Toast.LENGTH_SHORT).show();

    }


    //Google Tracker
 /*   public static void trackScreenView(activities activity, String screenName) {
        YouApplication application = (YouApplication) activity.getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }*/

    //network check
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    /*<---------------------- share & downlode and methodes ---------------------->*/

    public static void share_save(String image, String bookName, String bookAuthor, String description, String bookLink) {
        new DownloadImageTask().execute(image, bookName, bookAuthor, description, bookLink);
    }

    public static class DownloadImageTask extends AsyncTask<String, String, String> {

        String bookName, bookAuthor, bookDescription, bookLink;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                bookName = params[1];
                bookAuthor = params[2];
                bookDescription = params[3];
                bookLink = params[4];
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                mbitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                // Log exception
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (share) {
                ShareImage(mbitmap, bookName, bookAuthor, bookDescription, bookLink);
                share = false;
            }
            super.onPostExecute(s);
        }
    }

    public static void fullScreenActivity(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private static void ShareImage(Bitmap finalBitmap, String bookName, String bookAuthor, String description, String bookLink) {

        String root = activity.getExternalCacheDir().getAbsolutePath();
        String fname = "Image_share" + ".jpg";
        file = new File(root, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri contentUri = Uri.fromFile(file);
        Log.d("url", String.valueOf(contentUri));

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setData(contentUri);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(bookName)
                    + "\n" + "\n" + Html.fromHtml(bookAuthor)
                    + "\n" + "\n" + Html.fromHtml(description)
                    + "\n" + "\n" + Html.fromHtml(bookLink));
            activity.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }
/*
    public void download(String id, String bookName, String bookImage, String bookAuthor, String bookUrl, String type) {

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidEBook/");
        if (!root.exists()) {
            root.mkdirs();
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(bookUrl));
        request.setDescription(activity.getResources().getString(R.string.downloading) + bookName);
        request.setTitle(activity.getResources().getString(R.string.app_name));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        filename = "filename-" + id;
        if (type.equals("epub")) {
            request.setDestinationInExternalPublicDir("/AndroidEBook/", filename + ".epub");
        } else {
            request.setDestinationInExternalPublicDir("/AndroidEBook/", filename + ".pdf");
        }

        Log.d("bookNmae", bookName);
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

        new DownloadImage().execute(bookImage, id, bookName, bookAuthor, type);

    }

    public class DownloadImage extends AsyncTask<String, String, String> {

        private String id, bookName, bookAuthor, type;
        Bitmap bitmapDownload;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                id = params[1];
                bookName = params[2];
                bookAuthor = params[3];
                type = params[4];
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmapDownload = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                // Log exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            downloadImage(bitmapDownload, id, bookName, bookAuthor, type);

            super.onPostExecute(s);
        }

    }

    public void downloadImage(Bitmap bitmap, String id, String bookName, String bookAuthor, String type) {

        String filePath = null;

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/AndroidEBook/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            String fname = "Image-" + id;
            filePath = sdIconStorageDir.toString() + "/" + fname + ".jpg";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
        }
        if (type.equals("epub")) {
            db.addDownload(new DownloadList(id, bookName, filePath, bookAuthor, iconsStoragePath + filename + ".epub"));
        } else {
            db.addDownload(new DownloadList(id, bookName, filePath, bookAuthor, iconsStoragePath + filename + ".pdf"));
        }
    }*/

    public static int dpToPx(final float dp) {
        return Math.round(dp * (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static List<Address> getAddress(Context mContext, double lat, double lng) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            Log.e("TestRajeet", "MethodValue" + geocoder.getFromLocation(lat, lng, 1));

            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();

                Log.v("IGA", "Address" + add);
            }
            Log.e("TestRajeet", "MethodFactory" + addresses.toString());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return addresses;
    }
}
