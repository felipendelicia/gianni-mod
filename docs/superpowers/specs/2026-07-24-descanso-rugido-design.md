# Descanso alternado, pose esfinge y rugido periódico — Diseño

**Fecha:** 2026-07-24 · **Aprobado por:** Felipe (diseño presentado en sesión tras smoke de v0.3.0)

## Problema

1. Los leones salvajes adultos descansan TODO el día sin cortes (`LionRestGoal.canUse ==
   canContinueToUse`, sin timer): la manada entera queda tirada, estática.
2. La pose `resting` reusa la de `sitting` (solo cambia la cabeza): "acostado" se ve como
   un perro sentado con la cabeza gacha, no como un león echado.
3. El rugido solo ocurre una vez al anochecer (`roaredTonight`) o en combate: los audios
   del león negro casi no se escuchan.

## Cambios

### 1. Pose esfinge para `resting` (LionModel.setupAnim)

- `resting` deja de compartir bloque con `sitting`. `sitting` queda EXACTAMENTE como está.
- Pose esfinge: cuerpo bajado a nivel del piso (panza apoyada), patas delanteras dobladas
  hacia adelante (upper ~horizontal, lower plegada debajo — requiere posar las `_lower`,
  que hoy no tienen campos en el modelo), patas traseras recogidas, cabeza erguida con
  cabeceo lento sutil (sin ápice de animación brusca).
- Valores exactos de rotación/offset se ajustan viendo el resultado in-game (criterio
  ya usado con el rolling del catnip).

### 2. Alternancia descanso/caminata (LionRestGoal)

- `start()` sortea duración de descanso: **300–800 ticks (15–40 s)**.
- `canContinueToUse()` exige además que la duración no haya expirado.
- `stop()` inicia cooldown: **200–600 ticks (10–30 s)** durante el cual `canUse()` es
  false → `WaterAvoidingRandomStrollGoal` (prio 7) toma el control y el león camina.
- Las condiciones existentes (salvaje, adulto, de día, sin target, sin amor) no cambian.
- Domesticación sin cambios: sigue requiriendo `isResting()`; con la alternancia hay
  ventanas de descanso en vez de disponibilidad permanente.

### 3. Rugido periódico unificado (LionEntity.aiStep / roar())

- Se elimina `roaredTonight` y el caso especial de anochecer.
- UN solo timer (`roarCooldown`): decrementa cada tick (machos/negro adultos, no en
  viaje de catnip — gate existente); al llegar a 0 el león ruge y el timer se resortea.
- Resorteo tras cada rugido: **1200–2400 ticks (60–120 s)**.
- Rugido de combate: sin cambios de mecánica (si hay target vivo y cooldown en 0, ruge);
  al rugir resetea el mismo timer — nunca hay spam.
- Inicialización: cada león arranca con un valor aleatorio del rango (evita coros
  sincronizados al cargar un chunk con manada).
- El león negro reproduce sus audios (alternados por vanilla) en cada rugido periódico.

### 4. Docs

- README y `manual-test-checklist.md`: "rugido al anochecer" → "rugido periódico
  (~1-2 min, también en combate)"; agregar item de alternancia descanso/caminata y
  pose esfinge.

## Tests

- Gametest nuevo `lion_rest`: león salvaje adulto de día sin target → `isResting()`
  true dentro de los primeros ticks (regresión de que el descanso engancha).
- Cadencia de alternancia y período de rugido: verificación in-game (timers de minutos
  no entran en un gametest sano). Suite completa (15 con el nuevo) debe quedar verde.

## Fuera de alcance (YAGNI)

Cambios a `sitting`, rugido de hembras, sonidos nuevos, animación de transición
parado↔echado.
