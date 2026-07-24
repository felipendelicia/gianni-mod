# Descanso Alternado + Pose Esfinge + Rugido Periódico — Plan de Implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Los leones salvajes alternan descanso (pose esfinge nueva) y caminata en vez de estar siempre tirados, y los machos rugen periódicamente (60-120 s) en vez de solo al anochecer.

**Architecture:** `LionRestGoal` gana duración y cooldown basados en `tickCount` (sin campos nuevos en la entidad); el rugido unifica anochecer+combate en un solo timer `roarCooldown` que se resortea tras cada rugido; la pose `resting` se separa de `sitting` en `LionModel.setupAnim` con patas `_lower` poseadas (esfinge).

**Tech Stack:** NeoForge 26.2.0.28-beta / MC 26.2, Java 25, GameTests data-driven.

**Spec:** `docs/superpowers/specs/2026-07-24-descanso-rugido-design.md`

## Global Constraints

- MC **26.2** + NeoForge **26.2.0.28-beta**. Cero dependencias externas.
- Alternancia: descanso **300–800 ticks**, cooldown **200–600 ticks**. Rugido: resorteo **1200–2400 ticks** tras cada rugido, init aleatoria en el mismo rango.
- `sitting` NO cambia (pose ni mecánica). Condiciones de `canUse` del rest existentes NO cambian (solo se les suma el cooldown).
- Firmas dudosas → `build/moddev/artifacts/minecraft-patched-26.2.0.28-beta-sources.jar`.
- Suite completa: `./gradlew runGameTestServer` → "All 15 required tests passed" desde Task 1 (14 actuales + `lion_rest`).
- Commits sin atribución a Claude.

---

### Task 1: Alternancia descanso/caminata + gametest `lion_rest`

**Files:**
- Modify: `src/main/java/com/gianni/entity/goal/LionRestGoal.java`
- Modify: `src/main/java/com/gianni/ModGameTests.java`
- Create: `src/main/resources/data/gianni/test_instance/lion_rest.json`

**Interfaces:**
- Consumes: `LionEntity.isResting()`/`setResting(boolean)`, `isDaytime()`, `tickCount` (público en `Entity`), `getRandom()`.
- Produces: nada para otras tasks (comportamiento puro).

- [ ] **Step 1: Gametest que falla**

En `ModGameTests.java` (patrón `runAfterDelay` ya usado en `lion_catnip`):

```java
public static final Supplier<Consumer<GameTestHelper>> LION_REST =
    TEST_FUNCTIONS.register("lion_rest", () -> helper -> {
        helper.getLevel().setDayTime(1000);   // asegura día; ningún otro test depende de la hora
        LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(2, 2, 2)).spawn();
        lion.setVariant(LionVariant.FEMALE);
        helper.runAfterDelay(10, () -> {
            if (!lion.isResting()) helper.fail("león salvaje adulto de día no descansa");
            helper.succeed();
        });
    });
```

`data/gianni/test_instance/lion_rest.json`:

```json
{
  "type": "minecraft:function",
  "environment": "minecraft:default",
  "function": "gianni:lion_rest",
  "structure": "gianni:lion_arena",
  "max_ticks": 100
}
```

`// ADAPT:` verificar la firma de `setDayTime` en `ServerLevel` (fuentes 26.2) antes de compilar; si el nombre difiere, usar el equivalente.

- [ ] **Step 2: Verificar estado**

Run: `./gradlew runGameTestServer`
Expected: el test nuevo debería PASAR ya (el rest actual engancha siempre) — es un test de regresión que debe SEGUIR verde tras el cambio de alternancia. Confirmar "All 15 required tests passed". Si falla acá, investigar antes de seguir (¿hora del server?, ¿prioridades?).

- [ ] **Step 3: Implementar alternancia**

Reemplazo completo de `LionRestGoal.java`:

```java
package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LionRestGoal extends Goal {
    private final LionEntity lion;
    private long restUntil;        // tickCount hasta el que descansa
    private long cooldownUntil;    // tickCount hasta el que no puede volver a descansar

    public LionRestGoal(LionEntity lion) {
        this.lion = lion;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    private boolean conditions() {
        return !lion.isTame() && !lion.isBaby() && lion.isDaytime() && lion.getTarget() == null && !lion.isInLove();
    }

    @Override
    public boolean canUse() {
        return lion.tickCount >= this.cooldownUntil && conditions();
    }

    @Override
    public boolean canContinueToUse() {
        return lion.tickCount < this.restUntil && conditions();
    }

    @Override
    public void start() {
        this.restUntil = lion.tickCount + 300 + lion.getRandom().nextInt(501);      // 15-40 s
        lion.getNavigation().stop();
        lion.setResting(true);
    }

    @Override
    public void stop() {
        this.cooldownUntil = lion.tickCount + 200 + lion.getRandom().nextInt(401);  // 10-30 s
        lion.setResting(false);
    }
}
```

Nota: se usa `tickCount` y no contadores decrementados porque `canUse` de goals no corriendo puede evaluarse con frecuencia variable según el selector; los timestamps son inmunes a eso.

- [ ] **Step 4: Verde**

Run: `./gradlew runGameTestServer`
Expected: "All 15 required tests passed" (el `lion_rest` sigue verde: el primer descanso arranca sin cooldown).

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "Descanso alternado: los leones ciclan entre echarse y pasear"
```

---

### Task 2: Rugido periódico unificado

**Files:**
- Modify: `src/main/java/com/gianni/entity/LionEntity.java` (bloque de rugido en `aiStep` ~líneas 169-180, campo `roaredTonight`, `roar()` ~línea 143, constructor)

**Interfaces:**
- Consumes: `canRoar()`, `isTripping()`, `roar()`, `roarCooldown` existentes.
- Produces: nada para otras tasks.

- [ ] **Step 1: Implementar**

1. Eliminar el campo `roaredTonight` (y todo uso).
2. En el constructor de `LionEntity` (después del super), inicializar:

```java
this.roarCooldown = 1200 + this.random.nextInt(1201);   // evita coros al cargar la manada
```

`// ADAPT:` si `this.random` no está disponible en ese punto del constructor, usar `this.getRandom()`.

3. Reemplazar el bloque de rugido de `aiStep` (hoy: decremento + lógica de `night`/`roaredTonight` + combate) por:

```java
if (!this.level().isClientSide() && this.canRoar() && !this.isTripping()) {
    if (this.roarCooldown > 0) this.roarCooldown--;
    if (this.roarCooldown == 0) {
        this.roar();   // periódico; en combate simplemente coincide con target a la vista
    }
}
```

4. En `roar()`, asegurar el resorteo del timer (reemplazar el valor actual si ya setea `roarCooldown`, agregarlo si no):

```java
this.roarCooldown = 1200 + this.random.nextInt(1201);   // 60-120 s
```

`// ADAPT:` verificar qué hace `roar()` hoy con `roarCooldown` (el rugido de combate se apoyaba en él) y dejar UN solo punto de resorteo, dentro de `roar()`.

- [ ] **Step 2: Verde (los tests de rugido existentes cubren `roar()`)**

Run: `./gradlew runGameTestServer`
Expected: "All 15 required tests passed" — `lion_roar_panic` y `lion_black_roar` llaman `roar()` directo y no dependen del trigger; `lion_catnip` verifica que el trip suprime el rugido (el gate `!isTripping()` se mantiene).

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "Rugido periódico: un solo timer de 60-120s en vez del caso de anochecer"
```

---

### Task 3: Pose esfinge + docs

**Files:**
- Modify: `src/main/java/com/gianni/client/LionModel.java` (campos `_lower` + bloque `resting` en `setupAnim`)
- Modify: `README.md`
- Modify: `docs/superpowers/manual-test-checklist.md`

**Interfaces:**
- Consumes: partes `right_front_lower`/`left_front_lower`/`right_hind_lower`/`left_hind_lower` (hijas de las `_upper`, definidas en `addLeg`); flags `s.sitting`/`s.resting` del render state.
- Produces: nada para otras tasks.

- [ ] **Step 1: Campos de patas inferiores**

En el constructor de `LionModel`, después de las `_upper` existentes:

```java
this.rightFrontLower = this.rightFrontUpper.getChild("right_front_lower");
this.leftFrontLower = this.leftFrontUpper.getChild("left_front_lower");
this.rightHindLower = this.rightHindUpper.getChild("right_hind_lower");
this.leftHindLower = this.leftHindUpper.getChild("left_hind_lower");
```

(con sus `private final ModelPart` correspondientes).

- [ ] **Step 2: Separar `resting` de `sitting` en `setupAnim`**

El bloque actual es `if (s.sitting || s.resting) { ... }` con un `if (s.resting)` interno para la cabeza. Reemplazarlo por DOS bloques:

```java
if (s.sitting && !s.resting) {
    // (bloque actual sin el if(s.resting) interno — pose sentada intacta)
    this.bodyFront.y += 5.0f;
    this.bodyBack.y += 5.0f;
    this.belly.y += 5.0f;
    this.head.y += 5.0f;
    this.rightFrontUpper.xRot = -Mth.HALF_PI;
    this.leftFrontUpper.xRot = -Mth.HALF_PI;
    this.rightHindUpper.xRot = -Mth.HALF_PI;
    this.leftHindUpper.xRot = -Mth.HALF_PI;
    this.rightFrontUpper.y += 5.0f;
    this.leftFrontUpper.y += 5.0f;
    this.rightHindUpper.y += 5.0f;
    this.leftHindUpper.y += 5.0f;
}

if (s.resting) {                          // esfinge: panza al piso, patas delanteras al frente
    this.bodyFront.y += 7.0f;
    this.bodyBack.y += 7.0f;
    this.belly.y += 6.5f;
    this.head.y += 6.0f;
    this.head.xRot += 0.1f + Mth.sin(s.ageInTicks * 0.03f) * 0.06f;   // cabeceo lento
    // delanteras: húmero vertical apoyado, antebrazo plano hacia adelante
    this.rightFrontUpper.xRot = 0.0f;
    this.leftFrontUpper.xRot = 0.0f;
    this.rightFrontUpper.y += 8.0f;
    this.leftFrontUpper.y += 8.0f;
    this.rightFrontLower.xRot = -Mth.HALF_PI;
    this.leftFrontLower.xRot = -Mth.HALF_PI;
    // traseras recogidas: muslo horizontal, canilla vertical
    this.rightHindUpper.xRot = -Mth.HALF_PI;
    this.leftHindUpper.xRot = -Mth.HALF_PI;
    this.rightHindUpper.y += 6.0f;
    this.leftHindUpper.y += 6.0f;
    this.rightHindLower.xRot = Mth.HALF_PI;
    this.leftHindLower.xRot = Mth.HALF_PI;
}
```

Valores iniciales razonados sobre la geometría (piso del modelo en y=24: upper delantero a pivot 18 llega justo al piso; antebrazo plano adelante a nivel piso; muslo horizontal a 16 con canilla vertical llegando a 24). Son AJUSTABLES tras el smoke — si Felipe pide retoques, se iteran estos números, no la estructura. Nota: `setupAnim` resetea poses en 26.2, así que las asignaciones absolutas de xRot sobre las `_lower` (que la caminata no toca) son seguras.

- [ ] **Step 3: Docs**

README: donde diga rugido "al anochecer", cambiar a "periódicamente (~1-2 min, y al entrar en combate)"; agregar a la sección de comportamiento una línea sobre la alternancia descanso/caminata con pose echada tipo esfinge.

`manual-test-checklist.md`: en regresión v0.2.0 reemplazar "Rugido al anochecer (machos)" por "Rugido periódico (~1-2 min, machos y negro; también al entrar en combate)". En la sección de gráficos, reemplazar el ítem de "descanso (cabeza apoyada)" por: `- [ ] Descanso: pose esfinge (panza al piso, patas delanteras al frente), alterna con caminatas (~15-40 s echado, ~10-30 s paseando).`

- [ ] **Step 4: Verde + commit**

Run: `./gradlew runGameTestServer`
Expected: "All 15 required tests passed" (modelo es client-only).

```bash
git add -A && git commit -m "Pose esfinge para el descanso y docs de alternancia y rugido periódico"
```
