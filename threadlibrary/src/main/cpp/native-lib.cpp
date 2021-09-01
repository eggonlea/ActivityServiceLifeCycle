#include <fcntl.h>
#include <fstream>
#include <jni.h>
#include <string>
#include <thread>
#include <unistd.h>
#include <android/log.h>
#include <sys/stat.h>
#include <sys/types.h>

bool finished = false;
char tag[64];

void test_cgroup()
{
    int uid=0, pid=0;
    int self = getpid();
    std::ifstream cgroup("/proc/self/cgroup");
    std::string line;
    while (std::getline(cgroup, line)) {
        __android_log_print(ANDROID_LOG_INFO, tag, "%s", line.c_str());
        if (line.find("uid") != std::string::npos) {
            sscanf(line.c_str(), "0::/uid_%d/pid_%d", &uid, &pid);
            __android_log_print(ANDROID_LOG_INFO, tag, "pid=%d, uid/pid=%d/%d", self, uid, pid);
        }
    }

    if (uid > 0 && pid > 0) {
        int ret;
        char uiddir[1024];
        char piddir[1024];
        snprintf(uiddir, 1024, "/sys/fs/cgroup/uid_%d", uid);
        snprintf(piddir, 1024, "/sys/fs/cgroup/uid_%d/pid_%d", uid, pid);
        for (int i = 0; i < 3; i++) {
            ret = mkdir(uiddir, 0750);
            __android_log_print(ANDROID_LOG_INFO,
                                tag,
                                "mkdir#%d(%s, 0750) returns %d (errno=%d, EEXIST=%d)",
                                i,
                                uiddir,
                                ret,
                                errno,
                                EEXIST);
            ret = mkdir(piddir, 0750);
            __android_log_print(ANDROID_LOG_INFO,
                                tag,
                                "mkdir#%d(%s, 0750) returns %d (errno=%d, EEXIST=%d)",
                                i,
                                piddir,
                                ret,
                                errno,
                                EEXIST);
        }
    }
}

void test_flock()
{
  // To test the file locks. Do the following before running the app
  // adb shell touch /data/file_lock
  // adb shell chmod 777 /data/file_lock
  // adb shell setenforce 0
  // And then the blocked locks should be visible in /proc/locks
  int fd = open("/data/file_lock", O_CREAT | O_RDWR, S_IRWXU);
  __android_log_print(ANDROID_LOG_INFO, tag, "open %d", fd);
  fork();
  fork();
  struct flock lock;
  lock.l_type = F_WRLCK;
  lock.l_start = 0;
  lock.l_whence = SEEK_SET;
  lock.l_len = 0;
  __android_log_print(ANDROID_LOG_INFO, tag, "locking %d", fd);
  int ret = fcntl(fd, F_SETLKW, &lock);
  __android_log_print(ANDROID_LOG_INFO, tag, "locked %d return %d", fd, ret);
  //close(fd);
}

void run(bool cgroup, bool flock) {
  if (cgroup) {
    test_cgroup();
    int pid = fork();
    __android_log_print(ANDROID_LOG_INFO, tag, "fork %d", pid);

    if (pid == 0) {
      test_cgroup();
      return;
    }
  }

  if (flock) test_flock();

  int count = 0;
  while (!finished) {
    __android_log_print(ANDROID_LOG_INFO, tag, "%d", count++);
    sleep(1);
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeStart(
        JNIEnv* env,
        jobject /* this */,
        jstring s,
        jboolean cgroup,
        jboolean flock) {
    int n = env->GetStringLength(s);
    const char *str = env->GetStringUTFChars(s, 0);
    strncpy(tag, str, 64);
    tag[63] = '\0';
    env->ReleaseStringUTFChars(s, str);
    std::thread t(run, cgroup, flock);
    t.detach();
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeFinish(
    JNIEnv* env,
    jobject /* this */) {
    __android_log_print(ANDROID_LOG_INFO, tag, "finish");
    finished = true;
}
