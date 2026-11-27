package net.swilia.dailymenupanoramas.client;

//? if <1.21.6 {
/*public class DailyCubemapTexture {}
*///?} else {

import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DailyCubemapTexture extends CubemapTexture {
    private static final Logger LOGGER = LoggerFactory.getLogger("DailyMenuPanoramas");
    private static final String[] FACES = {"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};
    private final Identifier registeredId;

    public DailyCubemapTexture(Identifier id) {
        super(id);
        this.registeredId = id;
    }

    @Override
    public TextureContents loadContents(ResourceManager manager) throws IOException {
        Identifier chosen = PanoramaLogic.INSTANCE.getPanoramaId();
        String base = chosen.getPath();
        if (!base.endsWith("/panorama")) {
            base = base + "/panorama";
        }

        LOGGER.info("Cubemap load → registered={} chosen={}", registeredId, chosen);
        TextureContents first = loadFace(manager, chosen, base, FACES[0]);
        try {
            NativeImage faceImage = first.image();
            int width = faceImage.getWidth();
            int height = faceImage.getHeight();
            NativeImage cubemap = new NativeImage(width, height * 6, false);
            try {
                copyFace(faceImage, cubemap, width, height, 0);

                for (int i = 1; i < FACES.length; i++) {
                    TextureContents face = loadFace(manager, chosen, base, FACES[i]);
                    try {
                        copyFace(face.image(), cubemap, width, height, i);
                    } finally {
                        face.close();
                    }
                }

                return new TextureContents(cubemap, DailyCubemapMetadata.create());
            } catch (IOException | RuntimeException e) {
                cubemap.close();
                throw e;
            }
        } finally {
            first.close();
        }
    }

    private static TextureContents loadFace(ResourceManager manager, Identifier baseId, String basePath, String suffix) throws IOException {
        Identifier faceId = faceId(baseId, basePath, suffix);
        try {
            TextureContents contents = TextureContents.load(manager, faceId);
            NativeImage image = contents.image();
            LOGGER.info("Cubemap face loaded → {} ({}x{})", faceId, image.getWidth(), image.getHeight());
            return contents;
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Cubemap face load failed → {} ({})", faceId, e.toString());
            throw e;
        }
    }

    private static void copyFace(NativeImage source, NativeImage target, int width, int height, int slot) throws IOException {
        if (source.getWidth() != width || source.getHeight() != height) {
            throw new IOException("Cubemap face " + FACES[slot] + " is " + source.getWidth() + "x" + source.getHeight() + ", expected " + width + "x" + height);
        }
        source.copyRect(target, 0, 0, 0, slot * height, width, height, false, true);
    }

    private static Identifier faceId(Identifier baseId, String basePath, String suffix) {
        return Identifier.of(baseId.getNamespace(), basePath + suffix);
    }
}
//?}