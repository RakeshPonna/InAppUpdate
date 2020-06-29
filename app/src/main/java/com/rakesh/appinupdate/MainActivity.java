package com.rakesh.appinupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.rakesh.appinupdate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {
    ActivityMainBinding binding;
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private static final String TAG = "MainActivity";
    private InAppUpdateManager inAppUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true)
                .handler(this);

        binding.toggleButtonGroup.setOnCheckedChangeListener((radioGroup, i) -> {

            if (i == R.id.tb_flexible) {
                inAppUpdateManager.mode(InAppUpdateManager.UpdateMode.IMMEDIATE);
            } else {
                inAppUpdateManager
                        .mode(InAppUpdateManager.UpdateMode.FLEXIBLE)
                        .useCustomNotification(true);
            }
        });
        binding.btUpdate.setOnClickListener(view -> inAppUpdateManager.checkForAppUpdate());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager.checkForAppUpdate();

                Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // InAppUpdateHandler implementation

    @Override
    public void onInAppUpdateError(int code, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        Log.d(TAG, "code: " + code, error);
    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {

        /*
         * Called when the update status change occurred.
         */

        binding.progressBar.setVisibility(status.isDownloading() ? View.VISIBLE : View.GONE);

        binding.tvAvailableVersion.setText(String.format("Available version code: %d", status.availableVersionCode()));
        binding.tvUpdateAvailable.setText(String.format("Update available: %s", String.valueOf(status.isUpdateAvailable())));

        if (status.isDownloaded()) {
            binding.btUpdate.setText("Complete Update");
            binding.btUpdate.setOnClickListener(view -> inAppUpdateManager.completeUpdate());
        }
    }
}