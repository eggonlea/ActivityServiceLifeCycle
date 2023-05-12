package com.lilioss.lifecycle.simpleactivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import com.lilioss.lifecycle.library.ISimpleAidlInterface;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;

public class SimpleActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: SimpleActivity";

  private static final String PKG_NAME = "com.lilioss.lifecycle.simpleactivity";
  private static final String AIDL_SVC = "com.lilioss.lifecycle.simpleactivity.SimpleAIDLService";
  private static final String ACTION_AIDL = "com.lilioss.lifecycle.simpleactivity.action.AIDL";
  private static final String REGISTER_BROADCAST = "com.lilioss.lifecycle.RegisterBroadcast";

  private JavaThread javaThread = null;
  private final NativeThread nativeThread = new NativeThread(TAG);

  private BroadcastReceiver mReceiver = new SimpleBroadcastReceiver(this);
  public ISimpleAidlInterface simpleManager = null;
  private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.i(TAG, "onServiceConnected");
      simpleManager = ISimpleAidlInterface.Stub.asInterface(service);
      if (simpleManager != null) {
        try {
          Log.i(TAG, simpleManager.basicTypes(1,
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
      simpleManager = null;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple);
    String simple = getApplicationContext().getCacheDir() + "/lock.simple";
    nativeThread.lockLocal(simple);
    //nativeThread.lockRemote(simple);

    Intent intent = new Intent();
    intent.setComponent(new ComponentName(PKG_NAME, AIDL_SVC));
    intent.setAction(ACTION_AIDL);
    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    IntentFilter filter = new IntentFilter(REGISTER_BROADCAST);
    ContextCompat.registerReceiver(getApplicationContext(), mReceiver, filter,
        ContextCompat.RECEIVER_EXPORTED | ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS);
  }

  @Override
  protected final void onStart() {
    Log.i(TAG, "onStart");
    super.onStart();
    nativeThread.start();
  }

  @Override
  protected final void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
    javaThread = new JavaThread(TAG);
    javaThread.start();
    SimpleIntentService.startActionFoo(getApplicationContext(),
        "param1",
        "param2");
  }

  @Override
  protected void onPause() {
    Log.i(TAG, "onPause");
    super.onPause();
    javaThread.finish();
  }

  @Override
  protected void onStop() {
    Log.i(TAG, "onStop");
    super.onStop();
    nativeThread.finish();
  }

  @Override
  protected void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
    getApplicationContext().unregisterReceiver(mReceiver);
    unbindService(mServiceConnection);
  }

  @Override
  public void onTrimMemory(int level) {
    Log.i(TAG, "onTrimMemory: " + level);
    super.onTrimMemory(level);
  }
}