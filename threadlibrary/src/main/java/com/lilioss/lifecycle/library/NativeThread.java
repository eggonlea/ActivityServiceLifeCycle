package com.lilioss.lifecycle.library;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NativeThread {
  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private final String tag;

  public NativeThread(String s) {
    tag = "LifeCycle: std::thread_" + s;
    nativeInit(tag);
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

  public void testProcLocks() {
    nativeTestProcLocks();
  }

  public void lockLocal(String path) {
    nativeLockLocal(path);
  }

  public void lockRemote(String path) {
    nativeLockRemote(path);
  }

  /**
   * A native method that is implemented by the 'native-lib' native library, which is packaged with
   * this application.
   */
  private native void nativeInit(String tag);
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
  private native void nativeTestProcLocks();
  private native void nativeLockLocal(String path);
  private native void nativeLockRemote(String path);
}
