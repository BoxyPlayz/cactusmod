package com.steamteam.cactusmod;

import java.util.function.Function;

import com.steamteam.cactusmod.blocks.DeathCactus;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class CactusMod {

    public static <T extends Block> DeferredSupplier<T> registerBlock(String name,
            Function<BlockBehaviour.Properties, T> blockFactory,
            BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        DeferredSupplier<T> block = BLOCKS.register(name,
                () -> blockFactory.apply(settings.setId(blockKey)));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or
        // `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            ITEMS.register(name,
                    () -> new BlockItem(block.get(),
                            new Item.Properties().setId(itemKey).arch$tab(CreativeModeTabs.COMBAT)
                                    .useBlockDescriptionPrefix()));
        }

        return block;
    }

    public static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static final String MOD_ID = "cactusmod";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    public static final DeferredSupplier<DeathCactus> DEATH_CACTUS = registerBlock(
            "death_cactus",
            DeathCactus::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().strength(1F).sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, level, pos) -> true),
            true);

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
    }
}
