package net.swilia.dailymenupanoramas.mixin;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.Identifier;
import net.swilia.dailymenupanoramas.client.PanoramaLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CubeMapRenderer.class)
public class CubeMapRendererMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Identifier replaceTextureId(Identifier original) {
        return PanoramaLogic.INSTANCE.getPanoramaId();
    }
}