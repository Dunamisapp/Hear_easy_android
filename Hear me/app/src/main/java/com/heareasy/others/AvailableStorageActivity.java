package com.heareasy.others;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.TextView;


import com.heareasy.R;
import com.heareasy.common_classes.AppConstants;

import java.io.File;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import java.util.Locale;

public class AvailableStorageActivity extends AppCompatActivity {

    TextView textView;
    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = findViewById(R.id.textView);
        textView.setText(humanReadableByteCountBin(sd_card_free())+"\n"+storage());
        Log.e("TAG", ">>>>>>>>: "+storage() );
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public long sd_card_free(){
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availBlocks = stat.getAvailableBlocksLong();
        long blockSize = stat.getBlockSizeLong();
        long free_memory = availBlocks * blockSize;
        return free_memory;
    }

    public String storage(){
        final long space = sd_card_free();
        String format = "wav";
        int sampleRate = 8000;
        int bitrate = 48000;
        int channelsCount = 2;
        final long time = spaceToTimeSecs(space, format, sampleRate, bitrate, channelsCount);
        return formatTimeIntervalHourMinSec(time);
    }
    private long spaceToTimeSecs(long spaceBytes, String recordingFormat, int sampleRate, int bitrate, int channels) {
        switch (recordingFormat) {
            case AppConstants.FORMAT_M4A:
            case AppConstants.FORMAT_3GP:
                return 1000 * (spaceBytes/(bitrate/8));
            case AppConstants.FORMAT_WAV:
                return 1000 * (spaceBytes/(sampleRate * channels * 2));
            default:
                return 0;
        }
    }

    public static String formatTimeIntervalHourMinSec(long mills) {
        long hour = mills / (60 * 60 * 1000);
        long min = mills / (60 * 1000) % 60;
        long sec = (mills / 1000) % 60;
        if (hour == 0) {
            if (min == 0) {
                return String.format(Locale.getDefault(), "%02ds", sec);
            } else {
                return String.format(Locale.getDefault(), "%02dm:%02ds", min, sec);
            }
        } else {
            return String.format(Locale.getDefault(), "%02dh:%02dm:%02ds", hour, min, sec);
        }
    }
}