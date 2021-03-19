package com.reapex.sv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = "Leo";
    private static final int PERMISSION_REQUESTS = 1;
    public static final String API_KEY = "client/api_key";
    private ClassOnResultInterface oFromInterface = new ClassOnResultInterface();

    RecognizerSV mReco;
    TextView tvASR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i = 1;
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_asr_start).setOnClickListener(this);
        tvASR = findViewById(R.id.textview_showing_asr);

        setApiKey();
        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_asr_start) {
            Log.d(TAG, "第一次RecognizerSV");
            mReco = new RecognizerSV(this, oFromInterface);
            Log.d(TAG, "第一次RecognizerSV 结束");
        }
    }

    private class ClassOnResultInterface implements RecognizerSV.OnResultsReadyInterface {
        @Override
        public void onResults(ArrayList<String> results) {
            if (results != null && results.size() > 0) {
                if (results.size() == 1) {
                    tvASR.setText(results.get(0)+"。。");
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (results.size() > 5) {
                        results = (ArrayList<String>) results.subList(0, 5);
                    }
                    for (String result : results) {
                        sb.append(result).append("\n");
                    }
                    tvASR.setText(sb.toString()+"L1_69 Never Happen");
                }
            }
            Log.d(TAG, "OnResults");
        }

        @Override
        public void onError(int error) {        }

        @Override
        public void onFinsh() {        }
    }

    private void setApiKey(){
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }

    private boolean allPermissionsGranted() {
        for (String permission : this.getRequiredPermissions()) {
            if (!this.isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!this.isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), this.PERMISSION_REQUESTS);
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }
}