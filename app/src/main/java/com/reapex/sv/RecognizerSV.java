package com.reapex.sv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.asr.MLAsrListener;
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer;

import java.util.ArrayList;

public class RecognizerSV {
    private final static String TAG = "Leo";
    public Intent mRecoIntent;

    protected Context mContext;
    protected MLAsrRecognizer mRecognizer;

    private OnResultsReadyInterface mInterface;  //2 接口变量

    public RecognizerSV(Context pContext, OnResultsReadyInterface pInterface) {
        try {
            mInterface = pInterface;        //3 提供注册接口的方法 暴露接口给调用者；
        } catch (ClassCastException e) {
            Log.d(TAG, e.toString());
        }
        mContext = pContext;
        initReco();
    }

    protected void initReco(){
        mRecognizer = MLAsrRecognizer.createAsrRecognizer(mContext);    //a 用户调用接口创建一个语音识别器。

        mRecognizer.setAsrListener(new ListenerSV());                   //b 绑定个listener
        mRecoIntent = new Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH);
        mRecoIntent
                .putExtra(MLAsrConstants.LANGUAGE, "zh-CN")
                .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX);
        mRecognizer.startRecognizing(mRecoIntent);
    }

    protected class ListenerSV implements MLAsrListener {
        ArrayList<String> mResultsList = new ArrayList<>();

        // 从MLAsrRecognizer接收到持续语音识别的文本，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        //Bundle中携带了识别后的文本信息，文本信息以String类型保存在以MLAsrRecognizer.RESULTS_RECOGNIZING为key的value中。
        @Override
        public void onRecognizingResults(Bundle partialResults) {
            if (partialResults != null) {
                mResultsList.clear();
                mResultsList.add(partialResults.getString(MLAsrRecognizer.RESULTS_RECOGNIZING));
                mInterface.onResults(mResultsList);      //4 接口变量调用被实现的接口方法；
                Log.d(TAG, "onRecognizingResults is_sv " + partialResults);
            }
        }

        // 收尾。语音识别的文本数据，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        @Override
        public void onResults(Bundle results) {
            if (results != null) {
                mResultsList.clear();
                mResultsList.add(results.getString(MLAsrRecognizer.RESULTS_RECOGNIZED));
                Log.d(TAG, "onResults is _sv -------------  " + results);
                initReco();
            }
        }

        @Override
        public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
            int length = data == null ? 0 : data.length;
            Log.d(TAG, "onVoiceDataReceived-- energy =" + energy);
        }

        @Override
        public void onError(int error, String errorMessage) {
            Log.d(TAG, "onError: " + errorMessage);
            // If you don't add this, there will be no response after you cut the network
        }

        @Override
        // 通知应用状态发生改变，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        //sv listener 不关闭，但语音识别还是关闭了
        public void onState(int state, Bundle params) {
            Log.d(TAG, "onState: L_77 " + state);
            if (state == MLAsrConstants.STATE_NO_SOUND_TIMES_EXCEED) {
                Log.d(TAG, "onState: L_79 no sound " + state);
            }
        }

        @Override   //4
        public void onStartListening() {
            Log.d(TAG, "录音器开始接收声音。onStartListening--");
        }

        @Override
        public void onStartingOfSpeech() {
            Log.d(TAG, "用户开始讲话，即语音识别器检测到用户开始讲话。 onStartingOfSpeech--");
        }
    }

    public interface OnResultsReadyInterface {
        //1 定义接口和接口中的方法
        void onResults(ArrayList<String> results);
        void onFinsh();
        void onError(int error);
    }
}
