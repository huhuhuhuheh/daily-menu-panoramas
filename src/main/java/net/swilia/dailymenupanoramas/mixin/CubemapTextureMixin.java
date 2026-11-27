package net.swilia.dailymenupanoramas.mixin;

//? if <1.21.6 {
/*import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.gui.CubeMapRenderer.class)
public class CubemapTextureMixin {}
*///?} else {

import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.swilia.dailymenupanoramas.client.PanoramaLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(CubemapTexture.class)
public class CubemapTextureMixin {
    private static final Pattern VANILLA_FACE = Pattern.compile("^textures/gui/title/background/panorama(?:_([0-5]))?(?:\\.png)?$");

    @Redirect(
        method = "loadContents",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureContents;load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/TextureContents;")
    )
    private TextureContents serveSelectedPanorama(ResourceManager manager, Identifier id) throws IOException {
        if (id.getNamespace().equals("minecraft")) {
            Matcher matcher = VANILLA_FACE.matcher(id.getPath());
            if (matcher.matches()) {
                String face = matcher.group(1) == null ? "0" : matcher.group(1);
                Identifier chosen = PanoramaLogic.INSTANCE.getPanoramaId();
                String base = chosen.getPath();
                if (!base.endsWith("/panorama")) {
                    base = base + "/panorama";
                }
                Identifier faceId = Identifier.of(chosen.getNamespace(), base + "_" + face + ".png");
                return TextureContents.load(manager, faceId);
            }
        }
        return TextureContents.load(manager, id);
    }
}
//?}