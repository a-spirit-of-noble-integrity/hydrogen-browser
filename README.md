# 氢浏览器 Hydrogen Browser (Android 原生工程)

基于 **Kotlin + Jetpack Compose + Material 3** 构建的极简高速浏览器工程，严格按照定制规格书实现：

- **视觉与主页**：夸克式纯净浅色（`#FAFBFC` + `#2563EB`），极简单行「搜索或输入网址」，仅保留「必应」「谷歌」微卡片快捷入口。
- **底部栏与控制中心**：VIA 风格悬浮毛玻璃胶囊栏，5 键顺序固定为 `返回 | 菜单 | 主页 | 标签 | 设置`，菜单弹出圆角 Bottom Sheet 控制中心。
- **油猴方块（Bento Grid）**：内置全网去广告增强、视频倍速悬浮球、沉浸双语翻译、小说自动拼页、强制复制解限；支持 GreasyFork 链接导入与 AI 一句话生成脚本。
- **广告拦截（Strict）**：`WebViewClient.shouldInterceptRequest` 拦截引擎，内置 EasyList China 常见规则、防跳转与 App 唤起屏蔽、手动点击元素屏蔽器。
- **AI 与扩展**：M3U8 视频嗅探、小说阅读模式、单手边缘滑动导航、WebDAV 云同步、网页源码查看。

## 导入编译
1. 使用 **Android Studio Ladybug (2024.2+)** 打开 `HydrogenBrowser` 目录。
2. 等待 Gradle Sync 完成（Compile SDK 35, Min SDK 26）。
3. 连接真机或模拟器点击 **Run** 即可生成并安装 APK。
