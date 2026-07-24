# León negro — Diseño

**Fecha:** 2026-07-24 · **Aprobado por:** Felipe (diseño presentado en sesión, "Dale! avanza")

## Objetivo

Cuarta variante del león: el **león negro**, macho raro salvaje que al rugir reproduce
uno de dos audios provistos por Felipe (`Opinegro.wav` / `Opiniones.wav`), elegido al azar.

## Decisión de arquitectura

**`LionVariant.BLACK` como cuarta entrada del enum existente** (FEMALE/MALE/WHITE/BLACK).

Alternativas rechazadas:
- Entidad separada: duplica IA, domesticación, cría y renderer sin ganar nada.
- Flag NBT suelto: no encaja con el sistema de variantes que ya resuelve textura,
  melena y advancement del blanco.

## Componentes

### 1. Variante
- `LionVariant.BLACK` con id nuevo (siguiente entero libre; los ids existentes NO cambian
  — están persistidos en mundos guardados).
- El check `getVariant() == LionVariant.MALE` para melena/rugido/rol de cría pasa a un
  método del enum `isMaleVariant()` (true para MALE y BLACK). Un solo lugar decide.
- `LionEntity.isMale()` (o equivalente actual) delega en ese método.

### 2. Spawn y cría
- Distribución natural: **3% BLACK, 5% WHITE, 40% MALE, resto FEMALE** (blanco no cambia).
- Cría: BLACK se hereda con la misma mecánica de herencia de variante existente; la
  mutación especial existente (blanco) no cambia.

### 3. Rugido
- Sound event nuevo `gianni:lion_black_roar` en `sounds.json` con DOS archivos
  (`gianni:opinegro`, `gianni:opiniones`) — la elección aleatoria por rugido la hace
  vanilla, sin código custom.
- En `LionEntity.roar()`: si `getVariant() == BLACK`, suena `lion_black_roar`; si no,
  el sonido de rugido actual. Todo lo demás del rugido (pánico de presas, partículas,
  flag de animación) queda igual.
- Subtítulo (lang): es_ar "Un león negro opina" / en_us "A black lion opines".

### 4. Audios
- Fuente: `/home/felipe/Downloads/Opinegro.wav` (1.18s, 48kHz stereo) y
  `/home/felipe/Downloads/Opiniones.wav` (0.93s, 44.1kHz stereo).
- Pipeline: convertir a **OGG Vorbis mono 44.1kHz** con volumen normalizado parejo
  (ffmpeg, loudnorm o ajuste de gain equivalente), a
  `src/main/resources/assets/gianni/sounds/opinegro.ogg` y `opiniones.ogg`.
- Los WAV originales NO se commitean.

### 5. Textura
- Rampa negra en el painter v2 de `tools/gen_textures.py`: pelaje gris oscuro/negro,
  melena aún más oscura (la silueta debe leerse contra el pelaje).
- Genera `assets/gianni/textures/entity/lion/lion_black.png` (128x128) como las otras 3.
- `LionRenderer` mapea BLACK → esa textura (mismo mecanismo que WHITE).
- Ojos emisivos compartidos (`lion_eyes.png` sirve para todas las variantes).

### 6. Tests
- Gametest `lion_black_roar`: spawn de león, `setVariant(BLACK)`, `roar()` →
  `isRoaringAnim()` true y sin crash (valida sound event registrado y variante entera).
- La distribución de spawn no se testea (aleatoria); la herencia usa la mecánica ya
  testeada.
- Suite completa debe quedar verde (13 existentes + el nuevo).

## Fuera de alcance (YAGNI)

Advancement propio del negro, drops especiales, comportamiento distinto al de un macho
normal, spawn egg diferenciado. Se agregan después si Felipe los pide.
