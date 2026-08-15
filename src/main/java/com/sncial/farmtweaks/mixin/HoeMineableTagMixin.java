package com.sncial.farmtweaks.mixin;

import com.sncial.farmtweaks.Config;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
abstract class HoeMineableTagMixin {
    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    private void farmTweaks$disableHoeMineableTag(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> callback) {
        if ((Object) this instanceof HoeItem && !Config.enableHoeMineableTag() && state.is(BlockTags.MINEABLE_WITH_HOE)) {
            callback.setReturnValue(1.0F);
        }
    }
}
