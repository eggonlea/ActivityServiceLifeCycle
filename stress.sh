#!/bin/bash

adb logcat -c
adb shell device_config put activity_manager_native_boot freeze_debounce_timeout

II=0

while [ $II -lt 1000 ];
do
  echo "Iteration $II"
  #adb shell am start -n com.lilioss.lifecycle.simpleactivity/.SimpleActivity
  adb shell am start -n com.lilioss.lifecycle.simpleactivity/.BackgroundActivity
  sleep 1
  adb shell am start -n com.google.android.deskclock/com.android.deskclock.DeskClock
  sleep 1
  adb shell am start -n com.google.android.calculator/com.android.calculator2.Calculator
  sleep 2

  adb logcat -d | grep -E "FAILED BINDER TRANSACTION|DeadObjectException"
  if [ $? -eq 0 ];
  then
    echo "ERROR FOUND"
    adb logcat -d > logcat.log
    exit
  fi

  II=$((II + 1))
done
