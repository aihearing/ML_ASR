// udpated 2024
package com.reapex.sv;

import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer;
import com.huawei.hms.mlsdk.asr.MLAsrListener;
//1 onError(int error, String errorMessage): 网络错误或者识别错误后会调用该接口。
//2 onStartListening()        录音器开始接收声音。
//3 onStartingOfSpeech()        用户开始讲话，即语音识别器检测到用户开始讲话。
//4 onState(int state, Bundle params)        通知应用状态发生改变。
//5 onVoiceDataReceived(byte[] data,float energy, Bundle params) 返回给用户原始的PCM音频流和音频能量。

/* TODO
  6 onResults(Bundle results)        从语音识别器接收到最终的语音识别结果。
  7 onRecognizingResults(Bundle recognizingResults) 当用户配置语音识别模式为MLAsrConstants.FEATURE_WORDFLUX时，
                                      语音识别器会通过该接口持续返回语音识别过程中的结果。
*/

public class ListenerSV implements MLAsrListener {
    private final static String TAG = "Listener";
    ArrayList<String> mResultsList = new ArrayList<>();

    // 从MLAsrRecognizer接收到持续语音识别的文本，该接口并非运行在主线程中，返回结果需要在子线程中处理。
    //Bundle中携带了识别后的文本信息，文本信息以String类型保存在以MLAsrRecognizer.RESULTS_RECOGNIZING为key的value中。
    @Override
    public void onRecognizingResults(Bundle partialResults) {
        if (partialResults != null) {
            mResultsList.clear();
            mResultsList.add(partialResults.getString(MLAsrRecognizer.RESULTS_RECOGNIZING));
            Log.d(TAG, "onRecognizingResults is_sv " + partialResults);
        }
    }

    // 收尾。语音识别的文本数据，该接口并非运行在主线程中，返回结果需要在子线程中处理。
    @Override
    public void onResults(Bundle results) {
        if (results != null) {
            mResultsList.clear();
            mResultsList.add(results.getString(MLAsrRecognizer.RESULTS_RECOGNIZED));
            Log.d(TAG, "onResults is " + results);
        }
    }

    @Override
    public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
        int length = data == null ? 0 : data.length;
        Log.d(TAG, "onVoiceDataReceived-- data.length=" + length);
        Log.d(TAG, "onVoiceDataReceived-- energy =" + energy);
    }

    @Override
    public void onError(int error, String errorMessage) {
        Log.e(TAG, "onError: " + errorMessage);
        // If you don't add this, there will be no response after you cut the network
    }

    @Override
    // 通知应用状态发生改变，该接口并非运行在主线程中，返回结果需要在子线程中处理。
    //sv listener 不关闭，但语音识别还是关闭了
    public void onState(int state, Bundle params) {
        Log.e(TAG, "onState: L_142 " + state);
        if (state == MLAsrConstants.STATE_NO_SOUND_TIMES_EXCEED) {
            Log.e(TAG, "onState: L_142 no sound " + state);
        }
    }

    @Override   //4
    public void onStartListening() {
        // 录音器开始接收声音。
        Log.d(TAG, "onStartListening--");
    }

    @Override
    public void onStartingOfSpeech() {
        // 用户开始讲话，即语音识别器检测到用户开始讲话。
        Log.d(TAG, "onStartingOfSpeech--");
    }
}
