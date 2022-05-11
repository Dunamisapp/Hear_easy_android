package com.heareasy.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.audio.AudioRecordingHandler;
import com.heareasy.audio.AudioRecordingThread;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.fragment.BluetoothDeviceFragment;
import com.heareasy.others.AnonymousClass2;
import com.heareasy.others.AvailableStorageActivity;
import com.heareasy.others.MyBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.heareasy.activity.DashBoardActivity.MY_PREFS_NAME;
import static com.heareasy.common_classes.AppConstants.directory;


public class RecordActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "RecordActivity";

    private static final int AUDIO_REQUEST_CODE = 1001;
    private Toolbar toolbar_record_audio;
    private static final int RECORDER_SAMPLERATE = 44100;
    AudioManager am = null;
    int audioInLength = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, 16, 2);
    int audioOutLength = AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, 4, 2);
    private AudioTrack audioTrack = null;
    private AudioTrack audioTrack2 = null;

    public BluetoothAdapter bluetooth = null;
    private AcousticEchoCanceler canceler = null;
    public boolean isBluConnected = false;
    boolean isHeadsetPlugin = false;
    public static boolean isLoudSpeakerOn = false;

    public boolean isPermit = true;

    public boolean isPlaying = false;

    public boolean isPlayingDB = false;

    public boolean isPlayingHB = false;

    public static boolean isRecordOn = false;

    public boolean isRecorded = false;

    private SharedPreferences.Editor editor;
    //    private SharedPreferences prefTarget;
    private Rect droidTarget;
    private SpannableString sassyDesc;

    public static boolean isRecording = false;
    private AnonymousClass2 callStateListener;

    public static int loudspeaker_status = 0;
    // private AdView mAdView;
    MediaPlayer mp = null;
    // MyBroadcastReceiver myBroadcastReceiver;
    private NoiseSuppressor ns = null;
    private int originalVol;
    private MyBroadcastReceiver myBroadcastReceiver;
    private boolean phoneAvailable = false;

    public Thread playbackThread = null;

    public Thread playbackThreadDB = null;

    public Thread playbackThreadHB = null;
    private AudioRecord recorder = null;

    public AudioRecordingThread recordingThread = null;

    private int targetSdkVersion;
    private TelephonyManager telephonyManager;
    private String writeFileName = "";
    private Throwable th;
    private TextView tv_check_available_storage;
    private boolean isAlreadyConnected = false;
    public static boolean checkConnectedBluetooth = false;
    private SessionManager sessionManager;
    public static MediaPlayer player;
    private File dir;
    private String recordFileName = "";
    private String fileName = "";


    private TextView tv_filenameText, tv_indicate;
    public static Chronometer timer;
    private CircleImageView img_recordAudio, btn_recordAudio;
    private MyStateView mLoadingBar;
    private CardView cardSwitch;
    private RadioButton modeRadioButton,btnKaraoke,btnLecture;
    private RadioGroup modeRadioGroup;
    private Button btn_testSound,btn_recordedFiles;

    /* access modifiers changed from: protected */
    @SuppressLint("WrongConstant")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_record);

        checkPermission();

        sessionManager = new SessionManager(this);
        mLoadingBar = new MyStateView(this, null);
        tv_check_available_storage = findViewById(R.id.tv_check_storage);
      //  toolbar_record_audio = findViewById(R.id.toolbar_record_audio);
        timer = findViewById(R.id.record_timer);
        tv_filenameText = findViewById(R.id.tv_filenameText);
        tv_indicate = findViewById(R.id.tv_indicate);
        img_recordAudio = findViewById(R.id.img_recordAudio);
        btn_recordAudio = findViewById(R.id.btn_recordAudio);
        modeRadioGroup = findViewById(R.id.rg_mode);
        btnKaraoke = findViewById(R.id.btnKaraoke);
        btnLecture = findViewById(R.id.btnLecture);
        btn_testSound = findViewById(R.id.btn_testSound);
        btn_recordedFiles = findViewById(R.id.btn_recordedFiles);
        toolbar_record_audio.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_record_audio.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//        prefTarget = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        tv_check_available_storage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AvailableStorageActivity availableStorageActivity = new AvailableStorageActivity();
                String storage = availableStorageActivity.storage();
                tv_check_available_storage.setText("Available Storage: " + storage);
            }
        });


        if (sessionManager.isKaraokeMode()) {
            btnKaraoke.setChecked(true);
        }else {
            btnLecture.setChecked(true);
        }

        findViewById(R.id.btnKaraoke).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });
        findViewById(R.id.btnLecture).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });
        btn_testSound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyBroadcastReceiver.connectedBluetooth || checkConnectedBluetooth) {
                 testLoudSpeakerSound();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                    builder.setTitle("Bluetooth Speaker not connected");
                    builder.setMessage("Please go to Settings to connect a bluetooth speaker");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RecordActivity.this, DashBoardActivity.class)
                                .putExtra("tag", "speaker"));
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });

        btn_recordedFiles.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordActivity.this, AudioRecordingListActivity.class));
            }
        });


        // TODO Audio recording path

//        dir = new File(Environment.getExternalStorageDirectory() + "/" + "Hear Me", "Recording");
//         dir = getApplicationContext().getExternalFilesDir("");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFileName = "Recording_" + formatter.format(now) + ".mp3";
        fileName = directory + "/" + recordFileName;

        this.btn_recordAudio = findViewById(R.id.btn_recordAudio);
//        checkBluetoothConnected();
        new BluetoothDeviceFragment().checkConnected();
        TelephonyManager telephonyManager2 = (TelephonyManager) getSystemService("phone");
        this.telephonyManager = telephonyManager2;
        if (telephonyManager2.getPhoneType() == 0) {
            this.phoneAvailable = false;
        } else {
            this.phoneAvailable = true;
        }
        if (this.phoneAvailable) {
            AnonymousClass2 r6 = new AnonymousClass2() {
                public void onCallStateChanged(int i, String str) {
                    if (i == 1) {
                        MyToast.display(RecordActivity.this.getApplicationContext(), "phone call-in, bluetooth will disable now");
                        if (RecordActivity.this.isLoudSpeakerOn) {
                            RecordActivity.this.toStopLoudspeaker();
                        } else if (RecordActivity.this.isPlaying) {
                            RecordActivity.this.toStopPlay();
                        }
                        if (RecordActivity.this.isPlayingHB) {
                            RecordActivity.this.stopMyMusic();
                        }
                        if (RecordActivity.this.getSharedPreferences("mySettings", 0).getBoolean("autochgblu", true)) {
                            RecordActivity.this.bluetooth = BluetoothAdapter.getDefaultAdapter();
                            if (RecordActivity.this.bluetooth != null && RecordActivity.this.bluetooth.isEnabled()) {
                                RecordActivity.this.bluetooth.disable();
                            }
                        }
                    }
                }
            };
            this.callStateListener = r6;
            this.telephonyManager.listen(r6, 32);
        }
        if (this.am == null) {
            @SuppressLint("WrongConstant") AudioManager audioManager = (AudioManager) getSystemService("audio");
            this.am = audioManager;
            audioManager.setMode(3);
        }
        boolean z = getSharedPreferences("mySettings", 0).getBoolean("autochgvol", true);


        AudioManager audioManager2 = this.am;
        btn_recordAudio.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (requestAudioPermissions()) {
                    if (MyBroadcastReceiver.connectedBluetooth || checkConnectedBluetooth) {
                        if (sessionManager.isKaraokeMode()) {
                            if (!isLoudSpeakerOn) {
                                Intent intent_upload = new Intent();
                                intent_upload.setType("audio/*");
                                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent_upload, AUDIO_REQUEST_CODE);
                            } else {
                                toStopLoudspeaker();
                            }
                        } else {
                            toStartLoudSpeaker();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                        builder.setTitle("Bluetooth Speaker not connected");
                        builder.setMessage("Please go to Settings to connect a bluetooth speaker");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(RecordActivity.this, DashBoardActivity.class)
                                    .putExtra("tag", "speaker"));
                                finish();
                            }
                        });
                        builder.show();
                    }
                }

            }
        });

        if (SDK_INT >= 23) {
            boolean findHeadset = findHeadset();
            this.isHeadsetPlugin = findHeadset;
            this.myBroadcastReceiver = new MyBroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.HEADSET_PLUG")) {
                        RecordActivity.this.updateHeadSetDeviceInfo(context, intent);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            registerReceiver(this.myBroadcastReceiver, intentFilter);
        }

    }

    public void onclickbuttonMethod(View v){
        int selectedId = modeRadioGroup.getCheckedRadioButtonId();
        modeRadioButton =  v.findViewById(selectedId);
        if(selectedId==-1){
            Toast.makeText(RecordActivity.this,"Nothing selected", Toast.LENGTH_SHORT).show();
        }
        else{
            if (modeRadioButton.getText().toString().equalsIgnoreCase("Karaoke Mode")){
                if (modeRadioButton.isChecked()) {
                    sessionManager.setKaraokeMode(true);
                    btnKaraoke.setChecked(true);
                    btnLecture.setChecked(false);
                } else {
                    sessionManager.setKaraokeMode(false);
                    btnLecture.setChecked(true);
                    btnKaraoke.setChecked(false);
                }

            }else if (modeRadioButton.getText().toString().equalsIgnoreCase("Lecture Mode")) {
                if (modeRadioButton.isChecked()){
                    sessionManager.setKaraokeMode(false);
                    btnLecture.setChecked(true);
                    btnKaraoke.setChecked(false);
                }else {
                    sessionManager.setKaraokeMode(true);
                    btnKaraoke.setChecked(true);
                    btnLecture.setChecked(false);
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(RecordActivity.this, DashBoardActivity.class));
//        finishAffinity();
    }

    @SuppressLint("WrongConstant")
    public boolean findHeadset() {
        if (SDK_INT >= 23) {
            AudioManager audioManager = this.am;
            if (audioManager != null) {
                for (AudioDeviceInfo type : audioManager.getDevices(1)) {
                    if (type.getType() == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateHeadSetDeviceInfo(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("state", -1);
        intent.getStringExtra("name");
        int intExtra2 = intent.getIntExtra("microphone", 0);
        if (intExtra != 0) {
            if (intExtra == 1 && intExtra2 == 1) {
                this.isHeadsetPlugin = true;
                if (this.isLoudSpeakerOn) {
                    voiceFromHeadset();
                }
            }
        } else if (this.isHeadsetPlugin) {
            this.isHeadsetPlugin = false;
            if (this.isLoudSpeakerOn) {
                voiceFromBuiltinMic();
            }
        }
    }


    @SuppressLint("WrongConstant")
    public AudioDeviceInfo getMyAudioDeviceInfo_wiredMic() {
        AudioDeviceInfo[] devices;
        AudioManager audioManager = this.am;
        AudioDeviceInfo audioDeviceInfo = null;
        if (audioManager != null) {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(1)) {
                    if (audioDeviceInfo2.getType() == 3) {
                        audioDeviceInfo = audioDeviceInfo2;
                    }
                }
            }
        }
        return audioDeviceInfo;
    }


    @SuppressLint("WrongConstant")
    public AudioDeviceInfo getMyAudioDeviceInfo_builtinMic() {
        AudioDeviceInfo[] devices;
        AudioManager audioManager = this.am;
        AudioDeviceInfo audioDeviceInfo = null;
        if (audioManager != null) {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(1)) {
                    if (audioDeviceInfo2.getType() == 15) {
                        audioDeviceInfo = audioDeviceInfo2;
                    }
                }
            }
        }
        return audioDeviceInfo;
    }


    @SuppressLint("WrongConstant")
    public AudioDeviceInfo getMyAudioDeviceInfo_BL_a2dp() {
        AudioDeviceInfo[] devices;
        AudioManager audioManager = this.am;
        AudioDeviceInfo audioDeviceInfo = null;
        if (audioManager != null) {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(2)) {
                    if (audioDeviceInfo2.getType() == 8) {
                        audioDeviceInfo = audioDeviceInfo2;
                    }
                }
            }
        }
        return audioDeviceInfo;
    }


    @SuppressLint("WrongConstant")
    public void voiceFromHeadset() {
        try {
            Thread.sleep(200);
        } catch (Exception unused) {
        }
        if (this.am == null) {
            this.am = (AudioManager) getSystemService("audio");
        }
        int i = this.loudspeaker_status;
        if (i == 1 || i == 0) {
            this.am.setSpeakerphoneOn(true);
            this.am.setMode(3);
        } else if (i == 2) {
            this.am.setMode(2);
            this.am.setSpeakerphoneOn(false);
        }
        if (this.recorder != null) {
            AudioDeviceInfo myAudioDeviceInfo_wiredMic = getMyAudioDeviceInfo_wiredMic();
            if (myAudioDeviceInfo_wiredMic != null) {
                if (SDK_INT >= Build.VERSION_CODES.M) {
                    this.recorder.setPreferredDevice(myAudioDeviceInfo_wiredMic);
                }
                if (this.audioTrack != null) {
                    AudioDeviceInfo myAudioDeviceInfo_BL_a2dp = getMyAudioDeviceInfo_BL_a2dp();
                    if (myAudioDeviceInfo_BL_a2dp != null) {
                        if (SDK_INT >= Build.VERSION_CODES.M) {
                            this.audioTrack.setPreferredDevice(myAudioDeviceInfo_BL_a2dp);
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("WrongConstant")
    public void voiceFromBuiltinMic() {
        try {
            Thread.sleep(200);
        } catch (Exception unused) {
        }
        if (this.am == null) {
            this.am = (AudioManager) getSystemService("audio");
        }
        int i = this.loudspeaker_status;
        if (i == 1 || i == 0) {
            this.am.setSpeakerphoneOn(true);
            this.am.setMode(3);
        } else if (i == 2) {
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(2);
        }
        if (this.recorder != null) {
            AudioDeviceInfo myAudioDeviceInfo_builtinMic = getMyAudioDeviceInfo_builtinMic();
            if (myAudioDeviceInfo_builtinMic != null) {
                if (SDK_INT >= Build.VERSION_CODES.M) {
                    this.recorder.setPreferredDevice(myAudioDeviceInfo_builtinMic);
                }
                if (this.audioTrack != null) {
                    AudioDeviceInfo myAudioDeviceInfo_BL_a2dp = getMyAudioDeviceInfo_BL_a2dp();
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        if (myAudioDeviceInfo_BL_a2dp != null && this.audioTrack.setPreferredDevice(myAudioDeviceInfo_BL_a2dp)) {
//                            setMaxVol();
                        }
                    }
                }
            }
        }
    }


    // Method to start loudspeaker
    @SuppressLint("StaticFieldLeak")
    public void testLoudSpeakerSound() {
        if (this.isLoudSpeakerOn || !this.isPermit) {
            if (SDK_INT >= 23) {
                voiceFromBuiltinMic();
            }
            toStopLoudspeaker();
            return;
        }
        isLoudSpeakerOn = true;
        loudspeaker_status = 1;

    }
    public void toStartLoudSpeaker() {
        if (this.isLoudSpeakerOn || !this.isPermit) {
            if (SDK_INT >= 23) {
                voiceFromBuiltinMic();
            }
            toStopLoudspeaker();
            return;
        }
        timer.setVisibility(View.VISIBLE);
        tv_filenameText.setVisibility(View.VISIBLE);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        img_recordAudio.setImageResource(R.drawable.mic_button_2);
        btn_recordAudio.setImageResource(R.drawable.off_button);
        tv_filenameText.setText(recordFileName);
        tv_indicate.setText("Press the button to start recording");
        isLoudSpeakerOn = true;
        loudspeaker_status = 1;
//                this.iv_wave.startAnimation(this.aniFade);
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                loudSpeaker();
                return null;
            }
        }.execute(new Void[0]);

    }


    // Method to stop loudspeaker
    public void toStopLoudspeaker() {
        timer.setVisibility(View.GONE);
        tv_filenameText.setVisibility(View.GONE);
        tv_indicate.setText("Press the button to stop recording");
        timer.stop();
        img_recordAudio.setImageResource(R.drawable.mic_button);
        btn_recordAudio.setImageResource(R.drawable.on_button);
        isLoudSpeakerOn = false;
        isRecording = false;
        isRecordOn = false;
        loudspeaker_status = 0;
        if (player != null)
            player.stop();
        if (recordingThread != null) {
            recordingThread.stopRecording();
            recordingThread = null;

        }

    }


    public void toStopPlay() {
        this.isPlaying = false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUDIO_REQUEST_CODE && resultCode == RESULT_OK) {

            //the selected audio.Do some thing with uri
            Uri audio_uri = data.getData();
            Log.e("audio_uri", "audio_uri:>>>>>>>>>>>" + audio_uri);
            try {
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(this, audio_uri);
                player.prepare();
                player.start();
                toStartLoudSpeaker();
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }
    }


    @SuppressLint("WrongConstant")
    public boolean requestAudioPermissions() {
        String str = "android.permission.RECORD_AUDIO";
        if (ContextCompat.checkSelfPermission(this, str) != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, str)) {
                MyToast.display(this, "Please grant permission to use device mic");
                ActivityCompat.requestPermissions(this, new String[]{str}, 101);
                return false;
            }
            ActivityCompat.requestPermissions(this, new String[]{str}, 101);
            return false;
        } else if (ContextCompat.checkSelfPermission(this, str) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 101) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                this.isPermit = false;
            } else {
                this.isPermit = true;
            }
        }
    }


    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyDevice", 0);
        isAlreadyConnected = pref.getBoolean("deviceConnected", false);
//        checkBluetoothConnected();
        checkConnected();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.phoneAvailable) {
            this.telephonyManager.listen(this.callStateListener, 0);
        }
        if (this.isPlayingHB) {
            stopMyMusic();
        }
        AudioTrack audioTrack3 = this.audioTrack;
        if (audioTrack3 != null) {
            try {
                audioTrack3.stop();
                this.audioTrack.release();
                this.audioTrack = null;
            } catch (IllegalStateException | NullPointerException unused) {
            }
        }
        AudioRecord audioRecord = this.recorder;
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                this.recorder.release();
                this.recorder = null;
            } catch (IllegalStateException | NullPointerException unused2) {
            }
        }
        if (this.isLoudSpeakerOn) {
            toStopLoudspeaker();
        } else if (this.isPlaying) {
            toStopPlay();
        }
        AcousticEchoCanceler acousticEchoCanceler = this.canceler;
        if (acousticEchoCanceler != null) {
            acousticEchoCanceler.release();
            this.canceler = null;
        }
        NoiseSuppressor noiseSuppressor = this.ns;
        if (noiseSuppressor != null) {
            noiseSuppressor.release();
            this.ns = null;
        }
        if (SDK_INT >= 23) {
            try {
                unregisterReceiver(this.myBroadcastReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        AudioManager audioManager = this.am;
    }


    @SuppressLint("WrongConstant")
    public void loudSpeaker() {
        try {

            if (this.am == null) {
                this.am = (AudioManager) getSystemService("audio");
            }
            int i = this.loudspeaker_status;
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(3);
            if (i == 1) {
                this.am.setSpeakerphoneOn(false);
                this.am.setMode(3);
            } else if (i == 2) {
                this.am.setSpeakerphoneOn(false);
                this.am.setMode(2);
            }
            if (SDK_INT >= 23) {
                if (findHeadset()) {
                    voiceFromHeadset();
                } else if (this.loudspeaker_status == 1) {
                    voiceFromBuiltinMic();
                }
            }

            recordingThread = new AudioRecordingThread(fileName, new AudioRecordingHandler() {
                @Override
                public void onFftDataCapture(final byte[] bytes) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }

                @Override
                public void onRecordSuccess() {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("filename", ">>>>>>>" + fileName);
                            byte[] audio = converwav(fileName);
                            if (audio != null) {
//                                callSaveRecordingAPI();
                                MyToast.display(RecordActivity.this, "File Saved");
                                Log.e("filebyte", ">>>>>>>>>>" + audio);

                            }
                        }
                    });
                }

                @Override
                public void onRecordingError() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }

                @Override
                public void onRecordSaveError() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            });
            recordingThread.start();

        } catch (Exception unused) {
            runOnUiThread(new Runnable() {
                public void run() {

                    RecordActivity.this.isLoudSpeakerOn = false;
                    RecordActivity.this.isRecording = false;
                    RecordActivity.this.isRecordOn = false;
                    Log.e("TAG", ": >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");


                }
            });
        }
    }

    public byte[] converwav(String filepath) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;
        int read;

        try {
            in = new BufferedInputStream(new FileInputStream(filepath));
            byte[] buff = new byte[1024];
            try {
                if (in != null) {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] audioBytes = out.toByteArray();
            Log.e("filebyte", ">>>>>>>>>>" + audioBytes);
            return audioBytes;
        } catch (FileNotFoundException e) {
            Log.e("filenotfound", ">>>>>>>>>>>");
            e.printStackTrace();
            return null;
        }
    }

    // Save recording files to local storage
    private void callSaveRecordingAPI() {

        String name = dir.getName();

        String durationTime = AudioRecordingListActivity.getDuration(dir);
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
                            MyToast.display(RecordActivity.this, "" + response.getString("responseMessage"));
                        } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(RecordActivity.this);
                        } else {
                            mLoadingBar.showContent();
                            MyToast.display(RecordActivity.this, "" + response.getString("responseMessage"));
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

    private int getTime(String time) {
        String[] t = time.split(":");
        int h = Integer.parseInt(t[0]);
        int m = Integer.parseInt(t[1]);
        int s = Integer.parseInt(t[2]);
        int totalTime = m + (h * 60);
        return totalTime;
    }


    public void stopMyMusic() {
        this.isPlayingHB = false;

        MediaPlayer mediaPlayer = this.mp;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                this.mp.release();
                this.mp = null;
            } catch (IllegalStateException unused) {
            }
        }
    }


    public void checkConnected() {
        // true == headset connected && connected headset is support hands free
        int state = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
        if (state != BluetoothProfile.STATE_CONNECTED)
            return;

        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(RecordActivity.this, serviceListener1, BluetoothProfile.HEADSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BluetoothProfile.ServiceListener serviceListener1 = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            checkConnectedBluetooth = false;
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            checkConnectedBluetooth = true;
            SharedPreferences pref = RecordActivity.this.getApplicationContext().getSharedPreferences("MyDevice", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("deviceConnected", true);
            editor.apply();


            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };


    @Override
    public void onRetryClick() {

    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            requestPermission();
            int result = ContextCompat.checkSelfPermission(RecordActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(RecordActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", RecordActivity.this.getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(RecordActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
