package com.heareasy.audio_playback_capture;


/**
 * Created by rangaofei on 2018/4/24.
 */
/*public class PCMToMp3Encoder {
    static {
        System.loadLibrary("Mp3Codec");
    }
     *//*   public static native int init(String pcmFilePath, int audioChannels, int bitRate,
                                  int sampleRate, String mp3FilePath);

    public static native void encode();

    public static native void destroy();*//*

    public static native int init(String pcmFilePath, int audioChannels, int bitRate,
                                   int sampleRate, String mp3FilePath);

    public static native void encode();

    public static native void destroy();




}*/



public class PCMToMp3Encoder {

    static {
        System.loadLibrary("Mp3Codec");
    }

    public static void PCMToMp3Encoder(String pcmFilePath,

                                   int audioChannels,
                                   int bitRate,
                                   int sampleRate,
                                   String mp3FilePath) {
        init(pcmFilePath, audioChannels, bitRate, sampleRate, mp3FilePath);
        encode();
        destroy();
    }

    public  static native int init(String pcmFilePath,
                           int audioChannels,
                           int bitRate,
                           int sampleRate,
                           String mp3FilePath);

    public static native void encode();

    public static native void destroy();


}
