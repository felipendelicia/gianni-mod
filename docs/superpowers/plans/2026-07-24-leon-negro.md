# León Negro — Plan de Implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Cuarta variante `BLACK` del león: macho raro salvaje (3% spawn) que al rugir reproduce uno de dos audios custom elegido al azar por vanilla.

**Architecture:** `LionVariant.BLACK` se suma al enum existente; los checks de "macho" (melena/rugido) pasan a `isMaleVariant()` en el enum. El rugido del negro usa un sound event nuevo `gianni:lion_black_roar` cuyo `sounds.json` lista los dos .ogg — la alternancia aleatoria la hace vanilla. Textura vía rampa negra en el painter v2 existente.

**Tech Stack:** NeoForge 26.2.0.28-beta / MC 26.2, Java 25, ffmpeg (conversión WAV→OGG), Python 3 + Pillow, GameTests data-driven.

**Spec:** `docs/superpowers/specs/2026-07-24-leon-negro-design.md`

## Global Constraints

- MC **26.2** + NeoForge **26.2.0.28-beta**. Cero dependencias externas.
- Ids de `LionVariant` existentes NO cambian (persistidos en mundos): FEMALE=0, MALE=1, WHITE=2. BLACK=3.
- Distribución de spawn: 3% BLACK, 5% WHITE (sin cambio), 40% MALE, resto FEMALE.
- Los WAV originales de `/home/felipe/Downloads/` NO se commitean; solo los .ogg convertidos.
- Renames 26.2 ya conocidos (memoria `mc-26-2-api-renames`); firmas dudosas se verifican contra `build/moddev/artifacts/minecraft-patched-26.2.0.28-beta-sources.jar`.
- Suite completa: `./gradlew runGameTestServer` → "All 14 required tests passed" desde Task 1 (13 existentes + `lion_black_roar`). Cero errores de carga de recursos (sounds.json roto loguea al arrancar).
- Commits sin atribución a Claude.

---

### Task 1: Variante BLACK (enum, checks de macho, spawn, gametest)

**Files:**
- Modify: `src/main/java/com/gianni/entity/LionVariant.java`
- Modify: `src/main/java/com/gianni/entity/LionEntity.java` (líneas ~140 `canRoar`, ~220 spawn)
- Modify: `src/main/java/com/gianni/client/LionRenderer.java` (línea ~28 `s.male`)
- Modify: `src/main/java/com/gianni/ModGameTests.java`
- Create: `src/main/resources/data/gianni/test_instance/lion_black_roar.json`

**Interfaces:**
- Consumes: enum `LionVariant` existente; `roar()`/`isRoaringAnim()` (v0.3 Task 6); patrón de gametest existente.
- Produces: `LionVariant.BLACK` (id=3) y `LionVariant.isMaleVariant()` (public boolean, true para MALE y BLACK). Tasks 2 y 3 dependen de `BLACK`.

- [ ] **Step 1: Gametest que falla**

En `ModGameTests.java`, mismo patrón que los tests existentes:

```java
public static final Supplier<Consumer<GameTestHelper>> LION_BLACK_ROAR =
    TEST_FUNCTIONS.register("lion_black_roar", () -> helper -> {
        LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(2, 2, 2)).spawn();
        lion.setVariant(LionVariant.BLACK);
        if (lion.getVariant() != LionVariant.BLACK) helper.fail("setVariant(BLACK) no persistió");
        if (LionVariant.byId(LionVariant.BLACK.id()) != LionVariant.BLACK) helper.fail("byId no rescata BLACK");
        if (!lion.canRoar()) helper.fail("el león negro adulto debería poder rugir");
        lion.roar();
        if (!lion.isRoaringAnim()) helper.fail("roar() del negro no activó el flag de animación");
        helper.succeed();
    });
```

(`LionVariant` ya está importado en `ModGameTests` si los tests existentes lo usan; si no, agregar `import com.gianni.entity.LionVariant;`.)

`data/gianni/test_instance/lion_black_roar.json` (igual a los existentes):

```json
{
  "type": "minecraft:function",
  "environment": "minecraft:default",
  "function": "gianni:lion_black_roar",
  "structure": "gianni:lion_arena",
  "max_ticks": 100
}
```

- [ ] **Step 2: Verificar que falla**

Run: `./gradlew compileJava`
Expected: FAIL — `LionVariant.BLACK` no existe.

- [ ] **Step 3: Enum + checks de macho + spawn**

`LionVariant.java` — entrada nueva y `isMaleVariant()`:

```java
package com.gianni.entity;

public enum LionVariant {
    FEMALE(0), MALE(1), WHITE(2), BLACK(3);

    private final int id;
    LionVariant(int id) { this.id = id; }
    public int id() { return id; }

    /** Machos con melena que rugen: MALE y BLACK. */
    public boolean isMaleVariant() { return this == MALE || this == BLACK; }

    public static LionVariant byId(int id) {
        for (LionVariant v : values()) if (v.id == id) return v;
        return FEMALE;
    }
}
```

`LionEntity.canRoar()` (línea ~140) — reemplazar el check:

```java
public boolean canRoar() {
    return this.getVariant().isMaleVariant() && !this.isBaby();
}
```

`LionEntity` spawn (línea ~220) — reemplazar la línea del setVariant:

```java
this.setVariant(r < 0.03f ? LionVariant.BLACK : r < 0.08f ? LionVariant.WHITE : r < 0.48f ? LionVariant.MALE : LionVariant.FEMALE);
```

(3% negro; blanco queda en 5% = rango 0.03–0.08; macho 40% = 0.08–0.48; hembra resto. La herencia en `getBreedOffspring` NO se toca: BLACK ya se hereda como cualquier variante.)

`LionRenderer.extractRenderState` (línea ~28) — la melena del render state:

```java
s.male = lion.getVariant().isMaleVariant();
```

(Nota: hasta Task 3 la textura del BLACK cae al `default -> TEX_FEMALE` del switch — esperado y temporal; se ve hembra con melena en el smoke solo si alguien spawnea uno antes de Task 3.)

- [ ] **Step 4: Verde**

Run: `./gradlew runGameTestServer`
Expected: "All 14 required tests passed" (13 previos + `lion_black_roar`).

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "León negro: variante BLACK con melena y rugido, spawn 3%"
```

---

### Task 2: Audios custom + sound event del rugido negro

**Files:**
- Create: `src/main/resources/assets/gianni/sounds/opinegro.ogg` (generado por ffmpeg)
- Create: `src/main/resources/assets/gianni/sounds/opiniones.ogg` (generado por ffmpeg)
- Modify: `src/main/resources/assets/gianni/sounds.json`
- Modify: `src/main/java/com/gianni/ModSounds.java`
- Modify: `src/main/java/com/gianni/entity/LionEntity.java` (línea ~145, `roar()`)
- Modify: `src/main/resources/assets/gianni/lang/es_ar.json`, `en_us.json`

**Interfaces:**
- Consumes: `LionVariant.BLACK` (Task 1); `ModSounds.register(...)` existente; `roar()` existente.
- Produces: `ModSounds.LION_BLACK_ROAR` (`DeferredHolder<SoundEvent, SoundEvent>`). Nadie más depende de esto.

- [ ] **Step 1: Convertir los audios (mono 44.1kHz, volumen normalizado)**

```bash
ffmpeg -y -i /home/felipe/Downloads/Opinegro.wav -af loudnorm=I=-16:TP=-1.5:LRA=11 -ac 1 -ar 44100 -c:a libvorbis -qscale:a 5 src/main/resources/assets/gianni/sounds/opinegro.ogg
ffmpeg -y -i /home/felipe/Downloads/Opiniones.wav -af loudnorm=I=-16:TP=-1.5:LRA=11 -ac 1 -ar 44100 -c:a libvorbis -qscale:a 5 src/main/resources/assets/gianni/sounds/opiniones.ogg
```

Verificar: `ffprobe -v error -show_entries stream=codec_name,channels,sample_rate -of default=noprint_wrappers=1 src/main/resources/assets/gianni/sounds/opinegro.ogg` → `codec_name=vorbis`, `channels=1`, `sample_rate=44100` (ídem opiniones).

- [ ] **Step 2: sounds.json + registro + lang**

En `sounds.json`, agregar la entrada (los dos archivos = vanilla elige al azar por rugido):

```json
"lion_black_roar": {
  "sounds": [ "gianni:opinegro", "gianni:opiniones" ],
  "subtitle": "entity.gianni.lion_black_roar"
}
```

En `ModSounds.java`, junto a los registros existentes:

```java
public static final DeferredHolder<SoundEvent, SoundEvent> LION_BLACK_ROAR = register("lion_black_roar");
```

Lang es_ar: `"entity.gianni.lion_black_roar": "Un león negro opina"`. Lang en_us: `"entity.gianni.lion_black_roar": "A black lion opines"`.

- [ ] **Step 3: Sonido en `roar()`**

`LionEntity.roar()` línea ~145 — reemplazar el playSound:

```java
this.playSound((this.getVariant() == LionVariant.BLACK ? ModSounds.LION_BLACK_ROAR : ModSounds.LION_ROAR).get(), 2.0f, 1.0f);
```

(El resto de `roar()` — flag, partículas, pánico — no se toca.)

- [ ] **Step 4: Verde + log limpio**

Run: `./gradlew runGameTestServer 2>&1 | grep -iE "sound|error" | head`
Expected: cero errores de sounds.json / archivos de sonido faltantes. Suite "All 14 required tests passed".

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "Rugido del león negro: audios Opinegro/Opiniones alternados por vanilla"
```

---

### Task 3: Textura negra (painter + renderer)

**Files:**
- Modify: `tools/gen_textures.py` (entrada `lion_black` en RAMPS)
- Create (generado): `src/main/resources/assets/gianni/textures/entity/lion/lion_black.png` (128x128)
- Modify: `src/main/java/com/gianni/client/LionRenderer.java` (TEX_BLACK + switch)

**Interfaces:**
- Consumes: `LionVariant.BLACK` (Task 1); painter v2 y dict `RAMPS` existentes (v0.3 Task 8); `TEX_*`/switch de textura existentes en `LionRenderer`.
- Produces: nada para otras tasks.

- [ ] **Step 1: Rampa negra en el painter**

En `tools/gen_textures.py`, agregar al dict `RAMPS` (después de `"lion_white"`):

```python
"lion_black":  {"fur": ramp((52, 48, 46)), "mane": ramp((22, 20, 20)), "belly": ramp((84, 80, 76))},
```

(Pelaje gris muy oscuro — no negro puro, para que el sombreado y los strokes se lean; melena casi negra: la silueta melena-vs-pelaje se mantiene. Ojos ámbar y nariz rosada ya contrastan solos. `paint_lion` y el preview iteran `RAMPS`, así que `lion_black.png` y su columna en `preview_leones.png` salen sin más cambios.)

Run: `python3 tools/gen_textures.py`
Verificar: `python3 -c "from PIL import Image; im=Image.open('src/main/resources/assets/gianni/textures/entity/lion/lion_black.png'); print(im.size, im.mode)"` → `(128, 128) RGBA`.

- [ ] **Step 2: Renderer**

`LionRenderer.java` — constante nueva junto a las TEX_* (línea ~13):

```java
private static final Identifier TEX_BLACK = GianniMod.id("textures/entity/lion/lion_black.png");
```

Y en el switch de `extractRenderState` (línea ~38), antes del default:

```java
case BLACK -> TEX_BLACK;
```

- [ ] **Step 3: Verde + commit**

Run: `./gradlew runGameTestServer`
Expected: "All 14 required tests passed".

```bash
git add -A && git commit -m "Textura del león negro: rampa oscura en el painter y mapeo en el renderer"
```
