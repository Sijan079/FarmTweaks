# FarmTweaks Feature Inventory

This is an implementation snapshot. GitHub Issues and detailed feature specifications remain the source of truth for future work.

## Implemented Features

### Right-click harvesting

- Mature vanilla crops, FarmTweaks flower crops, nether wart, and cocoa replant automatically after harvest.
- Cocoa is single-target and yields 5-6 beans total; one bean is replanted, leaving 4-5 for the player.
- Sugar cane harvests the clicked upper segment and every segment above it, always preserving the bottom stalk.
- Mature sweet berry bushes yield 3-4 berries and reset to age 1.
- Cocoa, sugar cane, and sweet berries are intentionally excluded from connected AoE harvesting.
- Crop harvesting preserves break/replant events, stats, configured crop XP, and hoe durability where applicable.

### Fortune policy

- Carrots and potatoes use their vanilla Fortune behavior only.
- FarmTweaks applies its extra Fortune bonus only to eligible seed-replanting crops.
- Cocoa, sugar cane, and sweet berries do not receive a FarmTweaks Fortune bonus.
- Nether wart uses its vanilla loot-table Fortune behavior.

### Hoe tools and previews

- Till and farmland-reversion footprints use ordered, centered additive ranges. Defaults are Wood 1x1, Stone 3x3, Iron/Gold 5x5, Diamond 7x7, and Netherite 9x9; ranges can scale as far as Netherite 19x19.
- Each hoe stores a Till, Untill, or Harvest mode. Ctrl+scroll cycles the held hoe; down is forward and up is backward by default. Harvest mode suppresses tilling.
- Efficiency increases only the connected crop-harvest budget.
- Sneaking limits tilling/reversion and harvest to the targeted block.
- Hoe previews render tilling/reversion boundaries, and Seed Bag previews render planting boundaries. Crop harvesting has no boundary preview.
- Farmland trampling prevention is configurable; dehydration and a blocking block above continue to revert farmland normally. Hoe reversion does not displace the player into dirt.

### Pumpkin slices

- Pumpkins broken without Silk Touch yield 3-9 Pumpkin Slices, including Fortune scaling. Silk Touch keeps the pumpkin block.
- FarmTweaks uses Farmer's Delight Pumpkin Slices when that optional mod is present; otherwise it uses the built-in Pumpkin Slice.
- One slice crafts one pumpkin seed; two slices, sugar, and an egg craft pumpkin pie; nine slices craft one pumpkin block.
- The jack-o'-lantern recipe remains unchanged.

### Other existing systems

- Tiered Seed Bags store one plantable item type and plant a charged target area. Ctrl+scroll switches each bag between persisted square and radial planting shapes.
- Vanilla small flowers have cultivation, Seed Bag, and harvest support.
- Optional custom farmland hydration range and optional Cloth Config screen support remain available. The Cloth screen separates Hoe Actions and client Controls from gameplay settings.
- Pumpkins, carved pumpkins, melons, mushroom blocks/stems, and wart blocks are hoe-mineable. Pumpkin, melon, and mushroom blocks/stems remain axe-mineable.

## Verification

Unit tests cover hoe range, harvest maturity, Fortune policy, special crop yields, harvest XP, and pumpkin-slice rules. The manual gameplay checklist is in `docs/testing/recent-gameplay-checklist.md`.

## Planned Features

- Little Joys harvest integration.
- The open GitHub Issue roadmap listed in the root README.
