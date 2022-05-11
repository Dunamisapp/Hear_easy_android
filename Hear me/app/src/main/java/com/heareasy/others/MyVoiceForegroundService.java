package com.heareasy.others;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;

import com.heareasy.R;
import com.heareasy.fragment.RecordFragment;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyVoiceForegroundService extends Service {
    static final boolean $assertionsDisabled = false;
    public static final String ACTION_START_FG_SERV = "ACTION_START_FG_SERV";
    public static final String ACTION_START_RECORD = "ACTION_START_RECORD";
    public static final String ACTION_STOP_FG_SERV = "ACTION_STOP_FG_SERV";
    public static final String ACTION_STOP_RECORD = "ACTION_STOP_RECORD";
    private static final int AUDIO_ENCODING = 2;
    public static final String CHANNEL_ID = "MyAudioServiceChannel";
    private static final int RECORDER_AUDIO_ENCODING = 2;
    private static final int RECORDER_IN_CHANNEL = 16;
    private static final int RECORDER_OUT_CHANNEL = 4;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int SAMPLERATE = 44100;
    private AudioManager am;
    private byte[] audioBufferByte;
    int audioInLength;
    int audioOutLength;
    private int audioOutPreferred;
    private AudioTrack audioTrack;
    private BufferedOutputStream bufferedOutputStream = null;
    private boolean isLoudSpeakerOn2;
    private boolean isRecordOn2;
    private boolean isRecording2;
    private int loudspeaker_status2;
    private BroadcastReceiver receiver;
    private AudioRecord recorder;
    private Thread recordingThread;
    SharedPreferences settingsPref;
    public boolean to_startListen;
    Handler toastHandler;
    Runnable toastRunnerFail1;
    Runnable toastRunnerFail2;
    Runnable toastRunnerOK;
    private String writePCMFileName;
    private String writeWAVFileName;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onRebind(Intent intent) {
    }

    public boolean onUnbind(Intent intent) {
        return false;
    }

    public MyVoiceForegroundService() {
        String str = "";
        this.writePCMFileName = str;
        this.writeWAVFileName = str;
        this.recorder = null;
        this.audioTrack = null;
        this.audioInLength = AudioRecord.getMinBufferSize(44100, 16, 2);
        this.audioOutLength = AudioTrack.getMinBufferSize(44100, 4, 2);
        this.audioOutPreferred = 1;
        this.to_startListen = false;
        this.loudspeaker_status2 = 0;
        this.recordingThread = null;
        this.isRecordOn2 = false;
        this.isRecording2 = false;
        this.isLoudSpeakerOn2 = false;
    }

    public void onCreate() {
        super.onCreate();
        this.toastHandler = new Handler();
        this.toastRunnerOK = new Runnable() {
            public void run() {
                Toast.makeText(MyVoiceForegroundService.this.getApplicationContext(), "Saving to wav file, please wait.", Toast.LENGTH_SHORT).show();
            }
        };
        this.toastRunnerFail1 = new Runnable() {
            public void run() {
                Toast.makeText(MyVoiceForegroundService.this.getApplicationContext(), "Saving to wav file failed.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                int i3 = -1;
                switch (action.hashCode()) {
                    case -1458609577:
                        if (action.equals(ACTION_START_RECORD)) {
                            i3 = 2;
                            break;
                        }
                        break;
                    case -1170479451:
                        if (action.equals(ACTION_STOP_RECORD)) {
                            i3 = 3;
                            break;
                        }
                        break;
                    case 50857870:
                        if (action.equals(ACTION_START_FG_SERV)) {
                            i3 = 0;
                            break;
                        }
                        break;
                    case 392957184:
                        if (action.equals(ACTION_STOP_FG_SERV)) {
                            i3 = 1;
                            break;
                        }
                        break;
                }
                if (i3 == 0) {
                    startForegroundService();
                    this.loudspeaker_status2 = intent.getIntExtra("audioInPreferred", 1);
                } else if (i3 == 1) {
                    this.isLoudSpeakerOn2 = false;
                    this.isRecording2 = false;
                    this.isRecordOn2 = false;
                    stopForegroundService();
                    this.loudspeaker_status2 = 0;
                } else if (i3 == 2) {
                    this.isRecordOn2 = true;
                } else if (i3 == 3) {
                    this.isRecordOn2 = false;
                }
            }
        }
        return 2;
    }

    @SuppressLint("WrongConstant")
    private void startForegroundService() {
        Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
        if (VERSION.SDK_INT >= 26) {
            this.isLoudSpeakerOn2 = true;
            loudSpeaker();
            return;
        }
        Intent intent = new Intent(this, RecordFragment.class);
        intent.setFlags(131072);
        startForeground(1, new Builder(this, CHANNEL_ID).setContentTitle("Bluetooth Loudspeaker").setContentText("go back to the app to stop microphone usage").setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon)).setSmallIcon(R.drawable.app_icon).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0)).build());
        this.isLoudSpeakerOn2 = true;
        loudSpeaker();
    }

    private void stopForegroundService() {
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        stopForeground(true);
        stopSelf();
    }

    @SuppressLint("WrongConstant")
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = this.am;
        if (audioManager != null) {
            if (audioManager.isBluetoothScoAvailableOffCall() && this.am.isBluetoothScoOn()) {
                this.am.setBluetoothScoOn(false);
                this.am.stopBluetoothSco();
            }
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(0);
        }
    }

    @SuppressLint("WrongConstant")
    private void createNotificationChannel() {
        if (VERSION.SDK_INT >= 26) {
            Intent intent = new Intent(this, RecordFragment.class);
            intent.setFlags(67108864);
            PendingIntent activity = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 134217728);
            String str = CHANNEL_ID;
            ((NotificationManager) getSystemService("notification")).createNotificationChannel(new NotificationChannel(str, "Bluetooth Loudspeaker Fg Serv Channel", 3));
            Builder builder = new Builder(this, str);
            Notification build = builder.setOngoing(true).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon)).setSmallIcon(R.drawable.app_icon).setContentTitle("go back to app to stop using microphone").setPriority(3).setCategory("service").setContentIntent(activity).build();
            NotificationManagerCompat.from(this).notify(1, builder.build());
            startForeground(1, build);
        }
    }


    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        stopSelf();
    }

    @SuppressLint("WrongConstant")
    private void loudSpeaker() {
        try {
            if (this.recorder == null) {
                this.recorder = new AudioRecord(6, 44100, 16, 2, this.audioInLength);
            }
            if (this.audioTrack == null) {
                this.audioTrack = new AudioTrack(3, 44100, 4, 2, this.audioOutLength, 1);
            }
        } catch (Exception unused) {
        }
        if (this.am == null) {
            this.am = (AudioManager) getSystemService("audio");
        }
        int i = this.loudspeaker_status2;
        if (i == 1) {
            this.am.setSpeakerphoneOn(true);
            this.am.setMode(3);
        } else if (i == 2) {
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(2);
        } else if (i == 0) {
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(0);
        }
        if (VERSION.SDK_INT >= 23) {
            if (findHeadset()) {
                voiceFromHeadset();
            } else if (this.loudspeaker_status2 == 1) {
                voiceFromBuiltinMic();
            }
        }
        Thread thread = new Thread(new Runnable() {
            public void run() {
                MyVoiceForegroundService.this.writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        this.recordingThread = thread;
        thread.start();
    }

    @SuppressLint("WrongConstant")
    private boolean findHeadset() {
        if (VERSION.SDK_INT >= 23) {
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
    @SuppressLint("WrongConstant")
    private void voiceFromHeadset() {
        if (this.am == null) {
            this.am = (AudioManager) getSystemService("audio");
        }
        int i = this.loudspeaker_status2;
        if (i == 1) {
            this.am.setSpeakerphoneOn(true);
            this.am.setMode(3);
        } else if (i == 2) {
            this.am.setMode(2);
            this.am.setSpeakerphoneOn(false);
        } else if (i == 0) {
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(0);
        }
        if (this.recorder != null) {
            AudioDeviceInfo myAudioDeviceInputInfo_wiredMic = getMyAudioDeviceInputInfo_wiredMic();
            if (myAudioDeviceInputInfo_wiredMic != null) {
                this.recorder.setPreferredDevice(myAudioDeviceInputInfo_wiredMic);
                if (this.audioTrack != null) {
                    myAudioDeviceInputInfo_wiredMic = getMyAudioDeviceOutputInfo_BL_a2dp();
                    if (myAudioDeviceInputInfo_wiredMic != null) {
                        this.audioTrack.setPreferredDevice(myAudioDeviceInputInfo_wiredMic);
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    public void voiceFromBuiltinMic() {
        if (this.am == null) {
            this.am = (AudioManager) getSystemService("audio");
        }
        int i = this.loudspeaker_status2;
        if (i == 1) {
            this.am.setSpeakerphoneOn(true);
            this.am.setMode(3);
        } else if (i == 2) {
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(2);
        } else if (i == 0) {
            this.am.setSpeakerphoneOn(false);
            this.am.setMode(0);
        }
        if (this.recorder != null) {
            AudioDeviceInfo myAudioDeviceInputInfo_builtinMic = getMyAudioDeviceInputInfo_builtinMic();
            if (myAudioDeviceInputInfo_builtinMic != null) {
                this.recorder.setPreferredDevice(myAudioDeviceInputInfo_builtinMic);
                if (this.audioTrack != null) {
                    myAudioDeviceInputInfo_builtinMic = getMyAudioDeviceOutputInfo_BL_a2dp();
                    if (myAudioDeviceInputInfo_builtinMic != null && this.audioTrack.setPreferredDevice(myAudioDeviceInputInfo_builtinMic)) {
                        setMaxVol();
                    }
                }
            }
        }
    }

    private void setMaxVol() {
        try {
            this.audioTrack.setVolume(AudioTrack.getMaxVolume());
        } catch (IllegalStateException | NullPointerException unused) {
        }
    }

    @SuppressLint("WrongConstant")
    private AudioDeviceInfo getMyAudioDeviceInputInfo_builtinMic() {
        AudioManager audioManager = this.am;
        AudioDeviceInfo audioDeviceInfo = null;
        if (audioManager != null) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    private AudioDeviceInfo getMyAudioDeviceOutputInfo_BL_a2dp() {
        AudioManager audioManager = this.am;
        AudioDeviceInfo audioDeviceInfo = null;
        if (audioManager != null) {
            for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(2)) {
                if (audioDeviceInfo2.getType() == 8) {
                    audioDeviceInfo = audioDeviceInfo2;
                }
            }
        }
        return audioDeviceInfo;
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private AudioDeviceInfo getMyAudioDeviceInputInfo_wiredMic() {
        AudioManager audioManager = this.am;
        AudioDeviceInfo audioDeviceInfo = null;
        if (audioManager != null) {
            for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(1)) {
                if (audioDeviceInfo2.getType() == 3) {
                    audioDeviceInfo = audioDeviceInfo2;
                }
            }
        }
        return audioDeviceInfo;
    }

    private void writeAudioDataToFile() {
        if (VERSION.SDK_INT >= 19) {
            setMaxVol();
        }
        AudioManager audioManager = this.am;
        if (audioManager != null) {
            audioManager.getStreamVolume(3);
        }
        this.bufferedOutputStream = null;
        BufferedOutputStream bufferedOutputStream;
        AudioRecord audioRecord;
        AudioTrack audioTrack;
        try {
            this.recorder.startRecording();
            this.audioTrack.play();
            int i = this.audioInLength;
            byte[] bArr = new byte[i];
            while (this.isLoudSpeakerOn2) {
                int read = this.recorder.read(bArr, 0, this.audioInLength);
                if (read > 0) {
                    this.audioTrack.write(bArr, 0, read);
                    if (this.isRecordOn2) {
                        if (this.isRecording2) {
                            this.bufferedOutputStream.write(bArr, 0, i);
                        } else {
                            String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("pcm");
                            stringBuilder.append(format);
                            stringBuilder.append(".raw");
                            this.writePCMFileName = stringBuilder.toString();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("audio");
                            stringBuilder.append(format);
                            stringBuilder.append(".wav");
                            this.writeWAVFileName = stringBuilder.toString();
                            this.bufferedOutputStream = new BufferedOutputStream(openFileOutput(this.writePCMFileName, 0));
                            this.isRecording2 = true;
                        }
                    } else if (this.isRecording2) {
                        this.isRecording2 = false;
                    }
                }
            }
            bufferedOutputStream = this.bufferedOutputStream;
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException unused) {
                }
            }
            audioRecord = this.recorder;
            if (audioRecord != null) {
                try {
                    audioRecord.stop();
                    this.recorder.release();
                    this.recorder = null;
                } catch (IllegalStateException | NullPointerException unused2) {
                }
            }
            audioTrack = this.audioTrack;
            if (audioTrack != null) {
                try {
                    audioTrack.stop();
                    this.audioTrack.release();
                    this.audioTrack = null;
                } catch (IllegalStateException | NullPointerException unused3) {
                }
            }
        } catch (IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException unused4) {
            bufferedOutputStream = this.bufferedOutputStream;
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException unused5) {
                }
            }
            audioRecord = this.recorder;
            if (audioRecord != null) {
                try {
                    audioRecord.stop();
                    this.recorder.release();
                    this.recorder = null;
                } catch (IllegalStateException | NullPointerException unused6) {
                }
            }
            audioTrack = this.audioTrack;
            if (audioTrack != null) {
                audioTrack.stop();
                this.audioTrack.release();
            }
        } catch (Throwable th) {
            BufferedOutputStream bufferedOutputStream2 = this.bufferedOutputStream;
            if (bufferedOutputStream2 != null) {
                try {
                    bufferedOutputStream2.close();
                } catch (IOException unused7) {
                }
            }
            AudioRecord audioRecord2 = this.recorder;
            if (audioRecord2 != null) {
                try {
                    audioRecord2.stop();
                    this.recorder.release();
                    this.recorder = null;
                } catch (IllegalStateException | NullPointerException unused8) {
                }
            }
            AudioTrack audioTrack2 = this.audioTrack;
            if (audioTrack2 != null) {
                try {
                    audioTrack2.stop();
                    this.audioTrack.release();
                    this.audioTrack = null;
                } catch (IllegalStateException | NullPointerException unused9) {
                }
            }
            this.isRecording2 = false;
        }
    }
        private boolean rawToWave2 (File file, File file2) throws Throwable {
            Throwable th;
            int length = (int) file.length();
            byte[] bArr = new byte[length];
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream;
            try {
                dataOutputStream = new DataOutputStream(new FileOutputStream(file2));
                try {
                    writeString(dataOutputStream, "RIFF");
                    writeInt(dataOutputStream, length + 36);
                    writeString(dataOutputStream, "WAVE");
                    writeString(dataOutputStream, "fmt ");
                    writeInt(dataOutputStream, 16);
                    writeShort(dataOutputStream, (short) 1);
                    writeShort(dataOutputStream, (short) 1);
                    writeInt(dataOutputStream, 44100);
                    writeInt(dataOutputStream, 88200);
                    writeShort(dataOutputStream, (short) 2);
                    writeShort(dataOutputStream, (short) 16);
                    writeString(dataOutputStream, "data");
                    writeInt(dataOutputStream, length);
                    byte[] bArr2 = new byte[4096];
                    DataInputStream dataInputStream2 = new DataInputStream(new FileInputStream(file));
                    try {
                        for (int read = dataInputStream2.read(bArr2); read != -1; read = dataInputStream2.read(bArr2)) {
                            dataOutputStream.write(bArr2);
                        }
                        dataInputStream2.close();
                        dataOutputStream.close();
                        return true;
                    } catch (IOException unused) {
                        dataInputStream = dataInputStream2;
                        dataInputStream.close();
                        dataOutputStream.close();
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        dataInputStream = dataInputStream2;
                        dataInputStream.close();
                        dataOutputStream.close();
                        throw th;
                    }
                } catch (IOException unused2) {
                    dataInputStream.close();
                    dataOutputStream.close();
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    dataInputStream.close();
                    dataOutputStream.close();
                    throw th;
                }
            } catch (IOException unused3) {
                dataOutputStream = null;
                dataInputStream.close();
                dataOutputStream.close();
                return false;
            } catch (Throwable th4) {
                th = th4;
                dataOutputStream = null;
                dataInputStream.close();
                dataOutputStream.close();
                throw th;
            }
        }


        private void writeInt (DataOutputStream dataOutputStream,int i) throws IOException {
            dataOutputStream.write(i >> 0);
            dataOutputStream.write(i >> 8);
            dataOutputStream.write(i >> 16);
            dataOutputStream.write(i >> 24);
        }

        private void writeShort (DataOutputStream dataOutputStream,short s) throws IOException {
            dataOutputStream.write(s >> 0);
            dataOutputStream.write(s >> 8);
        }

        private void writeString (DataOutputStream dataOutputStream, String str) throws IOException {
            for (int i = 0; i < str.length(); i++) {
                dataOutputStream.write(str.charAt(i));
            }
        }
}
