package com.lilioss.lifecycle.installpackages;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lilioss.lifecycle.library.ISimpleAidlInterface;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;

public class InstallPackagesActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: InsPkg";
  private static final String PKG_NAME = "com.lilioss.lifecycle.simpleactivity";
  private static final String INTENT_SVC = "com.lilioss.lifecycle.simpleactivity.SimpleIntentService";
  private static final String AIDL_SVC = "com.lilioss.lifecycle.simpleactivity.SimpleAIDLService";
  private static final String ACTION_BAZ = "com.lilioss.lifecycle.simpleactivity.action.BAZ";
  private static final String ACTION_AIDL = "com.lilioss.lifecycle.simpleactivity.action.AIDL";
  private static final String EXTRA_PARAM1 = "com.lilioss.lifecycle.simpleactivity.extra.PARAM1";
  private static final String EXTRA_PARAM2 = "com.lilioss.lifecycle.simpleactivity.extra.PARAM2";

  private final JavaThread javaThread = new JavaThread(TAG);
  private final NativeThread nativeThread = new NativeThread(TAG);

  private ISimpleAidlInterface mSimpleManager = null;

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

    startActionBaz(getApplicationContext(),
        "param1",
        "param2");

    bindAIDLService();
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

  /**
   * Starts this service to perform action Baz with the given parameters. If the service is already
   * performing a task this action will be queued.
   *
   * @see IntentService
   */
  public void startActionBaz(Context context, String param1, String param2) {
    Log.i(TAG, "startActionBaz");
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(PKG_NAME, INTENT_SVC));
    intent.setAction(ACTION_BAZ);
    intent.putExtra(EXTRA_PARAM1, param1);
    intent.putExtra(EXTRA_PARAM2, param2);
    context.startForegroundService(intent);
  }

  public void bindAIDLService() {
    Log.i(TAG, "bindAIDLService");
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(PKG_NAME, AIDL_SVC));
    intent.setAction(ACTION_AIDL);
    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
  }

  private final ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.i(TAG, "onServiceConnected");
      mSimpleManager = ISimpleAidlInterface.Stub.asInterface(service);
      if (mSimpleManager != null) {
        try {
          Log.i(TAG, mSimpleManager.basicTypes(1,
              2,
              true,
              3,
              4,
              "abc"));
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      Log.i(TAG, "onServiceDisconnected");
    }
  };
}