# Changelog

All notable player-facing changes to Farm Tweaks are recorded here. This project follows [Semantic Versioning](https://semver.org/). Dates use UTC.

## [Unreleased]

## [0.7.2] - 2026-08-15

### Added

- Right-click harvesting for sugar cane, mature cocoa, and mature sweet berry bushes, each with its own vanilla-friendly replant and yield rules.
- Per-hoe Till, Untill, and Harvest modes with Ctrl+scroll cycling and held-item status text.
- A dedicated Hoe Actions configuration tab with per-tier centered tilling ranges.
- Per-Seed-Bag square/radial planting modes, switched with Ctrl+scroll and persisted on the bag.
- A Controls tab with mode-HUD, Ctrl+scroll, and scroll-inversion preferences stored in a client-only config.
- Configurable farmland-trampling prevention and an in-game Cloth Config screen.
- Unit coverage for special crop yields, harvest experience, hoe mode, tier-range, and client-scroll behavior.

### Changed

- Cocoa, sugar cane, and sweet berries are now deliberately excluded from connected area harvesting and Farm Tweaks' extra Fortune bonus.
- Hoe previews now represent tiered, centered tilling and farmland reversion only; crop harvesting does not show a boundary preview.
- Pumpkin recipes, including the restored vanilla pumpkin-seed recipe when slice recipes are disabled, now respect the configured drop and recipe choices.
- Pumpkin pie now uses two Pumpkin Slices, sugar, and an egg, while one Pumpkin Slice crafts one pumpkin seed and nine craft a pumpkin block.

### Removed

- The project override for the vanilla pumpkin-pie recipe, allowing the Farm Tweaks recipe registration to supply the intended slice-based recipe.

## [0.7.1] - 2026-08-13

### Added

- Tiered hoe tilling and safe farmland reversion, with boundary previews.
- Pumpkin Slices, pumpkin crafting support, and improved hoe mining tags for pumpkins, melons, mushrooms, and wart blocks.

### Changed

- Expanded the harvesting and farming-tool experience while keeping pumpkins, melons, and mushrooms compatible with axes.

## [0.7.0] - 2026-08-13

### Added

- Tiered Seed Bags and area planting previews.
- Cultivable vanilla small flowers and their seed/harvest loop.
- A player-facing roadmap and feature documentation.

## [0.6.8] - 2026-08-12

### Changed

- Refined Seed Bag tiers and optional-integration behavior.

## [0.6.7] - 2026-08-12

### Changed

- Documentation and feature-progress updates.

## [0.6.6] - 2026-08-09

### Fixed

- Ensured the Gradle wrapper is executable for supported development and CI environments.

## [0.6.5] - 2026-08-09

### Fixed

- Corrected the release-command process invocation.

[Unreleased]: https://github.com/Sijan079/FarmTweaks/compare/v0.7.2...HEAD
[0.7.2]: https://github.com/Sijan079/FarmTweaks/compare/v0.7.1...v0.7.2
[0.7.1]: https://github.com/Sijan079/FarmTweaks/compare/v0.7.0...v0.7.1
[0.7.0]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.8...v0.7.0
[0.6.8]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.7...v0.6.8
[0.6.7]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.6...v0.6.7
[0.6.6]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.5...v0.6.6
