package com.steamteam.cactusmod.blocks;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;

public class DeathCactus extends CactusBlock {

	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public DeathCactus(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
			InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		if (level.isClientSide()) {
			entity.hurtClient(level.damageSources().cactus());
		} else {
			if (state.getValue(POWER) == 0) {
				entity.hurtServer((ServerLevel) level, level.damageSources().cactus(), 4.0F);
			} else {
				entity.hurtServer((ServerLevel) level, level.damageSources().cactus(), state.getValue(POWER) * 3);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWER);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
			@Nullable Orientation orientation, boolean movedByPiston) {
		if (!level.isClientSide()) {
			int power = level.getBestNeighborSignal(pos);
			if (state.getValue(POWER) != power) {
				level.setBlock(pos, state.setValue(POWER, power), UPDATE_ALL);
			}
		}
	}

}
