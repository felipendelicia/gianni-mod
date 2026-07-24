#!/usr/bin/env python3
"""Genera data/gianni/structure/lion_arena.nbt: plataforma 16x3x16 con piso de
smooth_stone y aire arriba, para que los gametests tengan suelo garantizado."""
import gzip
import struct
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "src/main/resources/data/gianni/structure/lion_arena.nbt"
DATA_VERSION = 4903  # Minecraft 26.2
SIZE_X, SIZE_Y, SIZE_Z = 16, 3, 16


def tag_string(s):
    b = s.encode("utf-8")
    return struct.pack(">H", len(b)) + b


def named(tag_type, name, payload):
    return bytes([tag_type]) + tag_string(name) + payload


def tag_int(v):
    return struct.pack(">i", v)


def tag_list(elem_type, payloads):
    return bytes([elem_type]) + struct.pack(">i", len(payloads)) + b"".join(payloads)


def compound(*named_tags):
    return b"".join(named_tags) + b"\x00"


def block_entry(x, y, z, state):
    return compound(
        named(9, "pos", tag_list(3, [tag_int(x), tag_int(y), tag_int(z)])),
        named(3, "state", tag_int(state)),
    )


def palette_entry(block_id):
    return compound(named(8, "Name", tag_string(block_id)))


blocks = []
for x in range(SIZE_X):
    for z in range(SIZE_Z):
        blocks.append(block_entry(x, 0, z, 0))          # piso
        for y in (1, 2):
            blocks.append(block_entry(x, y, z, 1))      # aire (limpia el área)

root = named(10, "", compound(
    named(9, "size", tag_list(3, [tag_int(SIZE_X), tag_int(SIZE_Y), tag_int(SIZE_Z)])),
    named(9, "entities", tag_list(0, [])),
    named(9, "blocks", tag_list(10, blocks)),
    named(9, "palette", tag_list(10, [
        palette_entry("minecraft:smooth_stone"),
        palette_entry("minecraft:air"),
    ])),
    named(3, "DataVersion", tag_int(DATA_VERSION)),
))

OUT.parent.mkdir(parents=True, exist_ok=True)
with gzip.open(OUT, "wb") as f:
    f.write(root)
print("ok", OUT)
