package com.lilioss.lifecycle.library;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.content.Context;
import android.os.Environment;

public class NativeThread {
  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private final String tag;

  public NativeThread(String s) {
    tag = "LifeCycle: std::thread_" + s;
    nativeSetTag(tag);
  }

  public int openFD() {
    return nativeOpenFD(Environment
        .getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        .getAbsolutePath());
  }

  public int getFD() {
    return nativeGetFD();
  }
  public void setFD(int fd) {
    nativeSetFD(fd);
  }
  public void start() {
    nativeStart();
  }
  public void fork() {
    nativeFork();
  }
  public void finish() {
    nativeFinish();
  }

  /**
   * Misc tools
   */
  public void testCgroup() {
    nativeTestCgroup();
  }

  public void testOverload() {
    nativeTestOverload();
  }

  /**
   * A native method that is implemented by the 'native-lib' native library, which is packaged with
   * this application.
   */
  private native void nativeSetTag(String tag);
  private native int nativeOpenFD(String path);
  private native int nativeGetFD();
  private native void nativeSetFD(int fd);
  private native void nativeStart();
  private native void nativeFork();
  private native void nativeFinish();

  /**
   * Misc tools
   */
  private native void nativeTestCgroup();
  private native void nativeTestOverload();
}
