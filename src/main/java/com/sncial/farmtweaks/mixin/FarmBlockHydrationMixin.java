package com.sncial.farmtweaks.mixin;

import com.sncial.farmtweaks.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public abstract class FarmBlockHydrationMixin {
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void farmtweaks$useCustomWaterHydrationRange(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.enableCustomWaterHydrationRange()) {
            return;
        }

        BlockState farmlandState = level.getBlockState(pos);
        int horizontalRange = Config.waterHydrationHorizontalRange();
        int verticalRange = Config.waterHydrationVerticalRange();

        for (BlockPos waterPos : BlockPos.betweenClosed(
                pos.offset(-horizontalRange, 0, -horizontalRange),
                pos.offset(horizontalRange, verticalRange, horizontalRange)
        )) {
            FluidState fluidState = level.getFluidState(waterPos);
            if (!fluidState.is(FluidTags.WATER)) {
                continue;
            }

            if (!Config.includeWaterloggedHydrationBlocks() && !level.getBlockState(waterPos).is(Blocks.WATER)) {
                continue;
            }

            if (farmlandState.canBeHydrated(level, pos, fluidState, waterPos)) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }
}
