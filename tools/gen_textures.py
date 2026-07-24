#!/usr/bin/env python3
"""Genera las texturas del león. Las regiones corresponden a los texOffs de LionModel.
Layout de un box WxHxD en texOffs(u,v): franja superior (tapas) en y=[v, v+D),
franja inferior (caras) en y=[v+D, v+D+H); caras: [D][W][D][W] de ancho."""
from PIL import Image, ImageDraw
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/gianni/textures"

PALETTES = {
    "lion_male":   {"base": (198, 154, 98), "dark": (150, 110, 66), "mane": (107, 74, 38)},
    "lion_female": {"base": (198, 154, 98), "dark": (150, 110, 66), "mane": (150, 110, 66)},
    "lion_white":  {"base": (232, 228, 218), "dark": (190, 186, 176), "mane": (200, 196, 186)},
}
BLACK = (30, 24, 18)
PINK = (120, 70, 60)


def box_region(d, u, v, w, h, dep, base, dark):
    # franja de tapas + franja de caras, con leve variación para que no sea plano
    d.rectangle([u + dep, v, u + dep + 2 * w - 1, v + dep - 1], fill=dark)          # tapas
    d.rectangle([u, v + dep, u + 2 * (dep + w) - 1, v + dep + h - 1], fill=base)    # caras
    d.rectangle([u, v + dep, u + dep - 1, v + dep + h - 1], fill=dark)              # cara lateral sombreada


def paint(name, pal):
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    box_region(d, 0, 0, 8, 8, 6, pal["base"], pal["dark"])       # cabeza
    box_region(d, 28, 0, 4, 3, 3, pal["base"], pal["dark"])      # hocico
    box_region(d, 28, 6, 2, 2, 1, pal["dark"], pal["dark"])      # oreja izq
    box_region(d, 34, 6, 2, 2, 1, pal["dark"], pal["dark"])      # oreja der
    box_region(d, 40, 10, 11, 11, 3, pal["mane"], pal["mane"])   # melena
    box_region(d, 0, 20, 10, 10, 16, pal["base"], pal["dark"])   # cuerpo
    box_region(d, 0, 46, 3, 10, 3, pal["base"], pal["dark"])     # pata A
    box_region(d, 12, 46, 3, 10, 3, pal["base"], pal["dark"])    # pata B
    box_region(d, 56, 0, 1, 9, 1, pal["dark"], pal["dark"])      # cola
    box_region(d, 36, 46, 11, 3, 8, (90, 60, 40), (60, 40, 25))  # montura
    box_region(d, 0, 56, 7, 1, 7, (70, 45, 30), (50, 32, 20))    # collar (cuero)
    for cx in (2, 6, 10, 14, 18, 22):                            # colmillos colgando
        d.point([(cx, 63)], fill=(240, 236, 224))
    # cara frontal de la cabeza: box (0,0) 8x8x6 -> cara frontal en (6,6)..(13,13)
    d.point([(8, 9), (11, 9)], fill=BLACK)                        # ojos
    d.rectangle([9, 11, 10, 12], fill=PINK)                       # nariz
    (OUT / "entity/lion").mkdir(parents=True, exist_ok=True)
    im.save(OUT / f"entity/lion/{name}.png")


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


for name, pal in PALETTES.items():
    paint(name, pal)
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
