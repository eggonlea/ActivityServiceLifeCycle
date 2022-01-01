#!/bin/bash

adbroot.sh
adb logcat -c
adb shell device_config put activity_manager_native_boot freeze_debounce_timeout 5000

adb shell am start -n com.lilioss.lifecycle.simpleactivity/.AlarmActivity
sleep 1
adb shell am start -n com.lilioss.lifecycle.installpackages/.ResolverActivity
sleep 1
adb shell am start -n com.google.android.deskclock/com.android.deskclock.DeskClock
sleep 1
adb shell am start -n com.google.android.calculator/com.android.calculator2.Calculator
sleep 1
adb shell am start -n com.lilioss.lifecycle.installpackages/.ResolverActivity
sleep 1
adb shell input keyevent KEYCODE_HOME

echo "Expect a series of 'FAILED BINDER TRANSACTION's from installpackages/.ResolverActivity to simpleactivity/.SimpleContentProvider"

