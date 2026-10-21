# Validates the Chinese localization against the English originals.
# Run from repo root:  python tools/check_localization.py

import json
import os
import re
import glob

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def load_lenient(path):
    text = open(path, encoding="utf-8").read()
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        repaired = re.sub(r'"\s*\n(\s*")', '",\n\\1', text, count=1)
        return json.loads(repaired)


def main():
    errors = 0

    en = load_lenient(os.path.join(ROOT, "assets/data/strings.dat"))
    zh = load_lenient(os.path.join(ROOT, "assets/data/strings_zh.dat"))

    # 1) placeholder integrity
    for k, v in zh.items():
        if k == "$schema":
            continue
        e_ph = sorted(re.findall(r"\{[0-9]\}", en.get(k, {}).get("localizedName", "")))
        z_ph = sorted(re.findall(r"\{[0-9]\}", v.get("localizedName", "")))
        if set(z_ph) - set(e_ph):
            print(f"PLACEHOLDER ERROR {k}: zh uses {z_ph}, en has {e_ph}")
            errors += 1

    # 2) markup safety on parsed values
    for k, v in zh.items():
        if isinstance(v, dict) and "[" in v.get("localizedName", ""):
            print(f"MARKUP RISK {k}")
            errors += 1

    # 3) message structure match + markup safety
    msg_dir = os.path.join(ROOT, "assets/data/messages")
    for f in sorted(glob.glob(msg_dir + "/**/*_zh.dat", recursive=True)):
        base = f.replace("_zh.dat", ".dat")
        a = load_lenient(f)
        b = load_lenient(base)
        ga = [len(g) for g in a["messages"]]
        gb = [len(g) for g in b["messages"]]
        if ga != gb:
            print(f"STRUCTURE MISMATCH {os.path.relpath(f, msg_dir)}: {ga} vs {gb}")
            errors += 1
        for grp in a["messages"]:
            for line in grp:
                if "[" in line:
                    print(f"MARKUP RISK in {os.path.relpath(f, msg_dir)}: {line[:50]}")
                    errors += 1

    # 4) glyph coverage
    font_ids = set()
    for line in open(os.path.join(ROOT, "assets/ui/pixel_zh.fnt"), encoding="utf-8"):
        if line.startswith("char id="):
            font_ids.add(int(line.split("id=")[1].split()[0]))

    need = set()
    for v in zh.values():
        if isinstance(v, dict):
            need |= {ord(c) for c in v.get("localizedName", "")}
    for f in glob.glob(msg_dir + "/**/*_zh.dat", recursive=True):
        for grp in load_lenient(f)["messages"]:
            for line in grp:
                need |= {ord(c) for c in line}

    missing = {c for c in need if c not in font_ids and c != ord("\n")}
    if missing:
        print(f"MISSING GLYPHS: {[hex(c) + ':' + chr(c) for c in sorted(missing)][:20]}")
        errors += 1

    print("ALL CHECKS PASSED" if errors == 0 else f"{errors} ERRORS FOUND")


if __name__ == "__main__":
    main()
