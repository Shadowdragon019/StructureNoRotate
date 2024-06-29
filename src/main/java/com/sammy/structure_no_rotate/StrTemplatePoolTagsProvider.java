package com.sammy.structure_no_rotate;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class StrTemplatePoolTagsProvider extends TagsProvider<StructureTemplatePool> {
    protected StrTemplatePoolTagsProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, RegistryAccess.builtinCopy().registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY), Str.mod_id, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(StrTemplatePoolTags.doNotRotate)
            .addOptional(new ResourceLocation("chocolate", "white_palace/white_palace_left"))
            .addOptional(new ResourceLocation("chocolate", "white_palace/white_palace_middle"))
            .addOptional(new ResourceLocation("chocolate", "white_palace/white_palace_middle_bottom"))
            .addOptional(new ResourceLocation("chocolate", "white_palace/white_palace_right"))
            .addOptional(new ResourceLocation("chocolate", "white_palace/white_palace_basement"));
    }
}
