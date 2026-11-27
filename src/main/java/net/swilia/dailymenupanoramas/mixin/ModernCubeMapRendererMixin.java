package net.swilia.dailymenupanoramas.mixin;

//? if <1.21.6 {
/*import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.gui.CubeMapRenderer.class)
public class ModernCubeMapRendererMixin {}
*///?} else {

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.swilia.dailymenupanoramas.client.DailyCubemapTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CubeMapRenderer.class)
public class ModernCubeMapRendererMixin {
    @Inject(method = "registerTextures", at = @At("HEAD"), cancellable = true)
    private void dmp$usePanorama(TextureManager textureManager, CallbackInfo ci) {
        Identifier base = Identifier.of("minecraft", "textures/gui/title/background/panorama");
        textureManager.registerTexture(base, (AbstractTexture) new DailyCubemapTexture(base));
        ci.cancel();
    }
}
//?}