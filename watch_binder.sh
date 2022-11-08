adbroot.sh

PID=`adb shell pidof com.lilioss.lifecycle.simpleactivity`
watch -n 1 -d "adb shell cat /sys/kernel/debug/binder/stats | sed -n \"/proc $PID/,\\\$p\""
