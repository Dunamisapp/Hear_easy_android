package com.heareasy.audio;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;

import com.heareasy.fragment.RecordFragment;
import com.heareasy.audio.fft.Complex;
import com.heareasy.audio.fft.FFT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import simplesound.pcm.PcmAudioHelper;
import simplesound.pcm.WavAudioFormat;

import static com.heareasy.audio_playback_capture.Constants.BUFFER_SIZE_IN_BYTES;


public class AudioRecordingThread extends Thread {
    private static final String FILE_NAME = "audiorecordtest.raw";
    private static final int SAMPLING_RATE = 44100;
    private static final int FFT_POINTS = 1024;
    private static final int MAGIC_SCALE = 10;
    private short[] shortAudioData;

    private String fileName_wav;
    private String fileName_raw;
    private AudioRecord record;
    private AudioTrack audioTrack;

    private int bufferSize;
    private byte[] audioBuffer;
    int intRecordSampleRate;

    private boolean isRecording = true;

    private AudioRecordingHandler handler = null;

    public AudioRecordingThread() {
    }

    public AudioRecordingThread(String fileWavName, AudioRecordingHandler handler) {
        this.fileName_wav = fileWavName;
        this.fileName_raw = getRawName(fileWavName);
        this.handler = handler;


    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        FileOutputStream out = prepareWriting();
        if (out == null) {
            return;
        }


        intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
        audioBuffer = new byte[bufferSize];

        shortAudioData = new short[bufferSize];

        record = new AudioRecord(AudioSource.DEFAULT, /*AudioSource.MIC*/
            SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE_IN_BYTES);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
            , SAMPLING_RATE
            , AudioFormat.CHANNEL_OUT_MONO
            , AudioFormat.ENCODING_PCM_16BIT
            , BUFFER_SIZE_IN_BYTES
            , AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(SAMPLING_RATE);
            record.startRecording();
            audioTrack.play();

        int read = 0;
        while (isRecording) {
            read = record.read(audioBuffer, 0, bufferSize);
            audioTrack.write(audioBuffer, 0, bufferSize);

            if ((read == AudioRecord.ERROR_INVALID_OPERATION) ||
                (read == AudioRecord.ERROR_BAD_VALUE) ||
                (read <= 0)) {
                continue;
            }

            if (RecordFragment.checkSound==1) {

                 //proceed();
                write(out);
            }
        }
        if (record != null) {
            record.stop();
            record.release();
        }

        if (RecordFragment.checkSound==1) {

            finishWriting(out);
            convertRawToWav();
        }
    }

    private void proceed() {
        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[FFT_POINTS];

        for (int i = 0; i < FFT_POINTS; i++) {
            temp = (double) ((audioBuffer[2 * i] & 0xFF) | (audioBuffer[2 * i + 1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp * MAGIC_SCALE, 0d);
        }

        y = FFT.fft(complexSignal);


       //  * See http://developer.android.com/reference/android/media/audiofx/Visualizer.html#getFft(byte[]) for format explanation


        final byte[] y_byte = new byte[y.length * 2];
        y_byte[0] = (byte) y[0].re();
        y_byte[1] = (byte) y[y.length - 1].re();
        for (int i = 1; i < y.length - 1; i++) {
            y_byte[i * 2] = (byte) y[i].re();
            y_byte[i * 2 + 1] = (byte) y[i].im();
        }

        if (handler != null) {
            handler.onFftDataCapture(y_byte);
        }
    }

    private FileOutputStream prepareWriting() {
        File file = new File(fileName_raw);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName_raw, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordingError();
            }
        }
        return out;
    }

    private void write(FileOutputStream out) {
        try {
            if (audioBuffer != null)
                out.write(audioBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordingError();
            }
        }
    }

    private void finishWriting(FileOutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordingError();
            }
        }
    }

    private String getRawName(String fileWavName) {
        return String.format("%s/%s", getFileDir(fileWavName), FILE_NAME);
    }

    private String getFileDir(String fileWavName) {
        File file = new File(fileWavName);
        String dir = file.getParent();
        return (dir == null) ? "" : dir;
    }

    private void convertRawToWav() {
        File file_raw = new File(fileName_raw);
        if (!file_raw.exists()) {
            return;
        }
        File file_wav = new File(fileName_wav);
        if (file_wav.exists()) {
            file_wav.delete();
        }
        try {
            PcmAudioHelper.convertRawToWav(WavAudioFormat.mono16Bit(SAMPLING_RATE), file_raw, file_wav);
            file_raw.delete();
            if (handler != null) {
                handler.onRecordSuccess();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordSaveError();
            }
        }
    }

    public synchronized void stopRecording() {
        if (record != null) {
            record.stop();
            record = null;
            isRecording = false;
            audioTrack.stop();

        }
    }

/*public class AudioRecordingThread extends Thread {
    private static final String FILE_NAME = "audiorecordtest.raw";
    private static final int SAMPLING_RATE = 44100;
    private static final int FFT_POINTS = 1024;
    private static final int MAGIC_SCALE = 10;
    private short[] shortAudioData;

    private String fileName_wav;
    private String fileName_raw;
    private AudioRecord record;
    private AudioTrack audioTrack;

    private int bufferSize;
    private byte[] audioBuffer;
    int intRecordSampleRate;

    private boolean isRecording = true;

    private AudioRecordingHandler handler = null;

    public AudioRecordingThread(String fileWavName, AudioRecordingHandler handler) {
        this.fileName_wav = fileWavName;
        this.fileName_raw = getRawName(fileWavName);
        this.handler = handler;

    }

    @Override
    public void run() {
        FileOutputStream out = prepareWriting();
        if (out == null) {
            return;
        }


        intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioBuffer = new byte[bufferSize];

        shortAudioData = new short[bufferSize];


        record = new AudioRecord(AudioSource.VOICE_RECOGNITION, *//*AudioSource.MIC*//*
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
                , SAMPLING_RATE
                , AudioFormat.CHANNEL_IN_DEFAULT
                , AudioFormat.ENCODING_PCM_16BIT
                , bufferSize
                , AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(SAMPLING_RATE);
        record.startRecording();
        audioTrack.play();

        int read = 0;
        while (isRecording) {
            read = record.read(audioBuffer, 0, bufferSize);
            audioTrack.write(audioBuffer, 0, bufferSize);

            if ((read == AudioRecord.ERROR_INVALID_OPERATION) ||
                    (read == AudioRecord.ERROR_BAD_VALUE) ||
                    (read <= 0)) {
                continue;
            }

            proceed();
            write(out);
        }
        if (record != null) {
            record.stop();
            record.release();
        }

        finishWriting(out);
        convertRawToWav();
    }

    private void proceed() {
        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[FFT_POINTS];

        for (int i = 0; i < FFT_POINTS; i++) {
            temp = (double) ((audioBuffer[2 * i] & 0xFF) | (audioBuffer[2 * i + 1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp * MAGIC_SCALE, 0d);
        }

        y = FFT.fft(complexSignal);

        *//*
         * See http://developer.android.com/reference/android/media/audiofx/Visualizer.html#getFft(byte[]) for format explanation
         *//*

        final byte[] y_byte = new byte[y.length * 2];
        y_byte[0] = (byte) y[0].re();
        y_byte[1] = (byte) y[y.length - 1].re();
        for (int i = 1; i < y.length - 1; i++) {
            y_byte[i * 2] = (byte) y[i].re();
            y_byte[i * 2 + 1] = (byte) y[i].im();
        }

        if (handler != null) {
            handler.onFftDataCapture(y_byte);
        }
    }

    private FileOutputStream prepareWriting() {
        File file = new File(fileName_raw);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName_raw, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordingError();
            }
        }
        return out;
    }

    private void write(FileOutputStream out) {
        try {
            if (audioBuffer != null)
                out.write(audioBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordingError();
            }
        }
    }

    private void finishWriting(FileOutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordingError();
            }
        }
    }

    private String getRawName(String fileWavName) {
        return String.format("%s/%s", getFileDir(fileWavName), FILE_NAME);
    }

    private String getFileDir(String fileWavName) {
        File file = new File(fileWavName);
        String dir = file.getParent();
        return (dir == null) ? "" : dir;
    }

    private void convertRawToWav() {
        File file_raw = new File(fileName_raw);
        if (!file_raw.exists()) {
            return;
        }
        File file_wav = new File(fileName_wav);
        if (file_wav.exists()) {
            file_wav.delete();
        }
        try {
            PcmAudioHelper.convertRawToWav(WavAudioFormat.mono16Bit(SAMPLING_RATE), file_raw, file_wav);
            file_raw.delete();
            if (handler != null) {
                handler.onRecordSuccess();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRecordSaveError();
            }
        }
    }

    public synchronized void stopRecording() {
        if (record != null) {
            record.stop();
            record = null;
            isRecording = false;
            audioTrack.stop();

        }
    }*/

}