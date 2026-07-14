# 构建和安装说明

## 构建环境

本项目可用 Android Studio 打开构建，也可以使用命令行构建。当前机器已在 WSL 用户目录安装 Android SDK：

```text
/home/april/android-sdk-codex
```

项目目录中下载了本地 Gradle：

```text
D:\projects\star\.tools\gradle-8.9
```

`.tools/`、`.android-sdk/`、`app/build/` 都是本地构建产物，已加入 `.gitignore`。

## 命令行构建

在 WSL 中运行：

```bash
cd /mnt/d/projects/star
export ANDROID_HOME=/home/april/android-sdk-codex
export ANDROID_SDK_ROOT=/home/april/android-sdk-codex
.tools/gradle-8.9/bin/gradle testDebugUnitTest assembleDebug assembleRelease --no-daemon
```

## APK 路径

推荐安装：

```text
D:\projects\star\app\build\outputs\apk\release\app-release.apk
```

备用调试包：

```text
D:\projects\star\app\build\outputs\apk\debug\app-debug.apk
```

## vivo 手机安装

1. 把 `app-release.apk` 发送到手机。
2. 在文件管理器中点击 APK。
3. 按系统提示允许当前来源安装未知应用。
4. 点击安装并打开。

应用不申请网络权限，不包含广告、登录、统计、联网或充值 SDK。
