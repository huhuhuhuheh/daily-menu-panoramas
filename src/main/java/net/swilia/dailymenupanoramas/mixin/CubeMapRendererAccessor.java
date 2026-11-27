package net.swilia.dailymenupanoramas.mixin;

//? if <1.21.6 {
/*import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(net.minecraft.client.gui.CubeMapRenderer.class)
public interface CubeMapRendererAccessor {
    @Accessor("faces")
    @Mutable
    void dmp$setFaces(List<Identifier> faces);
}
*///?} else {

import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.gui.CubeMapRenderer.class)
public interface CubeMapRendererAccessor {}
//?}