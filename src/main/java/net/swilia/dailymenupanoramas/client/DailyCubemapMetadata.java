package net.swilia.dailymenupanoramas.client;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;

//? if <1.21.11 {
/*public final class DailyCubemapMetadata {
    private DailyCubemapMetadata() {}

    public static TextureResourceMetadata create() {
        return new TextureResourceMetadata(true, false);
    }
}
*///?} else {

import net.minecraft.client.texture.MipmapStrategy;

public final class DailyCubemapMetadata {
    private DailyCubemapMetadata() {}

    public static TextureResourceMetadata create() {
        return new TextureResourceMetadata(true, false, MipmapStrategy.MEAN, 0.0F);
    }
}
//?}