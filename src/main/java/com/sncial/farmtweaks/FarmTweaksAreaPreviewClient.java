package com.sncial.farmtweaks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ItemAbilities;

@EventBusSubscriber(modid = FarmTweaks.MODID, value = Dist.CLIENT)
public final class FarmTweaksAreaPreviewClient {
    private static final double OUTLINE_OFFSET = 0.003;

    private FarmTweaksAreaPreviewClient() {}

    @SubscribeEvent
    public static void renderAreaPreview(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Level level = minecraft.level;
        if (player == null || level == null || !(minecraft.hitResult instanceof BlockHitResult hit)) {
            return;
        }

        Preview preview = previewFor(level, player, hit);
        if (preview.cells().isEmpty()) {
            return;
        }

        renderBoundary(event.getPoseStack(), event.getCamera().getPosition(), preview);
    }

    private static Preview previewFor(Level level, Player player, BlockHitResult hit) {
        ItemStack held = player.getMainHandItem();
        BlockPos center = hit.getBlockPos();
        if (held.getItem() instanceof SeedBagItem bag && Config.enableSeedBags()) {
            return new Preview(seedBagCells(center, bag.tier().plantingRadius(player.isShiftKeyDown())), center.getY() + 1, 0.1f, 1.0f, 0.15f);
        }
        if (held.getItem() instanceof HoeItem) {
            return hoePreview(level, player, center, held);
        }
        return Preview.EMPTY;
    }

    private static Set<FootprintBoundary.Cell> seedBagCells(BlockPos center, int radius) {
        Set<FootprintBoundary.Cell> cells = new HashSet<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Config.seedBagAoeShape().includes(dx, dz, radius)) {
                    cells.add(new FootprintBoundary.Cell(center.getX() + dx, center.getZ() + dz));
                }
            }
        }
        return cells;
    }

    private static Preview hoePreview(Level level, Player player, BlockPos center, ItemStack hoe) {
        BlockState centerState = level.getBlockState(center);
        HoeTillingMode mode = HoeTillingMode.fromTarget(centerState.is(Blocks.FARMLAND));
        if (isHoeTarget(level, player, center, mode)) {
            return new Preview(connectedHoeTargets(level, player, center, mode, hoe), center.getY() + 1, mode == HoeTillingMode.REVERT_TO_DIRT ? 0.95f : 0.3f, mode == HoeTillingMode.REVERT_TO_DIRT ? 0.35f : 0.9f, 0.2f);
        }
        if (centerState.getBlock() instanceof CropBlock crop && crop.isMaxAge(centerState)) {
            return new Preview(connectedMatureCrops(level, center, hoe, player.isShiftKeyDown()), center.getY() + 1, 1.0f, 0.7f, 0.2f);
        }
        return Preview.EMPTY;
    }

    private static Set<FootprintBoundary.Cell> connectedHoeTargets(Level level, Player player, BlockPos center, HoeTillingMode mode, ItemStack hoe) {
        Set<FootprintBoundary.Cell> cells = new HashSet<>();
        int durabilityLimit = HoeOperationLimits.maxActions(Integer.MAX_VALUE, Math.max(0, hoe.getMaxDamage() - hoe.getDamageValue()), player.isCreative());
        int sideLength = FarmTweaks.hoeSideLength(level, hoe);
        for (FootprintBoundary.Cell cell : HoeTillingArea.cells(center.getX(), center.getZ(), player.isShiftKeyDown(), sideLength)) {
            if (cells.size() >= durabilityLimit) break;
            BlockPos pos = new BlockPos(cell.x(), center.getY(), cell.z());
            if (isHoeTarget(level, player, pos, mode)) cells.add(cell);
        }
        return cells;
    }

    private static Set<FootprintBoundary.Cell> connectedMatureCrops(Level level, BlockPos center, ItemStack hoe, boolean sneaking) {
        int limit = sneaking ? 1 : hoeLimit(level, hoe, Config.aoeHarvestCountStep(), false);
        return floodFill(level, center, limit, pos -> {
            BlockState state = level.getBlockState(pos);
            return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
        });
    }

    private static Set<FootprintBoundary.Cell> floodFill(Level level, BlockPos center, int limit, java.util.function.Predicate<BlockPos> valid) {
        Set<FootprintBoundary.Cell> result = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(center);
        visited.add(center);
        while (!queue.isEmpty() && result.size() < limit) {
            BlockPos current = queue.removeFirst();
            if (!valid.test(current)) continue;
            result.add(new FootprintBoundary.Cell(current.getX(), current.getZ()));
            for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos next = current.offset(dx, 0, dz);
                if (visited.add(next) && valid.test(next)) queue.addLast(next);
            }
        }
        return result;
    }

    private static boolean isHoeTarget(Level level, Player player, BlockPos pos, HoeTillingMode mode) {
        if (mode == HoeTillingMode.REVERT_TO_DIRT) return level.getBlockState(pos).is(Blocks.FARMLAND);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        return level.getBlockState(pos).getToolModifiedState(new UseOnContext(player, InteractionHand.MAIN_HAND, hit), ItemAbilities.HOE_TILL, true) != null;
    }

    private static int hoeLimit(Level level, ItemStack hoe, int step, boolean creative) {
        int efficiency = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY), hoe);
        int configured = Math.min(256, 1 + efficiency * step);
        int remaining = Math.max(0, hoe.getMaxDamage() - hoe.getDamageValue());
        return HoeOperationLimits.maxActions(configured, remaining, creative);
    }

    private static void renderBoundary(PoseStack poseStack, Vec3 camera, Preview preview) {
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        for (FootprintBoundary.Edge edge : FootprintBoundary.edges(preview.cells())) {
            addLine(buffer, poseStack, edge.startX(), preview.y() + OUTLINE_OFFSET, edge.startZ(), edge.endX(), preview.y() + OUTLINE_OFFSET, edge.endZ(), preview.red(), preview.green(), preview.blue());
        }
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(6.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0F);
        poseStack.popPose();
    }

    private static void addLine(BufferBuilder buffer, PoseStack poseStack, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue) {
        var pose = poseStack.last();
        buffer.addVertex(pose.pose(), (float) x1, (float) y1, (float) z1).setColor(red, green, blue, 1.0f).setNormal(0, 1, 0);
        buffer.addVertex(pose.pose(), (float) x2, (float) y2, (float) z2).setColor(red, green, blue, 1.0f).setNormal(0, 1, 0);
    }

    private record Preview(Set<FootprintBoundary.Cell> cells, int y, float red, float green, float blue) {
        private static final Preview EMPTY = new Preview(Set.of(), 0, 0, 0, 0);
    }
}
