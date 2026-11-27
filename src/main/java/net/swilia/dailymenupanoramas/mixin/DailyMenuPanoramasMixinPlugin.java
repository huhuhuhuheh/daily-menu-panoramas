package net.swilia.dailymenupanoramas.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DailyMenuPanoramasMixinPlugin implements IMixinConfigPlugin {

    private static final boolean MODERN_CUBEMAP;

    static {
        String version = FabricLoader.getInstance().getRawGameVersion();
        boolean modern = false;
        try {
            String[] parts = version.split("[^0-9.]+")[0].split("\\.");
            int major = Integer.parseInt(parts[0]);
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            modern = (major > 1) || (major == 1 && minor > 21) || (major == 1 && minor == 21 && patch >= 6);
        } catch (Exception ignored) {
        }
        MODERN_CUBEMAP = modern;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // 1.21.6+
        if (mixinClassName.contains("ModernCubeMapRenderer") || mixinClassName.endsWith("CubemapTextureMixin")) {
            return MODERN_CUBEMAP;
        }
        // pre-1.21.6
        if (mixinClassName.equals("net.swilia.dailymenupanoramas.mixin.CubeMapRendererMixin")
            || mixinClassName.equals("net.swilia.dailymenupanoramas.mixin.CubeMapRendererAccessor")) {
            return !MODERN_CUBEMAP;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}