package com.heareasy.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.fragment.RecordFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordingAudioActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "RecordingAudioActivity";
    private Button btn_save_audio_recording;
    private Toolbar toolbar_recoding;
    private TextView tv_filenameText;
    private CircleImageView btn_off_recording;
    public  static File directory;

    public static boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    public static MediaRecorder mediaRecorder;
    private String recordFile;

    public static Chronometer timer;
    public MyStateView mLoadingBar;
    private SessionManager sessionManager;
    private Context context = RecordingAudioActivity.this;
    private Thread recordingThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_audio);
        mLoadingBar = new MyStateView(this, null);
        sessionManager = new SessionManager(this);
        btn_off_recording = findViewById(R.id.btn_off_recording);
        toolbar_recoding = findViewById(R.id.toolbar_recoding);
        toolbar_recoding.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_recoding.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        timer = findViewById(R.id.record_timer);
        tv_filenameText = findViewById(R.id.tv_filenameText);
        btn_save_audio_recording = findViewById(R.id.btn_save_audio_recording);
        btn_save_audio_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toStopLoudspeaker();
                stopRecording();
                isRecording = false;
                RecordFragment.player.stop();
                RecordFragment.player.release();
                RecordFragment.player = null;
            }
        });
        btn_off_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        //Get app external directory path
//        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();
//
         directory = new File(Environment.getExternalStorageDirectory()+"/Hear Me");

        File sdcardRoot = Environment.getExternalStoragePublicDirectory("/Hear Me");

        if(!directory.exists()) {
            if(directory.mkdir()); //directory is created;
        }

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".mp3";

        tv_filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(sdcardRoot + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        startRecording();
    }


    private void startRecording() {

        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();



        try {
            mediaRecorder.prepare();
            if (mediaRecorder!=null)
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = true;




    }

    public void stopRecording() {

        //Stop Timer, very obvious
        timer.stop();
        //Change text on page to file saved
        tv_filenameText.setText("Recording Stopped, File Saved : " + recordFile);
        Log.e("TAG", "path:>>>>>>>>>>> " + this.getExternalFilesDir("/").getAbsolutePath() + "/" + recordFile);

        //Stop media recorder and set it to null for further use to record new audio
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        callSaveRecordingAPI();


    }

    private void callSaveRecordingAPI() {

        File file = new File(getExternalFilesDir("/").getAbsolutePath() + "/" + recordFile);
        String name = file.getName();

        String durationTime = AudioRecordingListActivity.getDuration(file);
        int duration = getTime(durationTime);

        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String date = in.format(currentDate);

        Log.e(TAG, "name:>>>>>>>>>" + name);
        Log.e(TAG, "duration:>>>>>>>>>" + duration);
        Log.e(TAG, "date:>>>>>>>>>" + date);

        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.ADD_RECORDING)
            .addHeaders("Authorization", sessionManager.getAuthToken())
            .addHeaders("id", sessionManager.getUserID())
            .addBodyParameter("recording_name", name)
            .addBodyParameter("recording_time", duration + "")
            .addBodyParameter("recording_date_time", date)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    try {
                        if (response.getString("responseCode").equalsIgnoreCase("200")) {
                            mLoadingBar.showContent();
                            MyToast.display(context, "" + response.getString("responseMessage"));
                            finish();
                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(RecordingAudioActivity.this);
                        }
                        else {
                            mLoadingBar.showContent();
                            MyToast.display(context, "" + response.getString("responseMessage"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError error) {
                    Log.e(TAG, "onError:>>>>" + error.getMessage());
                    mLoadingBar.showRetry();
                }
            });
    }

    public static void toStopLoudspeaker() {
        RecordFragment.isLoudSpeakerOn = false;
        RecordFragment.isRecording = false;
        RecordFragment.isRecordOn = false;
        RecordFragment.loudspeaker_status = 0;

    }

    @Override
    public void  onBackPressed() {
        super.onBackPressed();
        toStopLoudspeaker();
        stopRecording();
        isRecording = false;
        if (RecordFragment.player!=null) {
            RecordFragment.player.stop();
            RecordFragment.player.release();
            RecordFragment.player = null;
        }
        startActivity(new Intent(RecordingAudioActivity.this, RecordFragment.class));
        finish();
    }


    @Override
    public void onRetryClick() {

    }

    private int getTime(String time) {
        String[] t = time.split(":");
        int h = Integer.parseInt(t[0]);
        int m = Integer.parseInt(t[1]);
        int s = Integer.parseInt(t[2]);
        int totalTime = m + (h * 60);
        return totalTime;
    }

}