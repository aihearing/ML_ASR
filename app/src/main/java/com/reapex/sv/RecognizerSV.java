package com.reapex.sv;

import android.content.Context;
import android.content.Intent;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer;

public class RecognizerSV {

    protected MLAsrRecognizer mRecognizer;

    public RecognizerSV(Context context) {
        int i =3;
        mRecognizer = MLAsrRecognizer.createAsrRecognizer(context);   //1 用户调用接口创建一个语音识别器。
        mRecognizer.setAsrListener(new ListenerSV());   //2 绑定个listener
        Intent mRecoIntent = new Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH);
        mRecoIntent
                .putExtra(MLAsrConstants.LANGUAGE, "zh-CN")
                .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX);
        mRecognizer.startRecognizing(mRecoIntent);
    }
}
