
/*#include <jni.h>
#include "lameutils/mp3file_encoder.h"
#include "saka_log.h"
#include <jni.h>
#include <jni.h>
#include <jni.h>
#include <jni.h>
#include <jni.h>
#include <jni.h>

mp3file_encoder *encoder;

extern "C"
JNIEXPORT void JNICALL
Java_com_mybluetooth_audio_1playback_1capture_PCMToMp3Encoder_encode(JNIEnv *env, jclass type) {

    encoder->Encode();

}

extern "C"
JNIEXPORT void JNICALL
Java_com_mybluetooth_audio_1playback_1capture_PCMToMp3Encoder_destroy(JNIEnv *env, jclass type) {

    encoder->Destroy();

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_mybluetooth_audio_1playback_1capture_PCMToMp3Encoder_init(JNIEnv *env, jclass type, jstring pcmFilePath_,
                                                       jint audioChannels, jint bitRate, jint sampleRate,
                                                       jstring mp3FilePath_) {
    const char *pcmFilePath = env->GetStringUTFChars(pcmFilePath_, 0);
    const char *mp3FilePath = env->GetStringUTFChars(mp3FilePath_, 0);
    SAKA_LOG_DEBUG("pcm=%s", pcmFilePath);
    SAKA_LOG_DEBUG("mp3=%s", mp3FilePath);
    encoder = new mp3file_encoder();
    encoder->Init(pcmFilePath, mp3FilePath, sampleRate, audioChannels, bitRate);
    env->ReleaseStringUTFChars(pcmFilePath_, pcmFilePath);
    env->ReleaseStringUTFChars(mp3FilePath_, mp3FilePath);
    return 1;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_heareasy_audio_1playback_1capture_PCMToMp3Encoder_init(JNIEnv *env, jclass clazz,
                                                                jstring pcm_file_path,
                                                                jint audio_channels, jint bit_rate,
                                                                jint sample_rate,
                                                                jstring mp3_file_path) {
    // TODO: implement init()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_heareasy_audio_1playback_1capture_PCMToMp3Encoder_encode(JNIEnv *env, jclass clazz) {
    // TODO: implement encode()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_heareasy_audio_1playback_1capture_PCMToMp3Encoder_destroy(JNIEnv *env, jclass clazz) {
    // TODO: implement destroy()
}*/


#include <jni.h>
#include "lameutils/mp3file_encoder.h"
#include "saka_log.h"

mp3file_encoder *encoder;
extern "C"
JNIEXPORT jint JNICALL
Java_linc_com_pcmdecoder_PCMDecoder_init(JNIEnv *env, jclass type, jstring pcmFilePath_,
                                         jint audioChannels, jint bitRate, jint sampleRate,
                                         jstring mp3FilePath_) {
    const char *pcmFilePath = env->GetStringUTFChars(pcmFilePath_, 0);
    const char *mp3FilePath = env->GetStringUTFChars(mp3FilePath_, 0);
    SAKA_LOG_DEBUG("pcm=%s", pcmFilePath);
    SAKA_LOG_DEBUG("mp3=%s", mp3FilePath);
    encoder = new mp3file_encoder();
    encoder->Init(pcmFilePath, mp3FilePath, sampleRate, audioChannels, bitRate);
    env->ReleaseStringUTFChars(pcmFilePath_, pcmFilePath);
    env->ReleaseStringUTFChars(mp3FilePath_, mp3FilePath);
    return 1;
}

extern "C"
JNIEXPORT void JNICALL
Java_linc_com_pcmdecoder_PCMDecoder_encode(JNIEnv *env, jclass type) {

    encoder->Encode();

}extern "C"
JNIEXPORT void JNICALL
Java_linc_com_pcmdecoder_PCMDecoder_destroy(JNIEnv *env, jclass type) {

    encoder->Destroy();

}


extern "C"
JNIEXPORT jint JNICALL
Java_com_heareasy_audio_1playback_1capture_PCMToMp3Encoder_init(JNIEnv *env, jclass clazz,
                                                                jstring pcm_file_path,
                                                                jint audio_channels, jint bit_rate,
                                                                jint sample_rate,
                                                                jstring mp3_file_path) {
    // TODO: implement init()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_heareasy_audio_1playback_1capture_PCMToMp3Encoder_encode(JNIEnv *env, jclass clazz) {
    // TODO: implement encode()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_heareasy_audio_1playback_1capture_PCMToMp3Encoder_destroy(JNIEnv *env, jclass clazz) {
    // TODO: implement destroy()
}