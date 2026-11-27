package net.swilia.dailymenupanoramas.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.gui.CubeMapRenderer.class)
public interface ModernCubeMapRendererAccessor {
    @Accessor("faces")
    @Mutable
    void dmp$setFacets(Identifier id);
}