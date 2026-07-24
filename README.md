# Gianni Mod 🦁

Mod de leones para **Minecraft Java 26.2** con **NeoForge**.

Agrega leones a las sabanas: cazan de noche, descansan de día, y se pueden domesticar, montar y criar. Con variantes (macho con melena, hembra, y el raro león blanco), loot propio, advancements y guaridas generadas en el mundo.

## Características

### Los leones
- **Spawn natural** en sabanas, en manadas de 2–4 con variantes mezcladas, más **guaridas** generadas (roca con huesos y su manada, ~1 cada 40 chunks).
- **Día/noche**: de día los salvajes descansan echados y no molestan; de noche cazan vacas, ovejas, cerdos, gallinas, conejos… y jugadores.
- **Rugido**: el macho ruge al anochecer y al entrar en combate — las presas cercanas huyen en pánico.
- **Acecho**: se agachan al fijar una presa lejana antes de correr a atacarla.
- **Cachorros** que se persiguen y saltan entre ellos.

### Domesticación
- Con **carne cruda** (vaca, cerdo, pollo, cordero o conejo): los adultos solo mientras descansan de día (~3 intentos); los cachorros a cualquier hora.
- El león domesticado te sigue, se sienta a pedido (click con mano vacía), defiende a su dueño y se cura con carne.

### Montura
- **Saddle** + click derecho para ensillar; mano vacía para montar. WASD para moverse, espacio para saltar, shift para bajarse.
- **Collar de colmillos** (craft: 2 hilos + 3 colmillos): +30 % de velocidad montado. Se ve puesto en el cuello.

### Cría
- Dos leones domesticados + carne → cachorro. La variante se hereda, con 5 % de chance de león blanco.

### Loot y crafteos
- Los leones salvajes dropean **pelaje** (blanco si el león es blanco), **carne**, y a veces un **colmillo** (solo adultos salvajes).
- 2 pelajes → 3 **alfombras de pelaje** (dorada o blanca según el pelaje).

### Advancements
Árbol propio "Sabana": Domador → Jinete / Criador → Rey de la sabana, y el desafío **Copo de nieve** (domesticar un león blanco).

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
