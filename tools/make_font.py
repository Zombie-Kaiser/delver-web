# Generates a CJK-capable bitmap font for the Chinese localization:
#   assets/ui/pixel_zh.fnt   (AngelCode text format, single page)
#   assets/ui/pixel_zh.png   (power-of-two texture, white glyphs on transparent)
# Charset comes from tools/zh_charset.txt (built by gen_localization.py).
# Run from repo root:  python tools/make_font.py [font_size] [font_path]

import os
import sys

from PIL import Image, ImageDraw, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

FONT_SIZE = int(sys.argv[1]) if len(sys.argv) > 1 else 16
FONT_PATH = sys.argv[2] if len(sys.argv) > 2 else "C:/Windows/Fonts/simhei.ttf"
OUT_FNT = os.path.join(ROOT, "assets", "ui", "pixel_zh.fnt")
OUT_PNG = os.path.join(ROOT, "assets", "ui", "pixel_zh.png")
CHARSET_FILE = os.path.join(ROOT, "tools", "zh_charset.txt")

PAD = 2
TEXTURE_W = 2048
TEXTURE_H = 256


def main():
    chars = open(CHARSET_FILE, encoding="utf-8").read()
    chars = "".join(sorted(set(chars)))
    # Guarantee full printable ASCII
    for i in range(32, 127):
        c = chr(i)
        if c not in chars:
            chars += c
    chars = sorted(set(chars))

    font = ImageFont.truetype(FONT_PATH, FONT_SIZE)
    ascent, descent = font.getmetrics()
    line_height = ascent + descent
    base = ascent

    # Precompute glyph metrics
    glyphs = []
    for c in chars:
        if c == " ":
            glyphs.append({"id": ord(c), "w": 0, "h": 0, "xoff": 0, "yoff": 0, "adv": round(font.getlength(c)), "img": None})
            continue
        bbox = font.getbbox(c, anchor="ls")
        x0, y0, x1, y1 = bbox
        w, h = max(0, x1 - x0), max(0, y1 - y0)
        if w == 0 or h == 0:
            glyphs.append({"id": ord(c), "w": 0, "h": 0, "xoff": 0, "yoff": 0, "adv": round(font.getlength(c)), "img": None})
            continue
        img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        draw.text((-x0, -y0), c, font=font, fill=(255, 255, 255, 255), anchor="ls")
        glyphs.append({
            "id": ord(c), "w": w, "h": h,
            "xoff": x0, "yoff": base + y0,
            "adv": round(font.getlength(c)), "img": img,
        })

    # Shelf packing (tallest first keeps shelves tight)
    packed = sorted([g for g in glyphs if g["img"]], key=lambda g: -g["h"])
    x, y, shelf_h = PAD, PAD, 0
    for g in packed:
        w, h = g["w"], g["h"]
        if x + w + PAD > TEXTURE_W:
            x = PAD
            y += shelf_h + PAD
            shelf_h = 0
        g["x"], g["y"] = x, y
        x += w + PAD
        shelf_h = max(shelf_h, h)
    used_h = y + shelf_h + PAD

    print(f"texture: {TEXTURE_W}x{TEXTURE_W} (used height {used_h}), glyphs: {len(glyphs)}")

    atlas = Image.new("RGBA", (TEXTURE_W, TEXTURE_H), (0, 0, 0, 0))
    for g in packed:
        atlas.paste(g["img"], (g["x"], g["y"]))
    atlas.save(OUT_PNG)
    print(f"wrote {OUT_PNG}")

    lines = [
        f'info face="pixel_zh" size={FONT_SIZE} bold=0 italic=0 charset="" unicode=1 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=0,0 outline=0',
        f"common lineHeight={line_height} base={base} scaleW={TEXTURE_W} scaleH={TEXTURE_H} pages=1 packed=0 alphaChnl=0 redChnl=4 greenChnl=4 blueChnl=4",
        'page id=0 file="pixel_zh.png"',
        f"chars count={len(glyphs)}",
    ]
    for g in sorted(glyphs, key=lambda g: g["id"]):
        lines.append(
            f'char id={g["id"]} x={g.get("x", 0)} y={g.get("y", 0)} width={g["w"]} height={g["h"]} '
            f'xoffset={g["xoff"]} yoffset={g["yoff"]} xadvance={g["adv"]} page=0 chnl=15'
        )
    with open(OUT_FNT, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lines) + "\n")
    print(f"wrote {OUT_FNT}")


if __name__ == "__main__":
    main()
