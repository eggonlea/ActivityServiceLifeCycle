package com.lilioss.lifecycle.simpleactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.JavaThread.JavaThreadCallback;

public class BatteryActivity extends AppCompatActivity implements JavaThreadCallback {

  private final static String TAG = "LifeCycle: BatteryActivity";
  private JavaThread javaThread = null;

  private BatteryManager batteryManager = null;
  IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @RequiresApi(api = VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
      BatteryActivity.this.onReceive(intent);
    }
  };

  @RequiresApi(api = VERSION_CODES.P)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple);

    Context context = getApplicationContext();
    batteryManager = context.getSystemService(BatteryManager.class);
    Intent intent = context.registerReceiver(mReceiver, mFilter);
    onReceive(intent);

    javaThread = new JavaThread(TAG);
    javaThread.setContext(context);
    javaThread.setInterval(10);
    javaThread.enableWorkload();
    javaThread.setCallback(this);
    javaThread.start();
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
    getApplicationContext().unregisterReceiver(mReceiver);
    javaThread.finish();
  }

  @RequiresApi(api = VERSION_CODES.P)
  public void onReceive(Intent intent) {
    String action = intent.getAction();
    Log.i(TAG, "onReceive(" + action + ")");
    switch(action) {
      case Intent.ACTION_BATTERY_CHANGED:
        Log.i(TAG, "Battery Changed");
        getBatteryStatus(intent);
        break;
      case Intent.ACTION_BATTERY_LOW:
        Log.i(TAG, "Battery Low");
        break;
      case Intent.ACTION_BATTERY_OKAY:
        Log.i(TAG, "Battery Okay");
        break;
      case Intent.ACTION_POWER_CONNECTED:
        Log.i(TAG, "Power Connected");
        break;
      case Intent.ACTION_POWER_DISCONNECTED:
        Log.i(TAG, "Power Disconnected");
        break;
      default:
        Log.w(TAG, "Unknown Intent");
        break;
    }
  }

  @RequiresApi(api = VERSION_CODES.P)
  public void getBatteryStatus(Intent intent) {
    if (intent != null) {
      Log.i(TAG, "present: " + intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false));
      Log.i(TAG, "plugged: " + intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));
      Log.i(TAG, "level  : " + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
      Log.i(TAG, "max    : " + intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
      Log.i(TAG, "status : " + intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1));
    }
    Log.i(TAG, "remain%: " + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    Log.i(TAG, "max mAh: " + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
    Log.i(TAG, "avg cur: " + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
    Log.i(TAG, "now cur: " + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
    Log.i(TAG, "to full: " + batteryManager.computeChargeTimeRemaining());
    Log.i(TAG, "charging " + batteryManager.isCharging());
  }

  @RequiresApi(api = VERSION_CODES.P)
  @Override
  public void onCallback(String text) {
    Log.i(TAG, "onCallback(" + text + ")");
    getBatteryStatus(null);
  }
}