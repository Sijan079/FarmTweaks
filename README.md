
## FarmTweaks

FarmTweaks is a NeoForge mod for Minecraft **1.21.1** that adds a handful of quality-of-life farming improvements: faster harvesting and tilling, optional Fortune-style crop bonuses, small XP rewards, and a Seed Bag for planting workflows.

## Implementation progress

**Current feature set: 9 / 9 implemented — 100%**

`[████████████████████] 9 / 9`

This tracker covers the player-facing features currently shipped by FarmTweaks. Items in the [roadmap](#roadmap) are deliberately excluded until they are implemented.

| Status | Feature |
| --- | --- |
| ✅ | Right-click harvest with auto-replant |
| ✅ | Fortune-affected crop drops |
| ✅ | AoE harvest |
| ✅ | AoE tilling |
| ✅ | Crop XP rewards |
| ✅ | Serene Seasons compatibility |
| ✅ | Seed Bag |
| ✅ | Tool tag tweaks |
| ✅ | Custom water hydration range |

## Implemented features

### ✅ Right-click Harvest (with auto-replant)
- Right-click fully-grown crops to harvest them and reset the crop to its initial growth stage.
- Works for vanilla `CropBlock` crops and (optionally) modded crops that use an integer `age` property.
- If the crop/block has its own `useWithoutItem` right-click behavior, FarmTweaks will prefer letting the block handle it.

### ✅ Fortune-Affected Crop Drops (hoes)
- When using a hoe with **Fortune**, FarmTweaks adds a Fortune-style bonus to *non-seed* drops from "wheat-like" crops.
- Seed-like drops are excluded using the `farmtweaks:seedlike` item tag.

### ✅ AoE Harvest (hoes)
- Using a hoe enables optional AoE harvesting: FarmTweaks flood-fills to nearby crop blocks and harvests up to a configurable maximum.
- AoE scaling is based on **Efficiency**:
  `maxHarvest = 1 + (Efficiency level * aoeHarvestCountStep)` (clamped to 1..256)
- Sneaking disables AoE (single-block harvest only).

### ✅ AoE Tilling (hoes)
- Right-click dirt/grass/path with a hoe to convert blocks to farmland.
- AoE tilling flood-fills through nearby tillable blocks up to a configurable maximum.
- AoE scaling is based on **Efficiency**:
  `maxTill = 1 + (Efficiency level * aoeTillingCountStep)` (clamped to 1..256)
- Sneaking disables AoE (single-block till only).

### ✅ Crop XP Rewards
- Optionally spawns XP orbs when harvesting mature crops.
- Controlled by `xpPerCrop` (set `cropXpRewards=false` to disable regardless of value).

### ✅ Serene Seasons Compatibility
- Optional Serene Seasons integration for version **1.21.1-10.1.0.3**.
- When enabled, FarmTweaks' extra Fortune crop bonus only applies to crops Serene Seasons considers in-season/fertile.
- Optional in-season XP boost adds extra XP on top of `xpPerCrop`.
- If Serene Seasons is not installed, FarmTweaks falls back to normal behavior.

### ✅ Seed Bag
- Craftable Seed Bag that stores up to **640** of a single seed type (10 stacks).
- Right-click (in air) deposits matching seeds from your inventory into the bag.
- Right-click a block to plant seeds using the stored seed type.
- Efficiency on the Seed Bag increases planting radius; sneaking disables AoE planting.
- Inventory QoL:
  - Right-click seeds onto the bag to deposit.
  - Right-click the bag onto an empty slot to withdraw.
- Allowed seed items:
  - Anything in the `c:seeds` tag.
  - Plus anything in `farmtweaks:seed_bag_plantables` (defaults include carrots and potatoes).

### ✅ Tool Tag Tweaks
- Moves **pumpkin** and **melon** into `minecraft:mineable/hoe` (and removes them from `mineable/axe`).

### ✅ Custom Water Hydration Range
- Optional configurable farmland hydration range for water.
- Uses the same water-fluid hydration check as vanilla, so normal water and waterlogged blocks can count.
- Waterlogged blocks can be disabled separately.
- Does not make water cauldrons or arbitrary modded blocks hydrate farmland.

## Configuration

FarmTweaks uses the standard NeoForge config system. The config file is generated at:
`config/farmtweaks.toml`

Notable options include:
- Feature toggles: right-click harvest, Fortune crops, AoE harvest/tilling, seed bags, crop XP, generic age-crop support, whitelist-only harvesting
- Serene Seasons compat toggles: `sereneSeasonsFortuneOnlyInSeason`, `sereneSeasonsInSeasonXpBoost`
- Water hydration toggles: `customWaterHydrationRange`, `includeWaterloggedHydrationBlocks`
- Tuning: `xpPerCrop`, `sereneSeasonsXpBoostAmount`, `waterHydrationHorizontalRange`, `waterHydrationVerticalRange`, `aoeHarvestCountStep`, `aoeTillingCountStep`

### Tags (datapack-friendly)
- `farmtweaks:seedlike` (item tag): items treated as seeds for the Fortune-bonus exclusion.
- `farmtweaks:seed_bag_plantables` (item tag): extra items the Seed Bag may accept/plant (e.g., carrots/potatoes).
- `farmtweaks:right_click_harvestable` (block tag): optional whitelist for "generic age crop" harvesting when `harvestWhitelistTagOnly=true`.

## Dependencies

- Minecraft **1.21.1**
- NeoForge **21.1.219+**
- Java **21**

Optional:
- **Cloth Config** (NeoForge) to get an in-game config screen from the mod list. FarmTweaks uses reflection, so it runs fine without Cloth Config installed.

## Installation

1. Install Minecraft 1.21.1 with NeoForge.
2. Drop the FarmTweaks `.jar` into your instance's `mods/` folder.
3. Launch once to generate `config/farmtweaks.toml`, then adjust settings as desired.

## Development (this repo)

- `./gradlew runClient` (or `gradlew.bat runClient` on Windows)
- `./gradlew build`
- `./gradlew runGameTestServer` (or `gradlew.bat runGameTestServer` on Windows)

### Versioning and releases

Normal CI builds automatically include build metadata when `GITHUB_RUN_NUMBER` or
`BUILD_NUMBER` is available. For example, `mod_version=0.6.4` produces an artifact
version such as `0.6.4+build.128` without modifying the working tree.

Use a clean Git worktree and choose the release level:

```powershell
gradlew.bat releasePatch
gradlew.bat releaseMinor
gradlew.bat releaseMajor
```

`releasePatch` changes `0.6.4` to `0.6.5`, `releaseMinor` changes it to `0.7.0`,
and `releaseMajor` changes it to `1.0.0`. The legacy `release` task aliases
`releasePatch`.

Each release task increments `mod_version`, builds the new version, commits the
version change, and creates an annotated `v<version>` tag. It does not push to a
remote automatically. If the build fails before the commit, the version file is
restored.

If your IDE is missing dependencies, try:
- `./gradlew --refresh-dependencies`

## Roadmap

The following items are not yet implemented and are not included in the progress bar above.

- [ ] Mouse Tweaks integration: allow Seed Bag quick in/out transfer while dragging items with RMB held.
- [ ] WD's Selling Bin integration: right-click the selling bin to dump Seed Bag contents and apply correct point/count logic.
- [ ] Little Joys integration: when harvesting (by hand or by hoe), trigger/apply Little Joys harvest-event chances as appropriate.
- [ ] Seed Bag planting mode switcher:
  - [ ] Square planting mode.
  - [ ] Radial planting mode.
  - [ ] Point-to-point planting mode.
  - [ ] Switch modes with the mouse scroll wheel while the Seed Bag is held.
- [ ] Custom Pumpkin Slice drops when breaking Pumpkin Blocks, similar to Melon Blocks.
  - [ ] Automatically detect Farmers' Delight when installed.
  - [ ] Use Farmers' Delight's Pumpkin Slice item instead of FarmTweaks' item when available.
