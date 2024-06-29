package com.sammy.structure_no_rotate.mixins;

import com.google.common.collect.Lists;
import com.sammy.structure_no_rotate.StrTemplatePoolTags;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(JigsawPlacement.class)
abstract class JigsawPlacementMixin {
    @Shadow
    @Final
    static Logger LOGGER;

    @Shadow
    private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement a, ResourceLocation b, BlockPos c, Rotation d, StructureTemplateManager e, WorldgenRandom f) {
        return Optional.empty();
    }

    @Shadow
    private static void addPieces(RandomState a, int b, boolean c, ChunkGenerator d, StructureTemplateManager e, LevelHeightAccessor f, RandomSource g, Registry<StructureTemplatePool> h, PoolElementStructurePiece i, List<PoolElementStructurePiece> j, VoxelShape k) {}

    /**
     * @author Sammy
     * @reason Easiest way to get new behavior
     */
    @Overwrite
    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext p_227239_, Holder<StructureTemplatePool> structureTemplatePool, Optional<ResourceLocation> p_227241_, int p_227242_, BlockPos p_227243_, boolean p_227244_, Optional<Heightmap.Types> p_227245_, int p_227246_) {
        RegistryAccess registryaccess = p_227239_.registryAccess();
        ChunkGenerator chunkgenerator = p_227239_.chunkGenerator();
        StructureTemplateManager structuretemplatemanager = p_227239_.structureTemplateManager();
        LevelHeightAccessor levelheightaccessor = p_227239_.heightAccessor();
        WorldgenRandom worldgenrandom = p_227239_.random();
        Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
        Rotation rotation = Rotation.getRandom(worldgenrandom);
        // All I added
        if (structureTemplatePool.containsTag(StrTemplatePoolTags.doNotRotate)) {
            rotation = Rotation.NONE;
        }
        StructureTemplatePool structuretemplatepool = structureTemplatePool.value();
        StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
        if (structurepoolelement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPos blockpos;
            if (p_227241_.isPresent()) {
                ResourceLocation resourcelocation = p_227241_.get();
                Optional<BlockPos> optional = getRandomNamedJigsaw(structurepoolelement, resourcelocation, p_227243_, rotation, structuretemplatemanager, worldgenrandom);
                if (optional.isEmpty()) {
                    LOGGER.error("No starting jigsaw {} found in start pool {}", resourcelocation, structureTemplatePool.unwrapKey().get().location());
                    return Optional.empty();
                }

                blockpos = optional.get();
            } else {
                blockpos = p_227243_;
            }

            Vec3i vec3i = blockpos.subtract(p_227243_);
            BlockPos blockpos1 = p_227243_.subtract(vec3i);
            PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(structuretemplatemanager, structurepoolelement, blockpos1, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuretemplatemanager, blockpos1, rotation));
            BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
            int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
            int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
            int k;
            if (p_227245_.isPresent()) {
                k = p_227243_.getY() + chunkgenerator.getFirstFreeHeight(i, j, p_227245_.get(), levelheightaccessor, p_227239_.randomState());
            } else {
                k = blockpos1.getY();
            }

            int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
            poolelementstructurepiece.move(0, k - l, 0);
            int i1 = k + vec3i.getY();
            return Optional.of(new Structure.GenerationStub(new BlockPos(i, i1, j), (p_227237_) -> {
                List<PoolElementStructurePiece> list = Lists.newArrayList();
                list.add(poolelementstructurepiece);
                if (p_227242_ > 0) {
                    AABB aabb = new AABB((double) (i - p_227246_), (double) (i1 - p_227246_), (double) (j - p_227246_), (double) (i + p_227246_ + 1), (double) (i1 + p_227246_ + 1), (double) (j + p_227246_ + 1));
                    VoxelShape voxelshape = Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST);

                    addPieces(p_227239_.randomState(), p_227242_, p_227244_, chunkgenerator, structuretemplatemanager, levelheightaccessor, worldgenrandom, registry, poolelementstructurepiece, list, voxelshape);
                    list.forEach(p_227237_::addPiece);
                }
            }));
        }
    }
}
