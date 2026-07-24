# Gianni Mod

Leones para Minecraft Java 26.2 (NeoForge).

- Spawnean en sabanas en manadas (macho/hembra/blanco raro).
- De día descansan; de noche cazan (y te cazan).
- Domesticables con carne cruda (cachorros siempre; adultos solo descansando de día).
- Montables con saddle (WASD + espacio para saltar).
- Criables: la variante se hereda; 5% de león blanco.

## Desarrollo

Requisitos: JDK 25 (el toolchain lo baja solo) y nada más.

    ./gradlew runClient        # jugar con el mod
    ./gradlew runGameTestServer  # tests
    ./gradlew build            # jar en build/libs/

Texturas: `tools/gen_textures.py` (Python 3 + Pillow) regenera los PNG.
Checklist de pruebas manuales: docs/superpowers/manual-test-checklist.md
