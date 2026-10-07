# 离线方块消除游戏

Android 原生离线方块消除游戏，使用 Kotlin + Jetpack Compose 开发。

## 当前实现

- 固定竖屏，包名 `com.april.blockstar`，不申请 `INTERNET` 权限。
- 10x10 棋盘，底部 3 个待放置方块。
- 支持拖拽放置，并保留点击方块后点击棋盘的备用放置方式。
- 支持行、列、多行、多列和行列交叉同时消除。
- 当前分数、历史最高分、金币、未结束棋局使用 DataStore 持久化。
- 支持刷新、删除、增加 3 个道具：每次 150 金币、每局各限 3 次；刷新会把剩余候选全部变成单格。
- 无法放置的候选方块会显示炸弹，可花 200 金币移除。
- 候选形状包含 1～5 格直线、2×2、2×3、3×3、3/4/5 格 L 和凹形，方向随机；凹形出现率约为其他类别的一半。
- 消除两条及以上行/列时，随机显示约 1 秒的“好 / 棒 / 酷”提示。
- 一局结束后获得 `当前分数 ÷ 50` 向上取整的金币。
- 支持死局弹窗、结束本局结算、重新开始、暂停/继续、振动开关。
- 使用 Compose Canvas 绘制宝石方块、棋盘、星光背景和轻量反馈效果。
- 不包含广告、登录、统计、联网、充值或排行榜 SDK。

## 已验证

在 WSL + Android SDK 环境执行通过：

```bash
.tools/gradle-8.9/bin/gradle testDebugUnitTest assembleDebug assembleRelease --no-daemon
```

已生成可安装 APK：

```text
D:\projects\star\app\build\outputs\apk\release\app-release.apk
```

该 release 包使用 Android 默认 debug keystore 签名，适合直接安装到手机测试；如需正式发布，应改为自己的 release keystore。

备用 debug 包：

```text
D:\projects\star\app\build\outputs\apk\debug\app-debug.apk
```

## 安装

把 `app-release.apk` 发送到 vivo 手机，打开文件后允许“安装未知来源应用”，然后点击安装即可。应用不需要网络权限。
