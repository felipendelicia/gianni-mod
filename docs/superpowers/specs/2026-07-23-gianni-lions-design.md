# Gianni Mod — Leones (diseño)

**Fecha:** 2026-07-23
**Target:** Minecraft Java 26.2 · NeoForge beta para 26.2 · Java 25
**Alcance:** un mob nuevo (león) con domesticación, montura, caza hostil, cría y variantes.

## Objetivo

Agregar leones a Minecraft con interacciones ricas: domesticar, montar, comportamiento hostil/de caza, y cría con variantes. Todo el arte (modelo y texturas) se genera en código/PNG estilo vanilla; se podrá reemplazar después.

## Gameplay

### Spawn
- Bioma: sabanas (todas las variantes de sabana), en manadas de 2–4.
- Huevo de spawn en el menú creativo.

### Variantes
- Macho (melena), hembra, león blanco raro (~5% al spawnear/nacer).
- Cachorros de cada variante (misma textura, escala reducida).

### Ciclo día/noche
- Día: descansan (pose echada, no inician ataques al jugador salvo que se los ataque).
- Noche: cazan activamente y son agresivos con jugadores cercanos.

### Hostilidad y caza
- León salvaje ataca al jugador dentro de un radio de agresión (solo de noche; de día solo si es provocado).
- Caza presas: vacas, ovejas, cerdos, gallinas y conejos.

### Domesticación
- Ítem: cualquier carne cruda. Probabilidad 1/3 por intento (estilo lobo con huesos).
- Cachorros: domesticables siempre.
- Adultos: solo mientras descansan de día (no agresivos).
- Domesticado: sigue al dueño, se sienta a la orden (clic con mano vacía), defiende al dueño, no ataca animales de granja.

### Montura
- Requiere: león domesticado adulto + montura (saddle).
- Control con WASD, velocidad tipo caballo medio, salto potenciado.

### Cría
- Dos leones domesticados alimentados con carne cruda → cachorro.
- El cachorro hereda variante de un padre al azar; chance baja de blanco.

### Sonidos y drops
- Sonidos: se reutilizan eventos de sonido vanilla (oso polar/gato) por ahora.
- Drops: solo XP. Extensible después.

## Técnica

### Stack
- NeoForge beta para MC 26.2, ModDevGradle, Gradle wrapper (9.1+), Java 25 (instalar `openjdk-25-jdk` por apt; Gradle toolchain como respaldo).
- Mod id `gianni`, nombre "Gianni Mod", paquete `com.gianni`.
- Repo git en `gianni-mod/`, .gitignore de Gradle.

### Servidor (lógica)
- `LionEntity` extiende `TamableAnimal`; montura vía lógica de saddle + control del jugador (`getControllingPassenger`, `tickRidden`).
- Variante como `EntityDataAccessor` sincronizado + persistido en NBT.
- Goals de IA (prioridad aprox.): flotar en agua, sentado, ataque cuerpo a cuerpo, seguir al dueño, cazar presas (solo de noche, solo salvaje), descansar de día (salvaje), pasear, mirar al jugador.
- Targets: defender/atacar con dueño; salvaje: jugador cercano de noche, presas de noche.

### Cliente (render)
- `LionModel`: modelo en código con `ModelPart`s (cuerpo, cabeza, 4 patas, cola; melena como pieza visible solo en macho). Animaciones por código: caminar, sentado, echado, ataque.
- `LionRenderer`: textura según variante (macho/hembra/blanco). Cachorro = escala 0.5.
- Texturas PNG 64×64 generadas (3 archivos).

### Datos y recursos
- `neoforge.mods.toml`, lang `en_us` + `es_ar`, biome modifier JSON para spawn en `#c:is_savanna` (o tag equivalente en 26.2), spawn egg registrado, loot table vacía.

### Manejo de errores / bordes
- Montar sin domesticar: no permite; muestra el "no" visual estándar (partículas de humo).
- Desmontar en agua/aire: comportamiento vanilla de dismount.
- Chunk unload / recarga: variante y estado de domesticación persisten en NBT.
- León domesticado no despawnea (persistencia al domesticar).

### Testing
- Manual: `runClient`, mundo creativo con sabana.
- Automatizado: GameTests para domesticación y herencia de variante, si la API de GameTest en 26.2/NeoForge beta lo permite sin fricción desproporcionada; si no, se documenta prueba manual guiada.

## Fuera de alcance (por ahora)
- Sonidos propios, drops especiales, GeckoLib/animaciones avanzadas, otros animales (cebras, etc.), compatibilidad multiloader.
