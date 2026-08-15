# Recent Gameplay Checklist

Use the development client to manually verify the currently uncommitted FarmTweaks changes. A successful Gradle build confirms compilation and resource packaging; it does not replace these in-game checks.

## Hoe coverage and farmland

- Ctrl+scroll while holding a hoe. Scroll down should cycle Till → Untill → Harvest → Till; scroll up must reverse the order. The mode persists on that individual hoe.
- In Till mode, dirt-like targets become farmland. In Untill mode, only farmland becomes dirt. Harvest mode suppresses tilling.
- Verify the default centered hoe footprint by tier: Wood 1x1, Stone 3x3, Iron/Gold 5x5, Diamond 7x7, and Netherite 9x9.
- Verify Efficiency does not change the till/revert footprint. It extends connected crop harvesting only; sneaking always targets one block.
- Verify the bright outline shows only the outer boundary of the exact till or farmland-reversion area; crop harvesting has no hoe boundary preview.
- Target existing farmland with the hoe reversion action. It should become dirt without moving the player into the new block.
- Toggle Prevent farmland trampling. With it enabled, jumping on farmland must not revert it; with it disabled, vanilla trampling resumes. In both cases farmland must still revert normally when dehydrated or blocked above.

## Seed Bag modes and controls

- Hold a Seed Bag and Ctrl+scroll. Confirm it switches between square and radial planting and that the selection persists on the individual bag.
- Confirm the targeted-empty-farmland preview matches the selected planting shape.
- In the Controls tab, disable Show held-item mode and confirm the hoe/Seed Bag status text disappears. Disable Ctrl+scroll switching and confirm scroll no longer changes modes. Enable inversion and confirm scroll direction reverses.

## Harvesting and drops

- Right-click a mature connected field with a hoe. Mature crops should harvest, replant at their initial age, consume hoe durability, and respect the configured AoE limit.
- Test mature nether wart and cocoa. Both should right-click harvest and reset to age 0; cocoa must remain attached to the same jungle log and keep its facing.
- Right-click sugar cane. It should harvest the clicked upper segment and any segments above it, always leaving the bottom stalk planted; it does not use connected AoE harvesting. Mature sweet berry bushes should yield 3-4 berries and regrow from age 1. Both are excluded from AoE and Fortune.
- With crop XP enabled, each harvested sugar-cane segment, nether wart, cocoa plant, and mature sweet berry bush should award the configured crop XP.
- Compare Fortune behavior: carrots and potatoes rely only on vanilla drops; seed-replanting crops receive the FarmTweaks extra bonus; nether wart keeps its vanilla loot-table Fortune behavior. Cocoa, sugar cane, and sweet berries do not receive FarmTweaks Fortune bonuses.

## Pumpkin slices and recipes

- Break a pumpkin without Silk Touch. It should drop Pumpkin Slices, with Fortune increasing the slice yield and Silk Touch preserving the pumpkin block drop.
- Confirm the Pumpkin Slice item uses its orange slice icon in the inventory.
- Craft one pumpkin seed from one Pumpkin Slice, and pumpkin pie from two slices, sugar, and an egg. Craft nine slices into one pumpkin block. The jack-o'-lantern recipe must still require a carved pumpkin and a torch.
- If Farmer's Delight is installed, confirm its Pumpkin Slice is accepted by the two overridden vanilla recipes and is used by the pumpkin-drop fallback.

## Automated checks run

- `VanillaHarvestCropAgesTest` passed for nether wart and cocoa maturity thresholds.
- `HarvestFortunePolicyTest` and `PumpkinSliceDropsTest` pass for the related pure drop rules.
- `./gradlew build` passed after the harvest, recipe, and item-texture changes.
