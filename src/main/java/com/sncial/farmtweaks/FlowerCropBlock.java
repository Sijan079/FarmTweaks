package com.sncial.farmtweaks;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

final class FlowerCropBlock extends CropBlock {
    private final Supplier<Item> seed;

    FlowerCropBlock(Supplier<Item> seed) {
        super(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WHEAT));
        this.seed = seed;
    }

    @Override
    protected Item getBaseSeedId() {
        return seed.get();
    }
}
