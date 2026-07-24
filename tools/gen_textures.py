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


for name, pal in PALETTES.items():
    paint(name, pal)
egg()
print("ok")
