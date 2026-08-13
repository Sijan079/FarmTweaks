# FarmTweaks

FarmTweaks is a NeoForge 1.21.1 mod that makes everyday farming quicker while keeping its interactions recognizably vanilla.

## Current Features

- Right-click harvest mature crops, nether wart, and cocoa with automatic replanting, optional Fortune-aware drops, crop XP, and connected AoE harvesting.
- Tiered Seed Bags that store one plantable type and plant a charged target area. Their coverage is previewed with a bright perimeter outline.
- Tiered hoe tilling and farmland reversion: Wood starts at 1×1, Stone 2×2, Iron/Gold 3×3, Diamond 4×4, and Netherite 5×5. Hoe tier alone defines this footprint; Efficiency extends connected crop harvesting instead. Sneaking always targets one block.
- Farmland cannot be trampled, while normal dehydration and blocked-above reversion still work. A hoe can deliberately revert targeted farmland to dirt without pushing the player into the new block.
- Hoe coverage previews that show only the outer boundary of the exact till, revert, or harvest area.
- Cultivable seeds and farmland crops for all current vanilla small flowers, including Seed Bag and existing harvest-system compatibility.
- Hoe mining support for pumpkins, carved pumpkins, melons, mushroom blocks/stems, and the existing wart blocks. Pumpkin, melon, and mushroom blocks remain axe-minable too.
- Pumpkin blocks yield Fortune-affected Pumpkin Slices (while Silk Touch preserves the block); pumpkin pie and pumpkin-seed recipes use compatible slices, while jack-o'-lanterns still use carved pumpkins.
- Configurable farmland hydration range, including optional waterlogged sources.

## Roadmap

These are the currently open GitHub issues. They are intentionally summarized here; the linked issues remain the source of truth for detailed acceptance criteria.

- [ ] [#1 — Compacted produce storage](https://github.com/Sijan079/FarmTweaks/issues/1): reversible 3×3 storage blocks for common farm produce, reducing bulk-storage clutter without duplicating hay bales for wheat.
- [ ] [#2 — Cultivable vanilla small flowers](https://github.com/Sijan079/FarmTweaks/issues/2) *(in progress)*: complete and polish the matching seed, crop, harvesting, and Seed Bag loop for vanilla small flowers.
- [ ] [#3 — Mycelium propagation and mushroom growth](https://github.com/Sijan079/FarmTweaks/issues/3): let bone meal spread mycelium to suitable nearby soil and occasionally sprout mushrooms.
- [ ] [#4 — Cultivated first mycelium](https://github.com/Sijan079/FarmTweaks/issues/4): provide a mushroom-and-bone-meal path from coarse dirt/podzol to a player's first mycelium.
- [ ] [#5 — Timed composter](https://github.com/Sijan079/FarmTweaks/issues/5): turn the composter into a small inventory-based processor that decomposes inputs over time and safely holds finished rewards.
- [ ] [#6 — Composter rewards and mushroom bonus](https://github.com/Sijan079/FarmTweaks/issues/6): add weighted compost rewards, with mushrooms increasing the chance of Rich Soil while bone meal remains the common result.
- [ ] [#7 — Rich Soil and Rich Farmland](https://github.com/Sijan079/FarmTweaks/issues/7): a compost-earned premium soil that can be tilled into moderately faster-growing farmland without replacing ordinary farming.
- [ ] [#8 — Seed Bag right-click dragging](https://github.com/Sijan079/FarmTweaks/issues/8): bulk deposit and withdrawal through vanilla inventory screens, with optional Mouse Tweaks compatibility and no required dependency.

## Development

Run `./gradlew build` for the standard validation build, or `./gradlew runClient` to launch the development client.
