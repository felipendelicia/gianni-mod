# Gianni Mod 🦁

Mod de leones para **Minecraft Java 26.2** con **NeoForge**.

Agrega leones a las sabanas: cazan de noche, descansan de día, y se pueden domesticar, montar y criar. Con variantes (macho con melena, hembra, el raro león blanco, y el aún más raro león negro), yuyos cultivables, loot propio, advancements y guaridas generadas en el mundo.

## Características

### Los leones
- **Spawn natural** en sabanas, en manadas de 2–4 con variantes mezcladas, más **guaridas** generadas (roca con huesos y su manada, ~1 cada 40 chunks).
- **León negro**: variante rara (3 % de los leones salvajes), con melena igual que el macho normal; se puede heredar al criar.
- **Día/noche**: de día los salvajes descansan echados y no molestan; de noche cazan vacas, ovejas, cerdos, gallinas, conejos… y jugadores.
- **Rugido**: el macho ruge al anochecer y al entrar en combate — las presas cercanas huyen en pánico. El león negro, al rugir, reproduce uno de dos audios propios elegido al azar (subtítulo "Un león negro opina").
- **Acecho**: se agachan al fijar una presa lejana antes de correr a atacarla.
- **Cachorros** que se persiguen y saltan entre ellos.
- **Modelo y texturas**: hocico, orejas, melena en capas (solo machos adultos) y cola con mechón; pelaje con sombreado y cara detallada en las cuatro variantes (hembra, macho, blanco, negro); ojos emisivos de noche y al acechar, apagados de día quieto.

### Domesticación
- Con **carne cruda** (vaca, cerdo, pollo, cordero o conejo): los adultos solo mientras descansan de día (~3 intentos); los cachorros a cualquier hora.
- El león domesticado te sigue, se sienta a pedido (click con mano vacía), defiende a su dueño y se cura con carne.

### Montura
- **Saddle** + click derecho para ensillar; mano vacía para montar. WASD para moverse, espacio para saltar, shift para bajarse.
- **Collar de colmillos** (craft: 2 hilos + 3 colmillos): +30 % de velocidad montado. Se ve puesto en el cuello.

### Cría
- Dos leones domesticados + carne → cachorro. La variante se hereda (el negro incluido), con 5 % de chance extra de mutar a león blanco.

### Yuyos
- **Guaridas**: nacen ya rodeadas de yuyos silvestres maduros, mezcla de gatero y dopante.
- **Cultivo**: se plantan en farmland, crecen en 4 etapas (bonemeal acelera) y al cosechar dropean 1–3.
- **Yuyo gatero**: dárselo a un león (salvaje o manso) lo manda de viaje ~45 s — rueda panza arriba, hace círculos, saltitos y tira corazones con partículas verdes; el salvaje no ataca mientras dura, y si estaba montado te baja.
- **Yuyo dopante**: le da al león Velocidad II, Fuerza I y Regeneración I por 2 minutos (con partículas de poción); montado, se nota en la velocidad y el aguante.

### Loot y crafteos
- Los leones salvajes dropean **pelaje** (blanco si el león es blanco), **carne**, y a veces un **colmillo** (solo adultos salvajes).
- 2 pelajes → 3 **alfombras de pelaje** (dorada o blanca según el pelaje).

### Advancements
Árbol propio "Sabana": Domador → Jinete / Criador → Rey de la sabana, el desafío **Copo de nieve** (domesticar un león blanco), y los de yuyos: **Mal viaje** (primer yuyo gatero) y **Doping** (yuyo dopante estando montado).

## Instalación

1. Instalá [NeoForge](https://neoforged.net/) para Minecraft **26.2**.
2. Descargá `gianni-x.y.z.jar` (de [Releases](../../releases) o compilalo vos, ver abajo).
3. Ponelo en la carpeta `mods/` y listo.

## Desarrollo

Requisito: JDK 25 (el toolchain de Gradle lo descarga solo).

```bash
./gradlew runClient          # jugar con el mod en el entorno de dev
./gradlew runGameTestServer  # correr los gametests
./gradlew build              # compilar: el jar queda en build/libs/
```

- Las texturas son 100 % generadas por script: `python3 tools/gen_textures.py` (Python 3 + Pillow).
- La estructura de los gametests también: `python3 tools/gen_test_structure.py`.
- Checklist de pruebas manuales: [`docs/superpowers/manual-test-checklist.md`](docs/superpowers/manual-test-checklist.md).

## Licencia

All Rights Reserved.
