package com.example.googleclientvslocationlistner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RemoteConfigFirebaseDemo extends AppCompatActivity {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_config_firebase_demo);
        initFirebase();

    }

    private void initFirebase() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("over_lay_max_chars", 5);
        mFirebaseRemoteConfig.setDefaults(defaults);
        final Task<Void> fetch = mFirebaseRemoteConfig.fetch(
                BuildConfig.DEBUG ? 0 : TimeUnit.HOURS.toSeconds(12));
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseRemoteConfig.activateFetched();
                updateMaxTextLength();
            }
        });
    }

    private void updateMaxTextLength() {
        int maxInputET;
        try {
            maxInputET = (int) mFirebaseRemoteConfig.getLong("over_lay_max_chars");
            initEditText(maxInputET);
        } catch (Exception e) {
            e.printStackTrace();
            maxInputET = 10;
        }
    }

    private void initEditText(int max) {
        EditText etRemoteConFirebase = (EditText) findViewById(R.id.et_remoteconfig);
        etRemoteConFirebase.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
    }
}
