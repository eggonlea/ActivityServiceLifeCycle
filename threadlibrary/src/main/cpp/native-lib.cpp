#include <fcntl.h>
#include <fstream>
#include <jni.h>
#include <string>
#include <thread>
#include <unistd.h>
#include <android/log.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>

#define FOOTPRINT __android_log_print(ANDROID_LOG_INFO, tag, "--- ===== %s() ===== ---", __func__)

bool finished = false;
char tag[64] = "NativeThread";
int fd = -1;

void testCgroup()
{
  FOOTPRINT;
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

void testOverload(bool *b) {
  *b = true;
  __android_log_print(ANDROID_LOG_INFO, tag, "%s %u", "Calling bool function: ", *b);
}

void testOverload(uint32_t *u) {
  *u = 0x11;
  __android_log_print(ANDROID_LOG_INFO, tag, "%s %u", "Calling u32 function: ", *u);
}

void testOverload() {
  FOOTPRINT;
  bool b = false;
  uint32_t u = 0;
  testOverload(&b);
  testOverload(&u);
  __android_log_print(ANDROID_LOG_INFO, tag, "b=%u u=%u", b, u);

  testOverload((uint32_t *)(&b));
  testOverload((bool *)(&u));
  __android_log_print(ANDROID_LOG_INFO, tag, "b=%u u=%u", b, u);
}

void testFileLock() {
  FOOTPRINT;
  struct flock lock;
  lock.l_type = F_WRLCK;
  lock.l_start = 0;
  lock.l_whence = SEEK_SET;
  lock.l_len = 0;
  __android_log_print(ANDROID_LOG_INFO, tag, "locking %d", fd);
  int ret = fcntl(fd, F_SETLKW, &lock);
  __android_log_print(ANDROID_LOG_INFO, tag, "locked %d return %d", fd, ret);
}

void runLoop() {
  FOOTPRINT;
  int count = 0;
  while (!finished) {
    sleep(1);
    __android_log_print(ANDROID_LOG_INFO, tag, "%d", count++);
  }
}
void run() {
  __android_log_print(ANDROID_LOG_INFO, tag, "NativeThread start");

  if (fd >= 0) testFileLock();

  runLoop();

  if (fd >= 0) {
    __android_log_print(ANDROID_LOG_INFO, tag, "release lock %d", fd);
    close(fd); // intentionally leave it open
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeTestCgroup(
    JNIEnv* env,
    jobject /* this */) {
  testCgroup();
  int pid = fork();
  __android_log_print(ANDROID_LOG_INFO, tag, "fork %d", pid);

  if (pid == 0) {
    setsid();
    testCgroup();
    execl("/system/bin/true", "/system/bin/true");
  } else {
    int ret;
    waitpid(pid, &ret, 0);
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeTestOverload(
    JNIEnv* env,
    jobject /* this */) {
  testOverload();
}

extern "C" JNIEXPORT int JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeOpenFD(
    JNIEnv* env,
    jobject /* this */,
    jstring path) {
  const char *str = env->GetStringUTFChars(path, 0);
  char fname[1024];
  snprintf(fname, 1024, "%s/%s", str, "file_lock");
  fd = open(fname, O_CREAT | O_RDWR, S_IRWXU | S_IRWXG | S_IRWXO);
  __android_log_print(ANDROID_LOG_INFO, tag, "open %d (%s) %d %s", fd, fname, errno, strerror(errno));
  return fd;
}

extern "C" JNIEXPORT int JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeGetFD(
    JNIEnv* env,
    jobject /* this */) {
  __android_log_print(ANDROID_LOG_INFO, tag, "GetFD(%d)", fd);
  return fd;
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeSetFD(
    JNIEnv* env,
    jobject /* this */,
    jint n) {
  fd = n;
  __android_log_print(ANDROID_LOG_INFO, tag, "SetFD(%d)", fd);
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeSetTag(
    JNIEnv* env,
    jobject /* this */,
    jstring s) {
  int n = env->GetStringLength(s);
  const char *str = env->GetStringUTFChars(s, 0);
  strncpy(tag, str, 64);
  tag[63] = '\0';
  env->ReleaseStringUTFChars(s, str);
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeStart(
    JNIEnv* env,
    jobject /* this */) {
  std::thread t(run);
  t.detach();
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeFork(
    JNIEnv* env,
    jobject /* this */) {
  int pid = fork();
  __android_log_print(ANDROID_LOG_INFO, tag, "fork %d", pid);

  if (pid == 0) {
    setsid();
    run();
    execl("/system/bin/true", "/system/bin/true");
  }
}

extern "C" JNIEXPORT void JNICALL
Java_com_lilioss_lifecycle_library_NativeThread_nativeFinish(
    JNIEnv* env,
    jobject /* this */) {
  __android_log_print(ANDROID_LOG_INFO, tag, "finish");
  finished = true;
}
