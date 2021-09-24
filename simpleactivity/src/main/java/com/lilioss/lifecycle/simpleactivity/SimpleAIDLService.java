package com.lilioss.lifecycle.simpleactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.lilioss.lifecycle.library.ISimpleAidlInterface;
import com.lilioss.lifecycle.library.NativeThread;

public class SimpleAIDLService extends Service {
  private final static String TAG = "LifeCycle: AidlSvc";

  private final NativeThread nativeThread = new NativeThread(TAG);
  private int mFd = -1;

  public SimpleAIDLService() {
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate");
    super.onCreate();
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind");
    return mSimpleManager;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
  }

  private final ISimpleAidlInterface.Stub mSimpleManager = new ISimpleAidlInterface.Stub() {
    @Override
    public String basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble,
        String aString) throws RemoteException {
      return "Int=" + anInt
          + " Long=" + aLong
          + " Boolean=" + aBoolean
          + " Float=" + aFloat
          + " Double=" + aDouble
          + " String=" + aString;
    }

    @Override
    public ParcelFileDescriptor shareFileLock() throws RemoteException {
      mFd = nativeThread.getFD();
      if (mFd == -1) {
        nativeThread.openFD();
        mFd = nativeThread.getFD();
      }

      if (mFd == -1) {
        Log.e(TAG, "Failed to open lock file");
        return null;
      }

      int fd = nativeThread.getFD();
      Log.i(TAG, "shareFile: " + fd);
      ParcelFileDescriptor pfd = ParcelFileDescriptor.adoptFd(fd);
      nativeThread.start();
      return pfd;
    }

    @Override
    public void cleanFileLock() throws RemoteException {
      nativeThread.finish();
    }

    @Override
    public void fork() throws RemoteException {
      nativeThread.setFD(mFd);
      nativeThread.fork();
    }
  };
}