package com.lilioss.lifecycle.installpackages;

import android.content.pm.PackageManager;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;

public class InstallPackagesActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: InsPkgActivity";
  private final JavaThread javaThread = new JavaThread(TAG);
  private final NativeThread nativeThread = new NativeThread(TAG);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    /*
    Steps to grant the apk INSTALL_PACKAGES permission as a system app:

    1. Add the following code to AndroidManifest.xml
    <uses-permission android:name="android.permission.INSTALL_PACKAGES"
      tools:ignore="ProtectedPermissions" />

    2. Add the following code to /etc/permissions/privapp-permissions-platform.xml
    <privapp-permissions package="com.lilioss.lifecycle.installpackages">
        <permission name="android.permission.INSTALL_PACKAGES"/>
    </privapp-permissions>

    3. Put the apk to /system/priv-app/InstallPackages/installpackages.apk
    */
    if (checkSelfPermission("android.permission.INSTALL_PACKAGES") == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "INSTALL_PACKAGES permission granted");
    } else {
      Log.i(TAG, "INSTALL_PACKAGES permission denied");
    }

    javaThread.start();
    nativeThread.start();
  }

  @Override
  protected final void onStart() {
    Log.i(TAG, "onStart");
    super.onStart();
  }

  @Override
  protected final void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.i(TAG, "onPause");
    super.onPause();
  }

  @Override
  protected void onStop() {
    Log.i(TAG, "onStop");
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
    javaThread.finish();
    nativeThread.finish();
  }
}