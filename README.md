# Gianni Mod

Leones para Minecraft Java 26.2 (NeoForge).

- Spawnean en sabanas en manadas (macho/hembra/blanco raro).
- De día descansan; de noche cazan (y te cazan).
- Domesticables con carne cruda (cachorros siempre; adultos solo descansando de día).
- Montables con saddle (WASD + espacio para saltar).
- Criables: la variante se hereda; 5% de león blanco.
- Rugido del macho al anochecer y al cazar (las presas huyen).
- Cachorros que juegan; acecho agachado antes de atacar.
- Loot: pelajes (dorado/blanco), colmillos, carne. Alfombras y collar craftables.
- Collar de colmillos: +30% de velocidad montado.
- Advancements propios y guaridas de leones en las sabanas.

## Desarrollo

Requisitos: JDK 25 (el toolchain lo baja solo) y nada más.

    ./gradlew runClient        # jugar con el mod
    ./gradlew runGameTestServer  # tests
    ./gradlew build            # jar en build/libs/

Texturas: `tools/gen_textures.py` (Python 3 + Pillow) regenera los PNG.
Checklist de pruebas manuales: docs/superpowers/manual-test-checklist.md
