package com.lilioss.lifecycle.installpackages;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.JavaThread.JavaThreadCallback;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResolverActivity extends AppCompatActivity implements JavaThreadCallback {

  private final static String TAG = "LifeCycle: ResolverActivity";
  TextView textView;
  private JavaThread javaThread = null;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resolver);
    textView = findViewById(R.id.resolver_text);
    textView.setText("");
    log("onCreate");
    javaThread = new JavaThread(TAG);
    javaThread.setInterval(3000);
    javaThread.setContext(getApplicationContext());
    javaThread.setCallback(this);
    javaThread.enableContent();
    javaThread.start();
  }

  @Override
  protected final void onStart() {
    super.onStart();
    log("onStart");
  }

  @Override
  protected final void onResume() {
    super.onResume();
    log("onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    log("onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();
    log("onStop");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    log("onDestroy");
    javaThread.finish();
  }

  private void log(String text) {
    Log.i(TAG, text);
    textView.append(sdf.format(new Date()));
    textView.append(" ");
    textView.append(text);
    textView.append("\n");
  }

  @Override
  public void onCallback(String text) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        log(text);
      }
    });
  }
}