# Builds Chinese localization assets:
#   assets/data/strings_zh.dat          (UI + item name overlay)
#   assets/data/messages/*_zh.dat       (translated dialogues)
#   tools/zh_charset.txt                (charset for the font generator)
# Run from repo root:  python tools/gen_localization.py

import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(__file__))
from zh_ui import ZH_UI
from zh_items import ZH_ITEMS
from zh_messages import ZH_MESSAGES

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, "assets")

LS_CLASS = "com.interrupt.dungeoneer.game.LocalizedString"


def load_json_lenient(path):
    text = open(path, encoding="utf-8").read()
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        repaired = re.sub(r'"\s*\n(\s*")', '",\n\\1', text, count=1)
        return json.loads(repaired)


def build_strings():
    strings = load_json_lenient(os.path.join(ASSETS, "data", "strings.dat"))
    out = {"$schema": strings.get("$schema", "")}
    translated = 0
    missing = []

    overlay = {}
    overlay.update(ZH_UI)
    overlay.update(ZH_ITEMS)

    for key, entry in strings.items():
        if key == "$schema":
            continue
        if key in overlay:
            zh = overlay[key]
            translated += 1
        else:
            # Keycap names and other technical strings keep English.
            zh = entry["localizedName"]
        out[key] = {"class": LS_CLASS, "localizedName": zh}
        if "comment" in entry:
            out[key]["comment"] = entry["comment"]
        if "form" in entry:
            out[key]["form"] = entry["form"]
        if "forms" in entry:
            out[key]["forms"] = entry["forms"]

    # Extra keys that exist only in items.dat (no English entry)
    for key, zh in overlay.items():
        if key not in out:
            out[key] = {"class": LS_CLASS, "localizedName": zh}
            translated += 1

    # New UI keys introduced by the localization feature itself must exist in
    # the English file too, otherwise English mode would show raw keys.
    new_keys = {
        "screens.OptionsScreen.languageLabel": "Language",
        "screens.OptionsScreen.languageEnglish": "English",
        "screens.OptionsScreen.languageChinese": "中文",
    }
    dirty = False
    for key, en in new_keys.items():
        if key not in strings:
            strings[key] = {"class": LS_CLASS, "localizedName": en}
            dirty = True
    if dirty:
        with open(os.path.join(ASSETS, "data", "strings.dat"), "w", encoding="utf-8", newline="\n") as f:
            json.dump(strings, f, ensure_ascii=False, indent=4)
        print("strings.dat: added language keys")

    for key in overlay:
        if key.startswith("screens.") and key not in strings:
            missing.append(key)

    with open(os.path.join(ASSETS, "data", "strings_zh.dat"), "w", encoding="utf-8", newline="\n") as f:
        json.dump(out, f, ensure_ascii=False, indent=4)

    print(f"strings_zh.dat: {len(out)} keys ({translated} translated)")
    if missing:
        print("WARNING keys not in original strings.dat:", missing)


def build_messages():
    msg_dir = os.path.join(ASSETS, "data", "messages")
    count = 0
    for rel, zh_data in ZH_MESSAGES.items():
        src = os.path.join(msg_dir, rel)
        en = load_json_lenient(src)
        en_groups = en.get("messages", [])
        zh_groups = zh_data.get("messages", [])
        if len(en_groups) != len(zh_groups):
            print(f"ERROR {rel}: group count {len(en_groups)} != {len(zh_groups)}")
            continue
        for i, (eg, zg) in enumerate(zip(en_groups, zh_groups)):
            if len(eg) != len(zg):
                print(f"ERROR {rel} group {i}: line count {len(eg)} != {len(zg)}")
        dst = os.path.join(msg_dir, os.path.splitext(rel)[0] + "_zh.dat")
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        data = {"repeats": en.get("repeats", False), "messages": zh_groups}
        with open(dst, "w", encoding="utf-8", newline="\n") as f:
            json.dump(data, f, ensure_ascii=False, indent=4)
        count += 1
    print(f"messages: wrote {count} _zh.dat files")


def build_charset():
    charset = set()
    for i in range(32, 127):
        charset.add(chr(i))
    charset.add("\n")

    strings_zh = json.load(open(os.path.join(ASSETS, "data", "strings_zh.dat"), encoding="utf-8"))
    for v in strings_zh.values():
        if isinstance(v, dict) and "localizedName" in v:
            charset.update(v["localizedName"])

    for rel in ZH_MESSAGES:
        for grp in ZH_MESSAGES[rel]["messages"]:
            for line in grp:
                charset.update(line)

    # Full-width punctuation and common extras used by future text
    charset.update("：，。！？、；·—…《》〈〉「」『』（）“”‘’×÷←→↑↓")

    chars = sorted(c for c in charset if c != "\n")
    with open(os.path.join(ROOT, "tools", "zh_charset.txt"), "w", encoding="utf-8") as f:
        f.write("".join(chars))
    cjk = [c for c in chars if ord(c) > 0x2E00]
    print(f"charset: {len(chars)} chars ({len(cjk)} CJK/wide)")


if __name__ == "__main__":
    build_strings()
    build_messages()
    build_charset()
