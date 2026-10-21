# Delver Web — Play Delver in Your Browser

[English](README.md) | [中文](README.zh-CN.md)

A browser-ready port of [Delver](https://github.com/Aureusus/DelverStride2D) — the open-source first-person roguelike by Priority Interrupt LLC — built with **libGDX** and compiled to JavaScript/WebAssembly with **[TeaVM](https://teavm.org/)** (via the [gdx-teavm](https://github.com/xpenatan/gdx-teavm) backend). This repository also ships a complete **Chinese (zh-CN) localization toolchain** and translated game assets.

**Play it now:** https://zombie-kaiser.github.io/delver-web/

## What's Inside

| Path | Description |
| --- | --- |
| `html/webapp/` | Ready-to-run compiled web build (`app.js`, assets, `index.html`) — playable immediately |
| `core/` | Java game source (libGDX) |
| `html/` | TeaVM web backend sources and build config |
| `assets/` | Game data, including generated Chinese localization files (`strings_zh.dat`, `messages/*_zh.dat`) |
| `tools/` | Python scripts that generate the Chinese localization and bitmap fonts |
| `converter/` | Data conversion helpers |
| `serve.py` | Tiny local HTTP server for testing the web build |

## Run Locally

The compiled build lives in `html/webapp/` — just serve it:

```bash
python serve.py
# then open http://127.0.0.1:8138
```

Or use any static file server pointing at `html/webapp/`.

## Build From Source

Requires JDK 17+.

```bash
./gradlew teavm        # compiles the game to JS in html/webapp/
```

## Rebuild the Chinese Localization

Requires Python 3. The generator merges UI strings, item names, and dialogue translations into the game's data files, plus charset-aware font generation:

```bash
python tools/gen_localization.py
./gradlew teavm        # re-bundle with the new strings
```

Translation sources live in `tools/zh_ui.py`, `tools/zh_items.py`, and `tools/zh_messages.py`.

## Credits & License

- **Delver** is © Priority Interrupt LLC (Joshua Skelton & contributors), licensed under the zlib license — see [LICENSE.txt](LICENSE.txt).
- Web build powered by [libGDX](https://libgdx.com/) and [TeaVM](https://teavm.org/).
- Chinese localization by the repository maintainer.

Delver is also available on [Steam](https://store.steampowered.com/app/249630/Delver/) — support the original developers if you enjoy the game!
