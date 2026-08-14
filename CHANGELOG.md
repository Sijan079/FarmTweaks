# Changelog

All notable player-facing changes to Farm Tweaks are recorded here. This project follows [Semantic Versioning](https://semver.org/). Dates use UTC.

## [Unreleased]

### Added

- Right-click harvesting for sugar cane, mature cocoa, and mature sweet berry bushes, each with its own vanilla-friendly replant and yield rules.
- Per-hoe Till, Untill, and None modes. Press Ctrl + right-click while holding a hoe to cycle modes; the current mode appears in the tooltip.
- Unit coverage for special crop yields, harvest experience, and the new hoe modes.

### Changed

- Cocoa, sugar cane, and sweet berries are now deliberately excluded from connected area harvesting and Farm Tweaks' extra Fortune bonus.
- Hoe previews now represent tilling and farmland reversion only; crop harvesting does not show a boundary preview.
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

[Unreleased]: https://github.com/Sijan079/FarmTweaks/compare/v0.7.1...HEAD
[0.7.1]: https://github.com/Sijan079/FarmTweaks/compare/v0.7.0...v0.7.1
[0.7.0]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.8...v0.7.0
[0.6.8]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.7...v0.6.8
[0.6.7]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.6...v0.6.7
[0.6.6]: https://github.com/Sijan079/FarmTweaks/compare/v0.6.5...v0.6.6
