package net.swilia.dailymenupanoramas.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

class DailyMenuPanoramasClient : ClientModInitializer {

    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
            object : SimpleSynchronousResourceReloadListener {
                override fun getFabricId(): Identifier = Identifier.of("daily_menu_panoramas", "pack_panoramas")

                //? if <26.1 {
                override fun reload(manager: ResourceManager) {
                    PanoramaLogic.invalidatePackPanoramaCache()
                    PanoramaLogic.preloadPackFaceTextures()
                    PanoramaLogic.ensureCurrentPackRegistered()
                }
                //?} else {
                /*override fun onResourceManagerReload(manager: ResourceManager) {
                    PanoramaLogic.invalidatePackPanoramaCache()
                    PanoramaLogic.preloadPackFaceTextures()
                    PanoramaLogic.ensureCurrentPackRegistered()
                }
                *///?}
            }
        )
    }
}