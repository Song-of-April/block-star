# 从零开始操作说明

这份说明适用于 Windows + Android Studio + Codex。

---

## 一、准备软件

安装：

1. Android Studio 稳定版。
2. Android SDK。
3. JDK 17，通常 Android Studio 已自带。
4. Git。
5. Codex 命令行或你正在使用的 Codex 编辑环境。

手机端准备：

1. vivo 手机进入“设置”。
2. 打开“关于手机”。
3. 连续点击软件版本号，开启开发者模式。
4. 在开发者选项中打开 USB 调试。
5. 用数据线连接电脑。
6. 手机上弹出授权时点击允许。

---

## 二、创建工作目录

建议在不需要管理员权限的位置创建项目，例如：

```text
D:\Projects\BlockStar
```

不要把项目放在：

```text
C:\Program Files
C:\Windows
```

若 D 盘仍要求管理员权限，先修复 D 盘文件夹权限，再开始开发。

在终端中：

```powershell
mkdir D:\Projects\BlockStar
cd D:\Projects\BlockStar
git init
```

---

## 三、把文档放进项目

将以下文件放在项目根目录：

```text
README.md
CODEX_PROMPTS.md
HOW_TO_BUILD.md
```

README 是规则标准。Codex 实现时若出现歧义，以 README 为准。

---

## 四、按阶段使用 Codex

不要一次要求 Codex 写完整游戏。

正确方式：

1. 打开 `CODEX_PROMPTS.md`。
2. 复制 Prompt 0 给 Codex。
3. 等 Codex 完成。
4. 在 Android Studio 中运行。
5. 确认能编译。
6. 提交一次 Git。
7. 再复制 Prompt 1。
8. 每个阶段重复测试和提交。

每阶段建议提交：

```bash
git add .
git commit -m "finish phase 1 game engine"
```

这样 Codex 把项目改坏时可以回退。

查看提交：

```bash
git log --oneline
```

回到上一个提交：

```bash
git reset --hard HEAD~1
```

注意：回退会删除未提交修改。

---

## 五、每个阶段你要亲自检查什么

### 阶段 0

- Android Studio 能打开。
- Gradle 同步成功。
- 手机能显示空白页面。
- 横着拿手机也不会旋转。

### 阶段 1

运行测试：

```bash
gradlew test
```

Windows PowerShell 中通常使用：

```powershell
.\gradlew.bat test
```

必须看到测试通过。

### 阶段 2

- 点击选择方块。
- 点击棋盘能放置。
- 分数正确。
- 三个方块用完后更新。
- 死局会结束。

### 阶段 3

重点真机测试：

- 拖动是否跟手。
- 方块是否位于手指上方。
- 贴边是否放得准。
- 松手是否误放。
- 快速操作会不会重复放置。

### 阶段 4

测试保存：

1. 玩几步。
2. 记住分数和棋盘。
3. 从系统最近任务划掉应用。
4. 重新打开。
5. 检查棋盘、分数和金币是否恢复。

### 阶段 5～7

逐个测试道具：

- 金币不足。
- 金币刚好。
- 点击取消。
- 连续快速点击。
- 死局中使用。
- 使用后退出再打开。

### 阶段 8

- 造出死局。
- 选择道具继续。
- 再造死局。
- 选择结束。
- 检查结算金币是否只加一次。

### 阶段 9～10

最后再追求好看。

不要在核心规则未稳定时先做复杂动画，否则 Codex 很容易把状态和动画混在一起，造成重复计分或重复消除。

---

## 六、如何连接 vivo 手机运行

Android Studio 中：

1. 用 USB 连接 vivo 手机。
2. 手机允许 USB 调试。
3. Android Studio 顶部设备列表选择手机。
4. 点击绿色运行按钮。
5. 第一次安装可能需要在手机上允许“通过 USB 安装”。

若手机不出现：

```powershell
adb devices
```

正常应显示一个设备编号和 `device`。

若显示 `unauthorized`：

1. 拔掉数据线。
2. 在手机开发者选项撤销 USB 调试授权。
3. 重新连接。
4. 再次点击允许。

---

## 七、生成 APK

### Debug APK

Android Studio：

```text
Build
→ Build Bundle(s) / APK(s)
→ Build APK(s)
```

通常生成在：

```text
app\build\outputs\apk\debug\app-debug.apk
```

这个 APK 可以直接发给家人安装测试。

### Release APK

Android Studio：

```text
Build
→ Generate Signed Bundle / APK
→ APK
```

然后：

1. 创建 keystore。
2. 保存 keystore 文件和密码。
3. 选择 release。
4. 完成签名构建。

通常生成在：

```text
app\build\outputs\apk\release\app-release.apk
```

不要丢失 keystore。以后升级同一个应用时必须使用同一签名。

---

## 八、安装 APK

将 APK 发到 vivo 手机。

安装时：

1. 点击 APK。
2. 允许当前文件管理器安装未知应用。
3. 点击安装。
4. 安装完成后打开。

应用不需要网络权限。

---

## 九、发现问题时怎样告诉 Codex

不要只说“有 bug”。

应提供：

```text
设备：vivo XXX
Android 版本：XX
所在阶段：阶段 3
操作步骤：
1. 按住横向 3 格
2. 拖到棋盘最右边
3. 松手

预期：
方块放在第 8～10 列

实际：
方块被放在第 7～9 列

日志：
粘贴 Logcat 中红色错误
```

越具体，Codex 越容易修复。

---

## 十、建议的开发顺序

严格按以下顺序：

```text
项目能运行
→ 纯游戏逻辑
→ 简单点击版
→ 拖动版
→ 分数和最高分
→ 存档
→ 删除道具
→ 刷新道具
→ 增加道具
→ 金币
→ 死局处理
→ 界面还原
→ 动画
→ APK
```

不要改变成：

```text
先画精美界面
→ 先做大量动画
→ 最后补游戏规则
```

这种顺序最容易导致项目卡住。

---

## 十一、第一版可以接受的简化

第一版先做到：

- 纯色方块。
- 无粒子动画。
- 无鼓励文字。
- 道具弹窗很简单。
- 金币规则简单。
- 只有一种背景。

只要以下内容正确，第一版就是成功的：

- 可拖动。
- 可放置。
- 可消除。
- 分数正确。
- 最高分正确。
- 可保存。
- 无广告。
- 无网络。
- vivo 能安装。

之后再慢慢还原视觉效果。
