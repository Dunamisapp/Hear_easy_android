package com.heareasy.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.viewpager.widget.PagerAdapter;

import com.heareasy.R;

import java.util.ArrayList;

public class WelcomeAdapter extends PagerAdapter {


    private ArrayList<Integer> LAYOUTS;
    private Context context;

    public WelcomeAdapter(Context context, ArrayList<Integer> LAYOUTS) {
        this.context = context;
        this.LAYOUTS = LAYOUTS;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return LAYOUTS.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(context)
                .inflate(LAYOUTS.get(position), view, false);

        String s = "<html>" +
                "<body>" +
                "<h4>Hear Easy is your personal sound amplifier app</h4>" +
                "<p>Hear Easy reimagines the function of a traditional sound amplifier. The App allows you to use your Smartphone’s built in MIC and a connected Bluetooth speaker for use as a personal sound amplifier. This is suitable for small group settings, and lets your sound be heard more clearly. Set up is quick and easy. The app gives you the option to record your audio session and save the MP3 file on your device; you can listen to your recording, rename the file or share the recorded MP3 file with friends and family.</p>" +
         /*   "<h4>Hear Easy is your personal karaoke player </h4>" +
            "The App allows you to use your Tablet or Smartphone’s built in MIC and a connected Bluetooth speaker for use as a karaoke player. Select the Karaoke mode in the App settings, select the music app and the track you want to sing to, and off you go with your best vocal rendition of the song. It is pop-up entertainment on the go with your friends and family." +
            "<p>The app records your audio session and saves the file on your device; you can listen to your recordings, rename the file or share the recorded MP3 file with friends and family.</p>" +
            */"</body>" + "</html>";
        String s2 = "<h4>The setup to use the Hear Easy app is quite simple.</h4>\n<p>1. Tap on the Speaker icon and the app will display the Select Audio Output screen. If a Bluetooth speaker is currently paired with your device, it will be shown and if you like the selection, no further action is needed. If you like to choose a different Bluetooth speaker, go to Android Settings and choose your preferred Bluetooth speaker. <br><br> 2. Tap on the New Recording icon, if you like to verify the correct Bluetooth speaker is selected, tap on the &quot;TEST AUDIO OUT&quot; and say a few words; adjust the phone and or Bluetooth speaker volume level as desired; after you are satisfied at the volume level, tap the &quot;OFF&quot; button. When you are ready, tap on the ON button to start the lecture.<br><br>  3. When you are done, tap on the OFF button and after the file is saved, you can play the recording back.<br><br>  4. Tap on Go To Recorded Files and you will find the list of all your recordings. You can listen to your recordings, rename the file or share the recorded MP3 file with friends and family. You can also check the available storage space you have on the phone.</p>";

        if (position == 1) {
            WebView webview1 = imageLayout.findViewById(R.id.webview1);
            webview1.getSettings().setBuiltInZoomControls(true);
            webview1.getSettings().setDisplayZoomControls(false);
            webview1.setWebViewClient(new WebViewClient());
            webview1.loadData(s, "text/html", "UTF-8");
        }

        if (position == 2) {
            WebView webview2 = imageLayout.findViewById(R.id.webview2);
            webview2.getSettings().setBuiltInZoomControls(true);
            webview2.getSettings().setDisplayZoomControls(false);
            webview2.setWebViewClient(new WebViewClient());
            webview2.loadData(s2, "text/html", "UTF-8");
        }

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
