# Recent Gameplay Checklist

Use the development client to manually verify the currently uncommitted FarmTweaks changes. A successful Gradle build confirms compilation and resource packaging; it does not replace these in-game checks.

## Hoe coverage and farmland

- Verify the hoe footprint by tier: Wood 1x1, Stone 2x2, Iron/Gold 3x3, Diamond 4x4, and Netherite 5x5.
- Verify Efficiency does not change the till/revert footprint. It extends connected crop harvesting only; sneaking always targets one block.
- Verify the bright outline shows only the outer boundary of the exact till, revert, or harvest area.
- Target existing farmland with the hoe reversion action. It should become dirt without moving the player into the new block.
- Jump on farmland. It must not revert from trampling, but it must still revert normally when dehydrated or blocked above.

## Harvesting and drops

- Right-click a mature connected field with a hoe. Mature crops should harvest, replant at their initial age, consume hoe durability, and respect the configured AoE limit.
- Test mature nether wart and cocoa. Both should right-click harvest and reset to age 0; cocoa must remain attached to the same jungle log and keep its facing.
- Compare Fortune behavior: carrots and potatoes rely only on vanilla drops; seed-replanting crops and cocoa receive the FarmTweaks extra bonus; nether wart keeps its vanilla loot-table Fortune behavior.

## Pumpkin slices and recipes

- Break a pumpkin without Silk Touch. It should drop Pumpkin Slices, with Fortune increasing the slice yield and Silk Touch preserving the pumpkin block drop.
- Confirm the Pumpkin Slice item uses its orange slice icon in the inventory.
- Craft one pumpkin seed from one Pumpkin Slice, and pumpkin pie from two slices, sugar, and an egg. Craft nine slices into one pumpkin block. The jack-o'-lantern recipe must still require a carved pumpkin and a torch.
- If Farmer's Delight is installed, confirm its Pumpkin Slice is accepted by the two overridden vanilla recipes and is used by the pumpkin-drop fallback.

## Automated checks run

- `VanillaHarvestCropAgesTest` passed for nether wart and cocoa maturity thresholds.
- `HarvestFortunePolicyTest` and `PumpkinSliceDropsTest` pass for the related pure drop rules.
- `./gradlew build` passed after the harvest, recipe, and item-texture changes.
