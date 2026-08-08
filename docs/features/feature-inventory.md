# FarmTweaks Feature Inventory

This document tracks the farming features currently implemented in the source code and the feature ideas that are still unimplemented. It is an implementation snapshot, not a replacement for detailed feature specifications. Update it when a feature changes status.

Status is based on the source and resources currently in this repository.

## Implemented Features

### 1. Right-click crop harvesting

- Harvest mature vanilla-style crops by right-clicking them.
- Automatically reset harvested crops to their initial growth state.
- Prefer a crop block's own `useWithoutItem` behavior when available.
- Support generic modded crops with an integer `age` property.
- Optionally restrict generic age-crop harvesting to the `farmtweaks:right_click_harvestable` block tag.
- Preserve normal block break/replant events and harvest statistics where possible.
- Respect the `rightClickHarvest`, `genericAgeCropHarvest`, and `harvestWhitelistTagOnly` configuration options.

Source: `FarmTweaks.java`, `Config.java`

### 2. Hoe Fortune crop bonuses

- Apply a Fortune-style bonus to eligible non-seed crop drops when harvesting with a hoe.
- Exclude seed-like drops using the `farmtweaks:seedlike` item tag.
- Gate the extra Fortune bonus by Serene Seasons crop fertility when configured.
- Keep the Serene Seasons dependency optional through reflection.

Source: `FarmTweaks.java`, `SereneSeasonsCompat.java`

### 3. Area-of-effect harvesting

- Flood-fill connected crop areas from the clicked crop.
- Traverse through mature and immature crops so connected mature crops can be harvested.
- Traverse across a one-block air gap to reach nearby mature crops.
- Scale the harvest limit using the hoe's Efficiency level.
- Disable area harvesting while sneaking.
- Clamp the maximum operation size to protect against runaway scans.

Source: `FarmTweaks.java`, `Config.java`

### 4. Area-of-effect tilling

- Convert supported dirt-like blocks to farmland with a hoe.
- Flood-fill connected tillable blocks using 8-direction horizontal connectivity.
- Scale the tilling limit using the hoe's Efficiency level.
- Damage the hoe for non-creative players.
- Disable area tilling while sneaking.
- Clamp the maximum operation size and search budget.

Source: `FarmTweaks.java`, `Config.java`

### 5. Crop experience rewards

- Spawn configurable experience rewards when mature crops are harvested.
- Allow crop XP rewards to be disabled independently of the configured amount.
- Add an optional extra XP bonus for in-season crops when Serene Seasons is installed.

Source: `FarmTweaks.java`, `Config.java`, `SereneSeasonsCompat.java`

### 6. Seed Bag item

- Register a craftable, single-stack Seed Bag item.
- Store up to 640 items of one exact seed/item type.
- Accept items in the `c:seeds` tag and items in `farmtweaks:seed_bag_plantables`.
- Deposit seeds into the bag through inventory slot interactions.
- Withdraw up to one stack from the bag into an empty inventory slot.
- Show stored seed type and quantity in the tooltip.
- Show fill progress using the item bar.
- Allow Efficiency enchantments and use Efficiency to increase planting radius.
- Plant stored seeds by right-clicking farmland or other valid planting locations.
- Disable area planting while sneaking.
- Preserve the bag contents using item custom data.

Source: `SeedBagItem.java`, `FarmTweaks.java`

### 7. Seed Bag compatibility behavior

- Preserve BundleItem behavior so Mouse Tweaks-style RMB dragging remains compatible with the bag’s inventory interactions.
- Deposit stored seeds into a `selling_bin` block entity from the `selling_bin` namespace while sneaking.
- Use the vanilla `Container` interface for Selling Bin insertion without a hard dependency on its internals.

Source: `SeedBagItem.java`

### 8. Custom farmland hydration range

- Optionally replace vanilla farmland water detection with a configurable horizontal and upward vertical range.
- Use NeoForge/Minecraft hydration checks for water sources.
- Optionally include waterlogged blocks as hydration sources.
- Leave the feature disabled by default.

Source: `FarmBlockHydrationMixin.java`, `Config.java`

### 9. Tool tag adjustments

- Make pumpkins and melons mineable with hoes through the Minecraft tool tags.
- Remove those blocks from the axe mineable tag in the project resources.

Resources: `src/main/resources/data/minecraft/tags/block/`

### 10. Optional configuration UI

- Register an in-game configuration screen when Cloth Config is installed.
- Keep Cloth Config and client-only classes optional through reflection.
- Expose feature toggles, compatibility toggles, and tuning values in the UI.
- Continue to work on dedicated servers and without Cloth Config installed.

Source: `ClothConfigCompat.java`, `FarmTweaks.java`

### 11. NeoForge configuration and data-driven extension points

- Generate the standard `config/farmtweaks.toml` configuration through NeoForge.
- Provide datapack-friendly tags for seed-like items, Seed Bag plantables, and generic harvest whitelisting.
- Register the Seed Bag and Farm Tweaks creative tab.
- Provide Seed Bag models, textures, recipe, advancement, and translations.

Source/resources: `Config.java`, `FarmTweaks.java`, `src/main/resources/`

## Unimplemented or Planned Features

### 1. Little Joys harvest integration

- Trigger or apply Little Joys harvest-event chances when harvesting by hand.
- Trigger or apply Little Joys harvest-event chances when harvesting with a hoe.

Status: Planned; no Little Joys integration is present in the current source.

### 2. Seed Bag planting mode switcher

- Add square planting mode.
- Add radial planting mode.
- Add point-to-point planting mode.
- Switch planting modes with the mouse wheel while holding the Seed Bag.

Status: Planned; current planting uses the existing radius-based pattern only.

### 3. Custom pumpkin slice drops

- Add custom pumpkin slice drops when breaking pumpkin blocks.
- Detect Farmers' Delight automatically when installed.
- Use Farmers' Delight’s Pumpkin Slice item when available.

Status: Planned; current resources only adjust pumpkin mining-tool tags.

## Clarifications and Known Documentation Drift

- The root README describes right-clicking in the air as a Seed Bag inventory-deposit action. The current `SeedBagItem.use` method intentionally does not implement that behavior; deposits currently happen through inventory slot interactions.
- The root README lists Mouse Tweaks compatibility and Selling Bin dumping under future plans, but both are implemented in `SeedBagItem.java`.
- No automated test suite was found under `src/test` during this inventory pass. Runtime behavior should be verified with the relevant NeoForge client/server or GameTest task when changes are made.

## Updating This Inventory

When implementing a feature:

1. Move it from **Unimplemented or Planned Features** to **Implemented Features**.
2. Add the important sub-behaviors beneath the main feature.
3. Update source/resource references if the implementation moves.
4. Add or update a detailed specification under `docs/features/` when the feature is substantial.
5. Record deferred work or limitations in the feature’s implementation notes.
