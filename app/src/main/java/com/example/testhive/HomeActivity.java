package com.example.testhive;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import org.elastos.carrier.forward.PfdAgent;

public class HomeActivity extends Activity {

    private static final String serverId = "192.168.0.1";

    private PfdAgent pfdAgent;
    private String localDataPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        localDataPath = getFilesDir().getAbsolutePath() + "/elaCarrier";
    }


    public void test(View view) {
        try {
            pfdAgent = PfdAgent.singleton(localDataPath);
            pfdAgent.start();
            pfdAgent.pairServer(serverId, "hello");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
