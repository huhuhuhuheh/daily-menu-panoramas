package net.swilia.dailymenupanoramas.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import net.swilia.dailymenupanoramas.client.ContributionConfig;
import net.swilia.dailymenupanoramas.client.ContributionScreen;
import net.swilia.dailymenupanoramas.client.PanoramaLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
// To test always showing the contribution screen, set IS_DEV_ENV to true
// If you are testing it for seeing the "Do Not Show This Again" button, IS_DEV_ENV to true can also help
@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    private static final boolean IS_DEV_ENV = false;
    private static final double CHANCE = 0.005;
    private static boolean hasCheckedContribution = false;

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        Identifier panoramaId = PanoramaLogic.INSTANCE.getPanoramaId();
        MinecraftClient client = MinecraftClient.getInstance();

        for (int i = 0; i < 6; ++i) {
            final int index = i;
            Identifier faceIdentifier = panoramaId.withPath(path -> path + "_" + index + ".png");
            client.getTextureManager().getTexture(faceIdentifier);
        }

        if (!hasCheckedContribution) {
            hasCheckedContribution = true;
            ContributionConfig.INSTANCE.load();

            if (!ContributionConfig.INSTANCE.getDisableContributionScreen()) {
                boolean shouldShow = IS_DEV_ENV || (new Random().nextDouble() < CHANCE);
                if (shouldShow) {
                    client.setScreen(new ContributionScreen((TitleScreen) (Object) this));
                }
            }
        }
    }
}