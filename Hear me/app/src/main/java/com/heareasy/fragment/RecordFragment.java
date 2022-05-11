package com.heareasy.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.heareasy.activity.AudioRecordingListActivity;
import com.heareasy.activity.DashBoardActivity;
import com.heareasy.audio_playback_capture.PCMToMp3Encoder;
import com.heareasy.audio_playback_capture.RecorderService;
import com.heareasy.others.AnonymousClass2;
import com.heareasy.others.MyBroadcastReceiver;
import com.heareasy.R;
import com.heareasy.others.AvailableStorageActivity;
import com.heareasy.audio.AudioRecordingHandler;
import com.heareasy.audio.AudioRecordingThread;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.others.WelcomePref;

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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;
import static com.heareasy.R.id.bnav_home;
import static com.heareasy.R.id.bnav_speaker;
import static com.heareasy.R.id.bottomLayout;
import static com.heareasy.R.id.bottom_navigation;
import static com.heareasy.R.id.nav_home;
import static com.heareasy.R.id.navigation_header_container;
import static com.heareasy.R.id.seekBar;
import static com.heareasy.audio_playback_capture.Constants.ACTION_START;
import static com.heareasy.audio_playback_capture.Constants.ACTION_STOP;
import static com.heareasy.audio_playback_capture.Constants.CAPTURE_MEDIA_PROJECTION_REQUEST_CODE;
import static com.heareasy.audio_playback_capture.Constants.DECODE_BIT_RATE;
import static com.heareasy.audio_playback_capture.Constants.DECODE_CHANNELS_COUNT;
import static com.heareasy.audio_playback_capture.Constants.DECODE_SAMPLE_RATE;
import static com.heareasy.audio_playback_capture.Constants.EXTRA_RESULT_DATA;
import static com.heareasy.audio_playback_capture.Constants.FORMAT_DATE_FULL;
import static com.heareasy.audio_playback_capture.Constants.FORMAT_RECORDING_DECODED;
import static com.heareasy.audio_playback_capture.Constants.FORMAT_RECORDING_ENCODED;
import static com.heareasy.audio_playback_capture.Constants.PREFERENCES_APP_NAME;
import static com.heareasy.audio_playback_capture.Constants.PREFERENCES_KEY_OUTPUT_DIRECTORY;
import static com.heareasy.audio_playback_capture.Constants.RECORDING_DEFAULT_NAME;
import static com.heareasy.audio_playback_capture.Constants.RECORD_AUDIO_PERMISSION_REQUEST_CODE;
import static com.heareasy.activity.DashBoardActivity.MY_PREFS_NAME;

public class RecordFragment extends Fragment implements MyStateView.ProgressClickListener {
    private static final String TAG = "RecordActivity";

    private static final int AUDIO_REQUEST_CODE = 1001;
    private Toolbar toolbar_record_audio;
    private static final int RECORDER_SAMPLERATE = 44100;
    public static AudioManager am = null;
    int audioInLength = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, 16, 2);
    int audioOutLength = AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, 4, 2);
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
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

    public static AudioRecordingThread recordingThread = null;

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


    public static TextView tv_filenameText, tv_indicate;
    public static Chronometer timer;
    public static CircleImageView img_recordAudio, btn_recordAudio;
    private MyStateView mLoadingBar;
    private CardView cardSwitch;
    private RadioButton modeRadioButton, btnKaraoke, btnLecture;
    private RadioGroup modeRadioGroup;
    private Button btn_testSound, btn_recordedFiles;
    public static int checkSound;
    private WelcomePref welcomePref;

    private MediaProjectionManager mediaProjectionManager;
    private boolean recording = false;
    private File directory;

    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager;
    private ProgressBar spinner;
    private ProgressDialog progressDialog;
    private SimpleDateFormat df;
    public static BottomNavigationView mbottomNavigationView;
    private static String DB_PATH;
    AlertDialog.Builder builder;



    /* access modifiers changed from: protected */
 /*   @SuppressLint("WrongConstant")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_record);

        checkPermission();

        sessionManager = new SessionManager(this);
        mLoadingBar = new MyStateView(this, null);
        tv_check_available_storage = findViewById(R.id.tv_check_storage);
        toolbar_record_audio = findViewById(R.id.toolbar_record_audio);
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


        tv_check_available_storage.setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.btnKaraoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });
        findViewById(R.id.btnLecture).setOnClickListener(new View.OnClickListener() {
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
        checkConnected();
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
    }*/

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_record, container, false);
        welcomePref = new WelcomePref(getActivity());
        checkPermission();
        boolean hasLowLatencyFeature =
                getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);

        boolean hasProFeature =
                getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO);

        Log.e("hasLowLatencyFeature", ">>>>>>>>>" + hasLowLatencyFeature);
        Log.e("hasProFeature", ">>>>>>>>>" + hasProFeature);
        sessionManager = new SessionManager(getActivity());
        mLoadingBar = new MyStateView(this, view);

        df = new SimpleDateFormat(FORMAT_DATE_FULL, Locale.US);
        tv_check_available_storage = view.findViewById(R.id.tv_check_storage);
//        toolbar_record_audio = view.findViewById(R.id.toolbar_record_audio);
        timer = view.findViewById(R.id.record_timer);
        tv_filenameText = view.findViewById(R.id.tv_filenameText);
        tv_indicate = view.findViewById(R.id.tv_indicate);
        img_recordAudio = view.findViewById(R.id.img_recordAudio);
        btn_recordAudio = view.findViewById(R.id.btn_recordAudio);
        modeRadioGroup = view.findViewById(R.id.rg_mode);
        btnKaraoke = view.findViewById(R.id.btnKaraoke);
        btnLecture = view.findViewById(R.id.btnLecture);
        btn_testSound = view.findViewById(R.id.btn_testSound);
        btn_recordedFiles = view.findViewById(R.id.btn_recordedFiles);
        spinner = view.findViewById(R.id.progressBar1);
        mbottomNavigationView = view.findViewById(R.id.bottom_navigation);

//        toolbar_record_audio.setNavigationIcon(R.drawable.ic_arrow_back);
//        toolbar_record_audio.setNavigationOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                onBackPressed();
//            }
//        });
        editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//        prefTarget = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        DashBoardActivity.tv_toolbar_tittle.setText("Record Audio");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait while file is rendering...");

        /*String manufacturer = "xiaomi";
        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }*/

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
        } else {
            btnLecture.setChecked(true);
        }
        sessionManager.setKaraokeMode(false);


        view.findViewById(R.id.btnKaraoke).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });
        view.findViewById(R.id.btnLecture).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });
        btn_testSound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestAudioPermissions()) {
                    if (MyBroadcastReceiver.connectedBluetooth || welcomePref.isConnectedDevice()) {
                        testLoudSpeakerSound();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please go to Settings to connect a bluetooth speaker")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //finish();
                                        /* startActivity(new Intent(getActivity(), DashBoardActivity.class)
                                .putExtra("tag", "speaker"));*/

                                    }
                                });
                        AlertDialog alertdialog = builder.create();
                        alertdialog.setTitle("Bluetooth Speaker not connected");
                        alertdialog.show();
                    }

                }
            }
        });

        btn_recordedFiles.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AudioRecordingListActivity.class));
            }
        });

        initControls(view);


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

        this.btn_recordAudio = view.findViewById(R.id.btn_recordAudio);
      // checkBluetoothConnected();
        checkConnected();
        TelephonyManager telephonyManager2 = (TelephonyManager) getActivity().getSystemService("phone");
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
                        MyToast.display(getActivity(), "phone call-in, bluetooth will disable now");
                        if (RecordFragment.this.isLoudSpeakerOn) {
                            RecordFragment.this.toStopLoudspeaker();
                        } else if (RecordFragment.this.isPlaying) {
                            RecordFragment.this.toStopPlay();
                        }
                        if (RecordFragment.this.isPlayingHB) {
                            RecordFragment.this.stopMyMusic();
                        }
                        if (getActivity().getSharedPreferences("mySettings", 0).getBoolean("autochgblu", true)) {
                            RecordFragment.this.bluetooth = BluetoothAdapter.getDefaultAdapter();
                            if (RecordFragment.this.bluetooth != null && RecordFragment.this.bluetooth.isEnabled()) {
                                RecordFragment.this.bluetooth.disable();
                            }
                        }
                    }
                }
            };
            this.callStateListener = r6;
            this.telephonyManager.listen(r6, 32);
        }
        if (this.am == null) {
            @SuppressLint("WrongConstant") AudioManager audioManager = (AudioManager) getActivity().getSystemService("audio");
            String sampleRateStr = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int sampleRate = Integer.parseInt(sampleRateStr);
            if (sampleRate == 0) sampleRate = 44100;
            Log.e("sample rate", ">>>>>>>>>>>" + sampleRate);
            String framesPerBuffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int framesPerBufferInt = Integer.parseInt(framesPerBuffer);
            Log.e("framesPerBufferInt", ">>>>>>>>>>>" + framesPerBufferInt);

            this.am = audioManager;
            audioManager.setMode(3);
        }
        boolean z = getActivity().getSharedPreferences("mySettings", 0).getBoolean("autochgvol", true);


        AudioManager audioManager2 = this.am;
        btn_recordAudio.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                if (requestAudioPermissions()) {
                    if (MyBroadcastReceiver.connectedBluetooth || welcomePref.isConnectedDevice()) {
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
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                toStartLoudSpeaker();
                            }else{
                                MyToast.display(getActivity(),"Recording requires Android version 10 or later");
                            }

                        }
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please go to Settings to connect a bluetooth speaker")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //finish();
                                        /* startActivity(new Intent(getActivity(), DashBoardActivity.class)
                                .putExtra("tag", "speaker")
                 =);*/
                                    }
                                });
                        AlertDialog alertdialog = builder.create();
                        alertdialog.setTitle("Bluetooth Speaker not connected");
                        alertdialog.show();
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
                        RecordFragment.this.updateHeadSetDeviceInfo(context, intent);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            getActivity().registerReceiver(this.myBroadcastReceiver, intentFilter);
        }
        return view;
    }

    public void onclickbuttonMethod(View v) {
        int selectedId = modeRadioGroup.getCheckedRadioButtonId();
        modeRadioButton = v.findViewById(selectedId);
        if (selectedId == -1) {
            Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
        } else {
            if (modeRadioButton.getText().toString().equalsIgnoreCase("Karaoke Mode")) {
                if (modeRadioButton.isChecked()) {
                    sessionManager.setKaraokeMode(true);
                    btnKaraoke.setChecked(true);
                    btnLecture.setChecked(false);
                } else {
                    sessionManager.setKaraokeMode(false);
                    btnLecture.setChecked(true);
                    btnKaraoke.setChecked(false);
                }

            } else if (modeRadioButton.getText().toString().equalsIgnoreCase("Lecture Mode")) {
                if (modeRadioButton.isChecked()) {
                    sessionManager.setKaraokeMode(false);
                    btnLecture.setChecked(true);
                    btnKaraoke.setChecked(false);
                } else {
                    sessionManager.setKaraokeMode(true);
                    btnKaraoke.setChecked(true);
                    btnLecture.setChecked(false);
                }
            }
        }

    }

 /*   @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(RecordActivity.this, DashBoardActivity.class));
//        finishAffinity();
        toStartLoudSpeaker();
    }*/

    private void initControls(View view) {
        try {
            volumeSeekbar = (SeekBar) view.findViewById(seekBar);

            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.e("onStartTrackingTouch", "onStartTrackingTouch>>>>>>>:" + seekBar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            this.am = (AudioManager) getActivity().getSystemService("audio");
        }
        int i = this.loudspeaker_status;
        if (i == 1 || i == 0) {
            this.am.setSpeakerphoneOn(false);
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
        //bottomNavigationView.getMenu().findItem(nav_home).setEnabled(false);

        try {
            Thread.sleep(200);
        } catch (Exception unused) {
        }
        if (this.am == null) {
            this.am = (AudioManager) getActivity().getSystemService("audio");
        }
        int i = this.loudspeaker_status;
        if (i == 1 || i == 0) {
            this.am.setSpeakerphoneOn(false);
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
        checkSound = 0;
        if (this.isLoudSpeakerOn || !this.isPermit) {
            if (SDK_INT >= 23) {
                voiceFromBuiltinMic();

            }
            toStopSpeaker();
            btn_testSound.setText("Test Audio Out");
            return;
        }
        isLoudSpeakerOn = true;
        loudspeaker_status = 1;
        btn_testSound.setText("Off");



        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {


                loudSpeaker();
                return null;
            }
        }.execute(new Void[0]);

    }

    public void toStartLoudSpeaker() {

        if (this.isLoudSpeakerOn || !this.isPermit) {
            if (SDK_INT >= 23) {
               voiceFromBuiltinMic();
            }
            toStopLoudspeaker();

            btn_testSound.setText("Test Audio Out");

            return;
        }
        btn_recordedFiles.setEnabled(false);
        btn_testSound.setEnabled(false);
       // mbottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        timer.setVisibility(View.VISIBLE);
        tv_filenameText.setVisibility(View.VISIBLE);

        img_recordAudio.setImageResource(R.drawable.mic_button_2);
        btn_recordAudio.setImageResource(R.drawable.off_button);
        tv_filenameText.setText(recordFileName);
        tv_indicate.setText("Press the button to stop recording");

//                this.iv_wave.startAnimation(this.aniFade);
        new AsyncTask<Void, Void, Void>() {
            //             access modifiers changed from: protected
            public Void doInBackground(Void... voidArr) {
               // loudSpeaker();
               startCapturing();
                return null;
            }
        }.execute(new Void[0]);

    }


    // Method to stop loudspeaker
    public void toStopLoudspeaker() {
        btn_recordedFiles.setEnabled(true);
        btn_testSound.setEnabled(true);
//        spinner.setVisibility(View.VISIBLE);
        progressDialog.show();

        timer.setVisibility(View.GONE);
        tv_filenameText.setVisibility(View.GONE);
        img_recordAudio.setImageResource(R.drawable.mic_button);
        btn_recordAudio.setImageResource(R.drawable.on_button);
        tv_indicate.setText("Press the button to start recording");
        btn_testSound.setText("Test Audio Out");
        timer.stop();
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
        new AsyncTask<Void, Void, Void>() {
            //             access modifiers changed from: protected
            public Void doInBackground(Void... voidArr) {
                stopCapturing();

                return null;
            }
        }.execute(new Void[0]);
    }

    public static void toStopSpeaker() {
        if (am != null) {
            am.setSpeakerphoneOn(false);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(getActivity(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == AUDIO_REQUEST_CODE && resultCode == RESULT_OK) {

            //the selected audio.Do some thing with uri

            Uri audio_uri = data.getData();
            Log.e("audio_uri", "audio_uri:>>>>>>>>>>>" + audio_uri);
            try {

                float v = (float) 0.5;
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getActivity(), audio_uri);
                player.setVolume(v, v);
                player.prepare();
                player.start();
                toStartLoudSpeaker();

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        if (requestCode == CAPTURE_MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
               /* Toast.makeText(getActivity(),
                        "MediaProjection permission obtained. Foreground service will be started to capture audio.",
                        Toast.LENGTH_SHORT
                ).show();*/
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();

                isLoudSpeakerOn = true;
                loudspeaker_status = 1;
                checkSound = 1;
                Intent audioCaptureIntent = new Intent(getActivity(), RecorderService.class);
                audioCaptureIntent.setAction(ACTION_START);
                audioCaptureIntent.putExtra(EXTRA_RESULT_DATA, data);
                getActivity().startForegroundService(audioCaptureIntent);
                Log.e(TAG, "karoake result:>>>>>");
                loudSpeaker();
            } else {
                btn_recordedFiles.setEnabled(true);
                btn_testSound.setEnabled(true);
//      spinner.setVisibility(View.VISIBLE);

                timer.setVisibility(View.GONE);
                tv_filenameText.setVisibility(View.GONE);
                img_recordAudio.setImageResource(R.drawable.mic_button);
                btn_recordAudio.setImageResource(R.drawable.on_button);
                tv_indicate.setText("Press the button to start recording");
                btn_testSound.setText("Test Audio Out");
                timer.stop();
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
               /* Toast.makeText(getActivity(),
                        "Request to obtain MediaProjection denied.",
                        Toast.LENGTH_SHORT
                ).show();*/
            }
        }

    }


    @SuppressLint("WrongConstant")
    public boolean requestAudioPermissions() {
        String str = "android.permission.RECORD_AUDIO";
        if (ContextCompat.checkSelfPermission(getActivity(), str) != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), str)) {
                MyToast.display(getActivity(), "Please grant permission to use device mic");
                ActivityCompat.requestPermissions(getActivity(), new String[]{str}, 101);
                return false;
            }
            ActivityCompat.requestPermissions(getActivity(), new String[]{str}, 101);
            return false;
        } else if (ContextCompat.checkSelfPermission(getActivity(), str) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       /* if (requestCode == 101) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                this.isPermit = false;
            } else {
                this.isPermit = true;
            }
        }*/

        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),
                        "Permissions to capture audio granted. Click the button once again.",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(getActivity(),
                        "Permissions to capture audio denied.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }

        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            case 2296:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Toast.makeText(getActivity(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        recordFileName = "Recording_" + formatter.format(now);

        SharedPreferences pref = getActivity().getSharedPreferences("MyDevice", 0);
        isAlreadyConnected = pref.getBoolean("deviceConnected", false);
        //checkBluetoothConnected();
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
                getActivity().unregisterReceiver(this.myBroadcastReceiver);
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
                this.am = (AudioManager) getActivity().getSystemService("audio");
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

            //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
            directory = ContextCompat.getExternalFilesDirs(getActivity(), Environment.DIRECTORY_MUSIC)[0];
            fileName = directory + "/" + recordFileName +".mp3";
          //  recordFileName = "Recording_" + formatter.format(now) + ".mp3";


            recordingThread = new AudioRecordingThread(fileName, new AudioRecordingHandler() {
                @Override
                public void onFftDataCapture(final byte[] bytes) {
                    getActivity().runOnUiThread(new Runnable() {
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
                              //  callSaveRecordingAPI();
                                MyToast.display(getActivity(), "File Saved");
                                Log.e("filebyte", ">>>>>>>>>>" + audio);

                            }
                        }
                    });
                }

                @Override
                public void onRecordingError() {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }

                @Override
                public void onRecordSaveError() {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            });
            recordingThread.start();


        } catch (Exception unused) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    RecordFragment.this.isLoudSpeakerOn = false;
                    RecordFragment.this.isRecording = false;
                    RecordFragment.this.isRecordOn = false;
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
            Log.e("filebyte>>>>>>>", ">>>>>>>>>>" + audioBytes);
            return audioBytes;
        } catch (FileNotFoundException e) {
            Log.e("filenotfound>>>>>>>>", ">>>>>>>>>>>");
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

        Log.e(TAG, "name:>>>>>>>>>>>>>>>>>" + name);
        Log.e(TAG, "duration:>>>>>>>>>>>>>" + duration);
        Log.e(TAG, "date:>>>>>>>>>>>>>>>>>" + date);

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
                        Log.e(TAG, "onResponse:>>>>>>>>>>>>>>>.>>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                MyToast.display(getActivity(), "" + response.getString("responseMessage"));
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(getActivity());
                            } else {
                                mLoadingBar.showContent();
                                MyToast.display(getActivity(), "" + response.getString("responseMessage"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError:>>>>>" + error.getMessage());
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
        //true == headset connected && connected headset is support hands free
        int state = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
        if (state != BluetoothProfile.STATE_CONNECTED)
            return;

        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(getActivity(), serviceListener1, BluetoothProfile.HEADSET);
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
           SharedPreferences pref = getActivity().getSharedPreferences("MyDevice", 0);
           //SharedPreferences pref = Objects.requireNonNull(getActivity()).getSharedPreferences("MyDevice", 0);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("deviceConnected", true);
            editor.apply();
            welcomePref.setConnectedDevice(true);

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
            int result = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getActivity().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        }
    }






    private void decodeCapture() {
        Log.e(TAG, "decodeCapture: >>>>>>>>>>>>>");
    /*   String outputDirectory = getSharedPreferences(PREFERENCES_APP_NAME, MODE_PRIVATE)
                .getString(PREFERENCES_KEY_OUTPUT_DIRECTORY, "");
        if(outputDirectory == null || outputDirectory.isEmpty()) {
            outputDirectory = ContextCompat.getExternalFilesDirs(getContext(), Environment.DIRECTORY_MUSIC)[0].toString();
        }*/

        String outputDirectory = ContextCompat.getExternalFilesDirs(getActivity(), Environment.DIRECTORY_MUSIC)[0].toString();
        Log.e(TAG, "decodeCapturedeepak: >>>>>>>>>>>>>"+outputDirectory);

        //String outputDirectory = directory.toString();
       Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        String decodedRecording = String.format(
                FORMAT_RECORDING_DECODED,
                outputDirectory,
                recordFileName

        );

        Log.e(TAG, "decodeCapturedeepak: >>>>>>>>>>>>>"+decodedRecording);
        //"Recording_"+df.format(new Date(System.currentTimeMillis()))
       String.format("Recording_%s", df.format(new Date(System.currentTimeMillis())));

  /*      PCMToMp3Encoder.init(
                String.format(FORMAT_RECORDING_ENCODED, outputDirectory, RECORDING_DEFAULT_NAME),
                DECODE_CHANNELS_COUNT,
                DECODE_BIT_RATE,
                DECODE_SAMPLE_RATE,
                decodedRecording
        );*/

        PCMToMp3Encoder.encode();
        PCMToMp3Encoder.destroy();
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MyToast.display(getActivity(), "Your file has been saved successfully");
//               spinner.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
       // playerManager.setAudio(decodedRecording);
    }

    public void startCapturing() {
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission();

           // disable menu if user not logged in


        } else {
            startMediaProjectionRequest();
        }
    }

    private void startMediaProjectionRequest() {
        mediaProjectionManager = (MediaProjectionManager) getActivity()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                CAPTURE_MEDIA_PROJECTION_REQUEST_CODE
        );
    }

    private void stopCapturing() {
     /*   Intent serviceIntent = new Intent(getActivity(), RecorderService.class);
        serviceIntent.setAction(ACTION_STOP);
        getActivity().startService(serviceIntent);*/
        Intent serviceIntent = new Intent(RecordFragment.this.getActivity(),RecorderService.class);
        serviceIntent.setAction(ACTION_STOP);
        getActivity().startService(serviceIntent);
        decodeCapture();
    }

    private boolean isRecordAudioPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordAudioPermission() {

            ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                },
                RECORD_AUDIO_PERMISSION_REQUEST_CODE
        );
    }
}
