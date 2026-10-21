# Delver Web — 在浏览器中玩 Delver

[English](README.md) | [中文](README.zh-CN.md)

本仓库是开源第一人称地牢 Roguelike 游戏 [Delver](https://github.com/Aureusus/DelverStride2D)（Priority Interrupt LLC 出品）的浏览器移植版：原始 **libGDX** Java 代码通过 **[TeaVM](https://teavm.org/)**（[gdx-teavm](https://github.com/xpenatan/gdx-teavm) 后端）编译为 JavaScript/WebAssembly，可以直接在网页中游玩。仓库同时包含完整的**中文（zh-CN）本地化工具链**与已翻译的游戏资源。

**在线游玩：** https://zombie-kaiser.github.io/delver-web/

## 仓库结构

| 目录 | 说明 |
| --- | --- |
| `html/webapp/` | 已编译完成、开箱即玩的网页版（`app.js`、资源、`index.html`） |
| `core/` | Java 游戏源码（libGDX） |
| `html/` | TeaVM 网页后端源码与构建配置 |
| `assets/` | 游戏数据，含生成的中文本地化文件（`strings_zh.dat`、`messages/*_zh.dat`） |
| `tools/` | 生成中文本地化与点阵字体的 Python 脚本 |
| `converter/` | 数据转换工具 |
| `serve.py` | 用于本地测试的简易 HTTP 服务器 |

## 本地运行

编译好的网页版位于 `html/webapp/`，直接启动静态服务器即可：

```bash
python serve.py
# 然后打开 http://127.0.0.1:8138
```

也可以用任意静态文件服务器指向 `html/webapp/`。

## 从源码构建

需要 JDK 17 或更高版本：

```bash
./gradlew teavm        # 将游戏编译为 JS，输出到 html/webapp/
```

## 重新生成中文本地化

需要 Python 3。生成器会把界面文本、物品名称与对话翻译合并进游戏数据文件，并生成覆盖所需字符集的字体：

```bash
python tools/gen_localization.py
./gradlew teavm        # 重新打包新文本
```

翻译源文件位于 `tools/zh_ui.py`、`tools/zh_items.py`、`tools/zh_messages.py`。

## 致谢与许可

- **Delver** © Priority Interrupt LLC（Joshua Skelton 及贡献者），采用 zlib 许可证发布，详见 [LICENSE.txt](LICENSE.txt)。
- 网页版构建基于 [libGDX](https://libgdx.com/) 与 [TeaVM](https://teavm.org/)。
- 中文本地化由本仓库维护者完成。

Delver 同样在 [Steam](https://store.steampowered.com/app/249630/Delver/) 上发售——喜欢这款游戏的话请支持原作者！
