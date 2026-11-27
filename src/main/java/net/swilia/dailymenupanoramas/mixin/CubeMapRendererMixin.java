package net.swilia.dailymenupanoramas.mixin;

//? if <1.21.6 {
/*import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.Identifier;
import net.swilia.dailymenupanoramas.client.PanoramaLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CubeMapRenderer.class)
public class CubeMapRendererMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceFaces(Identifier original, CallbackInfo ci) {
        Identifier base = PanoramaLogic.INSTANCE.getPanoramaId();
        List<Identifier> newFaces = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            newFaces.add(Identifier.of(base.getNamespace(), base.getPath() + "_" + i + ".png"));
        }
        ((CubeMapRendererAccessor) ((Object) this)).dmp$setFaces(newFaces);
    }
}
*///?} else {

import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.gui.CubeMapRenderer.class)
public class CubeMapRendererMixin {}
//?}