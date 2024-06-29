package com.sammy.structure_no_rotate;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Str.mod_id, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrJsonGeneration {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
            event.includeServer(),
            new StrTemplatePoolTagsProvider(
                generator, existingFileHelper)
            );
    }
}
