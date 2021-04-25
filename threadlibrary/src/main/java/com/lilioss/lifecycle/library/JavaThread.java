package com.lilioss.lifecycle.library;

import android.os.SystemClock;
import android.util.Log;

public class JavaThread extends Thread {

  private final String tag;
  boolean finished = false;

  public JavaThread(String s) {
    tag = "LifeCycle: Java Thread_" + s;
  }

  @Override
  public void run() {
    super.run();
    int i = 0;
    while (!finished) {
      Log.i(tag, "" + i++);
      SystemClock.sleep(1000);
    }
  }

  public void finish() {
    Log.i(tag, "finish");
    finished = true;
  }
}
