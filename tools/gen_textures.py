#!/usr/bin/env python3
"""Genera las texturas del león. Las regiones corresponden a los texOffs de LionModel
(atlas 128x128, Task 7). Layout de un box WxHxD en texOffs(u,v): franja superior
(tapas) en y=[v, v+D), franja inferior (caras) en y=[v+D, v+D+H); caras en el
orden [D][W][D][W] de ancho (right, front, left, back)."""
from PIL import Image, ImageDraw
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/gianni/textures"

# --- Painter v2: león 128x128 ---
ATLAS = 128


def ramp(base, n=5, spread=0.42):
    """n tonos del más oscuro al más claro alrededor de base."""
    out = []
    for i in range(n):
        f = 1.0 - spread + (2 * spread) * i / (n - 1)
        out.append(tuple(min(255, int(c * f)) for c in base))
    return out


RAMPS = {
    "lion_male":   {"fur": ramp((198, 154, 98)), "mane": ramp((107, 74, 38)), "belly": ramp((222, 196, 156))},
    "lion_female": {"fur": ramp((204, 162, 108)), "mane": ramp((150, 110, 66)), "belly": ramp((226, 202, 164))},
    "lion_white":  {"fur": ramp((232, 228, 218)), "mane": ramp((200, 196, 186)), "belly": ramp((244, 242, 236))},
    "lion_black":  {"fur": ramp((52, 48, 46)), "mane": ramp((22, 20, 20)), "belly": ramp((84, 80, 76))},
}
BLACK = (30, 24, 18)
PINK = (198, 120, 110)
EYE_AMBER = (216, 168, 60)


def fur_box(d, u, v, w, h, dep, r, rng, stroke=True):
    """Pinta un box con sombreado por cara + trazos de pelaje.
    r: rampa de 5 tonos. Tapa superior clara, inferior oscura, frente medio,
    laterales medio-oscuro, con strokes verticales +-1 tono."""
    top, bottom = r[3], r[0]
    d.rectangle([u + dep, v, u + dep + w - 1, v + dep - 1], fill=top)                    # tapa sup
    d.rectangle([u + dep + w, v, u + dep + 2 * w - 1, v + dep - 1], fill=bottom)         # tapa inf
    faces = [(u, dep, r[1]), (u + dep, w, r[2]), (u + dep + w, dep, r[1]), (u + dep + w + dep, w, r[2])]
    for fx, fw, tone in faces:
        d.rectangle([fx, v + dep, fx + fw - 1, v + dep + h - 1], fill=tone)
        if stroke:
            for sx in range(fx, fx + fw):
                if rng.random() < 0.45:
                    sy = v + dep + rng.randrange(max(1, h))
                    ln = rng.randrange(1, 3)
                    idx = 1 if rng.random() < 0.5 else 3
                    d.line([(sx, sy), (sx, min(v + dep + h - 1, sy + ln))], fill=r[idx])
    # oclusión abajo de cada cara
    d.rectangle([u, v + dep + h - 1, u + 2 * (dep + w) - 1, v + dep + h - 1], fill=r[0])


def paint_lion(name, ramps, rng):
    im = Image.new("RGBA", (ATLAS, ATLAS), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    fur, mane, belly = ramps["fur"], ramps["mane"], ramps["belly"]
    # tabla del atlas (pieza -> u, v, W, H, D) — DEBE coincidir con LionModel
    fur_box(d, 0, 0, 8, 8, 7, fur, rng)          # head
    fur_box(d, 30, 0, 4, 3, 4, fur, rng)         # snout
    fur_box(d, 30, 8, 4, 2, 3, fur, rng)         # jaw
    fur_box(d, 46, 0, 2, 3, 1, fur, rng, stroke=False)   # ear_r
    fur_box(d, 52, 0, 2, 3, 1, fur, rng, stroke=False)   # ear_l
    fur_box(d, 40, 34, 11, 10, 8, fur, rng)      # body_front
    fur_box(d, 0, 34, 9, 9, 8, fur, rng)         # body_back
    fur_box(d, 80, 34, 7, 2, 12, belly, rng)     # belly
    fur_box(d, 0, 52, 4, 6, 4, fur, rng)         # front_upper
    fur_box(d, 16, 52, 3, 8, 3, fur, rng)        # front_lower
    fur_box(d, 32, 52, 4, 6, 4, fur, rng)        # hind_upper
    fur_box(d, 48, 52, 3, 8, 3, fur, rng)        # hind_lower
    fur_box(d, 64, 52, 2, 6, 2, fur, rng)        # tail1
    fur_box(d, 74, 52, 1, 5, 1, fur, rng, stroke=False)  # tail2
    fur_box(d, 82, 52, 3, 4, 3, mane, rng)       # tail_tuft
    fur_box(d, 0, 72, 11, 11, 3, mane, rng)      # mane_inner
    fur_box(d, 28, 72, 12, 12, 3, mane, rng)     # mane_mid
    fur_box(d, 58, 72, 13, 13, 3, mane, rng)     # mane_outer
    fur_box(d, 64, 0, 11, 3, 9, ramp((90, 60, 40)), rng, stroke=False)   # saddle
    fur_box(d, 64, 16, 7, 2, 7, ramp((70, 45, 30)), rng, stroke=False)   # collar
    # cara frontal del head: box(0,0) 8x8x7, orden [D][W][D][W] -> frente en x=[7,15), y=[7,15)
    fx, fy = 7, 7
    d.rectangle([fx + 2, fy + 1, fx + 2, fy + 2], fill=BLACK)            # ojo izq (2x2 con brillo)
    d.rectangle([fx + 5, fy + 1, fx + 5, fy + 2], fill=BLACK)
    d.point([(fx + 2, fy + 1), (fx + 5, fy + 1)], fill=EYE_AMBER)
    # hocico: cara frontal box(30,0) 4x3x4 -> frente x=[34,38), y=[4,7)
    d.rectangle([35, 4, 36, 5], fill=PINK)                                # nariz
    d.point([(34, 6), (37, 6)], fill=BLACK)                               # comisuras
    # interior de orejas (cara frontal de ear boxes): x=[47,49) y=[1,4) / x=[53,55)
    d.rectangle([47, 1, 48, 3], fill=PINK)
    d.rectangle([53, 1, 54, 3], fill=PINK)
    (OUT / "entity/lion").mkdir(parents=True, exist_ok=True)
    im.save(OUT / f"entity/lion/{name}.png")
    return im


def paint_eyes():
    im = Image.new("RGBA", (ATLAS, ATLAS), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rectangle([9, 8, 9, 9], fill=EYE_AMBER)     # mismos px que los ojos del head
    d.rectangle([12, 8, 12, 9], fill=EYE_AMBER)
    (OUT / "entity/lion").mkdir(parents=True, exist_ok=True)
    im.save(OUT / "entity/lion/lion_eyes.png")


def preview(images):
    """Hoja de preview: variantes ampliadas x8 lado a lado + zoom x16 de la cara."""
    scale = 8
    w = ATLAS * scale
    sheet = Image.new("RGBA", (w * len(images), w + 40 * scale), (40, 40, 46, 255))
    for i, (name, im) in enumerate(images):
        big = im.resize((w, w), Image.NEAREST)
        sheet.paste(big, (i * w, 0), big)
        face = im.crop((0, 0, 40, 20)).resize((40 * 16, 20 * 16), Image.NEAREST)
        sheet.paste(face, (i * w, w), face)
    sheet.save(Path(__file__).resolve().parent / "preview_leones.png")


def egg():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse([3, 2, 12, 13], fill=(198, 154, 98))
    d.ellipse([5, 5, 7, 7], fill=(107, 74, 38))
    d.ellipse([8, 9, 10, 11], fill=(107, 74, 38))
    (OUT / "item").mkdir(parents=True, exist_ok=True)
    im.save(OUT / "item/lion_spawn_egg.png")


GOLD = (198, 154, 98)
GOLD_DARK = (150, 110, 66)
WHITE = (232, 228, 218)
WHITE_DARK = (190, 186, 176)


def pelt(name, base, dark):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rounded_rectangle([2, 3, 13, 12], radius=3, fill=base)
    for x, y in [(4, 5), (7, 8), (10, 5), (6, 10), (11, 9)]:
        d.point([(x, y)], fill=dark)
    d.rectangle([2, 12, 13, 13], fill=dark)
    (OUT / "item").mkdir(parents=True, exist_ok=True)
    im.save(OUT / f"item/{name}.png")


def fang():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.polygon([(6, 2), (10, 2), (9, 9), (8, 13), (7, 9)], fill=(240, 236, 224))
    d.line([(8, 4), (8, 11)], fill=(200, 194, 180))
    im.save(OUT / "item/lion_fang.png")


def collar_item():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.arc([2, 4, 13, 15], start=200, end=340, fill=(70, 45, 30), width=2)
    for x, y in [(4, 11), (8, 13), (12, 11)]:
        d.polygon([(x, y), (x + 1, y), (x, y + 3)], fill=(240, 236, 224))
    im.save(OUT / "item/fang_collar.png")


def carpet_block(name, base, dark):
    im = Image.new("RGBA", (16, 16), base)
    d = ImageDraw.Draw(im)
    for x, y in [(1, 2), (5, 6), (9, 1), (13, 5), (3, 11), (8, 9), (12, 13), (6, 14), (14, 10), (2, 7)]:
        d.point([(x, y), (x + 1, y)], fill=dark)
    (OUT / "block").mkdir(parents=True, exist_ok=True)
    im.save(OUT / f"block/{name}.png")


GATERO = (140, 190, 120)
GATERO_DARK = (95, 140, 80)
DOPANTE = (110, 150, 70)
DOPANTE_RED = (170, 80, 60)


def yuyo_item(name, base, dark, accent):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for x0, y0 in [(7, 3), (4, 6), (10, 6)]:                 # tallos
        d.line([(8, 13), (x0, y0)], fill=dark)
    for x, y in [(7, 2), (3, 5), (10, 5), (6, 8), (9, 9)]:   # hojas
        d.rectangle([x, y, x + 1, y + 1], fill=base)
    d.point([(8, 4), (5, 7)], fill=accent)
    im.save(OUT / f"item/{name}.png")


def yuyo_stages(name, base, dark, accent):
    for stage in range(4):
        im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        h = 4 + stage * 3                                     # altura por etapa
        for x0 in (4, 8, 12):
            d.line([(x0, 15), (x0, 15 - h)], fill=dark)
            for dy in range(2, h, 3):
                d.point([(x0 - 1, 15 - dy), (x0 + 1, 15 - dy - 1)], fill=base)
        if stage == 3:
            d.point([(4, 15 - h), (8, 15 - h), (12, 15 - h)], fill=accent)
        im.save(OUT / f"block/{name}_stage{stage}.png")


def yuyo_particle():
    im = Image.new("RGBA", (8, 8), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse([2, 2, 5, 5], fill=(150, 210, 130, 255))
    d.point([(3, 3)], fill=(220, 240, 200, 255))
    (OUT / "particle").mkdir(parents=True, exist_ok=True)
    im.save(OUT / "particle/yuyo.png")


import random as _random
_rng = _random.Random(42)                          # determinista: mismo output en cada corrida
imgs = [(n, paint_lion(n, r, _rng)) for n, r in RAMPS.items()]
paint_eyes()
preview(imgs)
egg()
pelt("lion_pelt", GOLD, GOLD_DARK)
pelt("white_lion_pelt", WHITE, WHITE_DARK)
fang()
collar_item()
carpet_block("lion_pelt_carpet", GOLD, GOLD_DARK)
carpet_block("white_lion_pelt_carpet", WHITE, WHITE_DARK)
yuyo_item("yuyo_gatero", GATERO, GATERO_DARK, (220, 230, 200))
yuyo_item("yuyo_dopante", DOPANTE, DOPANTE_RED, DOPANTE_RED)
yuyo_stages("yuyo_gatero", GATERO, GATERO_DARK, (220, 230, 200))
yuyo_stages("yuyo_dopante", DOPANTE, DOPANTE_RED, DOPANTE_RED)
yuyo_particle()
print("ok")
