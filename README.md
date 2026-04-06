# 🎲 Lord of the Dices
### CS102 - Spring 2025/2026 | Section 1G | StackOver5

A 2D desktop game built with **LibGDX** combining a side-scrolling exploration world with a turn-based card and dice battle system. Players navigate through six levels, each culminating in a unique boss fight themed after one of the Seven Deadly Sins.

---

## 👥 Group Members

| Name | Surname | GitHub | Responsibility |
|------|---------|--------|----------------|
| Behiç Eren | Köpüklü | [@Behicerenkopuklu](https://github.com/furkanerden27/CS-102-Project/commits?author=Behicerenkopuklu) | Battle system, fight logic & card effects |
| Bilgehan | Erdemir | [@Asasi1453](https://github.com/furkanerden27/CS-102-Project/commits?author=Asasi1453) | UI/UX, screen navigation & Firebase integration |
| Emre | Baktır | [@EmreBaktir](https://github.com/furkanerden27/CS-102-Project/commits?author=EmreBaktir) | Inventory, shop mechanics & merchant entity |
| Furkan | Erden | [@furkanerden27](https://github.com/furkanerden27/CS-102-Project/commits?author=furkanerden27) | Entity system, maps & play screen |
| Ömer | Şen | [@0shen6](https://github.com/furkanerden27/CS-102-Project/commits?author=0shen6) | Item/card system & relic management |

---

## 🎮 Game Description

**Lord of the Dices** is split into two gameplay modes:

- **Roaming World:** A 2D side-scrolling map where the player can walk, jump, talk to merchants, and trigger enemy encounters.
- **Battle Screen:** A tactical turn-based fight system where the player plays cards from their deck and rolls dice to calculate damage against enemies and bosses.

Players collect **gold**, **cards**, **dice**, and **relics** throughout the game to build a stronger loadout before confronting each Deadly Sin boss.

---

## 🏗️ Architecture

The project follows the **Model-View-Controller (MVC)** design pattern:

- **Model** — Core game logic including the `Entity` hierarchy, `Item` classes (cards, dice, relics), and `FightManager` logic.
- **View** — Rendering handled by `PlayScreen`, `BattleScreen`, `MainMenuScreen`, and 12+ other screens.
- **Controller** — `ScreenManager` centralizes all screen transitions and state changes; `InputProcessor` handles keyboard/mouse input.

---

## 📁 Project Structure

```
core/src/main/java/com/mygdx/LordOfTheDices/
├── entities/
│   ├── Entity.java          # Abstract base: health, animations, movement
│   ├── Player.java          # Player entity with inventory
│   ├── Mob.java             # Abstract enemy base
│   ├── BasicMob.java        # Standard enemy logic
│   └── bosses/
│       ├── Boss.java        # Abstract boss class
│       ├── Gluttony.java
│       ├── Lust.java
│       ├── Pride.java
│       ├── Sloth.java
│       ├── Wrath.java
│       └── Envy.java
├── items/
│   ├── Item.java            # Abstract base item class
│   ├── Card.java            # Combat cards with effect types
│   ├── Dice.java            # Dice with roll() logic
│   └── Relic.java           # Passive upgrade items
├── battle/
│   ├── FightManager.java    # Turn-based combat loop and logic
│   ├── Effect.java          # Abstract status effect
│   ├── Poison.java
│   ├── Stun.java
│   ├── Bleeding.java
│   ├── Weaken.java
│   ├── Strengthen.java
│   └── Lure.java
├── screens/
│   ├── ScreenManager.java   # Central screen state controller
│   ├── MainMenuScreen.java
│   ├── PlayScreen.java
│   ├── BattleScreen.java
│   ├── MerchantScreen.java
│   ├── InventoryScreen.java
│   ├── PauseScreen.java
│   ├── OptionsScreen.java
│   ├── MenuNewSaveScreen.java
│   ├── MenuLoadSaveScreen.java
│   ├── StoryScreenBeginning.java
│   └── StoryScreenEnd.java
├── shop/
│   ├── Shop.java            # Buy/sell logic for cards and relics
│   └── Inventory.java       # Player inventory management
├── utils/
│   └── FloatingText.java    # Floating damage/heal text display
└── Core.java                # LibGDX entry point
```

---

## Development:
  ### Week 1:
  - Meeting: general game plan & basic role distribution 
  - Determining the libraries that will be used
  - Project created, Core, Dice and PlayScreen classes added

  ### Week 2:
  - Meeting: discussion about the game relics/physics 
  - Entity class added, extended from Sprite
  - Player class implemented, extending Entity
  - Damage and heal system added
  - Player, Entity and PlayScreen updated to work with current assets
  - Main character sprites and background assets uploaded

  ### Week 3:
  - Meeting: boss properties finialized in design 
  - Mob, Boss and BasicMob classes added
  - All six Deadly Sin boss classes added: Gluttony, Lust, Pride, Sloth, Wrath, Envy
  - Player animations added, input mechanic revised
  - All status effect classes added: Poison, Stun, Bleeding, Weaken, Strengthen, Lure
  - Effects ArrayList added to Entity, damageModifier added to Player, effectiveDamage added to Mob
  - FightManager and BattleScreen classes created
  - FightManager modifications made

  ### Week 4:
  - Meeting: screens are discussed
  - Mob spawn positions added to the map
  - Entity atlas created, animations redesigned for the new atlas
  - FloatingText class added for damage display
  - Getting-damage animation handled
  - Package name changed to com.mygdx.LordOfTheDices
  - Item and Card base classes added
  - Multiple screens added, AssetManager introduced for centralized asset handling
  - PlayScreen changes applied
  - BattleScreen improvements made, FightManager further developed

  ### Week 5:
  - No in-person meeting
  - Lust and BasicMob special attacks implemented
  - Special effects for all bosses implemented
  - Gold drop system added to PlayScreen
  - Map 2 added
  - All item class implementations and general item properties added
  - Shop mechanic implemented
  - Shop assets and sounds added
  - InventoryScreen created with assets
  - Inventory.java updated
  - Remaining screens added, AssetManager refined
  - Firebase integration completed: Inventory can now be saved and loaded

  ### Week 6:
  - Meeting: games merged and optimization methods are discussed
  - Map layout redesigned, all maps completed
  - Merchant entity added, interaction layers improved
  - Card effects and asset management implemented
  - Relic assets added, relic appliance on player implemented
  - Entity sprites displayed on BattleScreen
  - FightManager made sufficient for full fight flow
  - Level class and PauseScreen created
  - Bosses placed into maps
  - Assets reclassified, Firebase structure updated
  - Game optimized, boss sequence finalized
  - Inventory.java and InventoryScreen.java updated
  - Shop and merchant integration completed
  - Merge conflicts resolved, branches integrated


---