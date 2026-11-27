package net.swilia.dailymenupanoramas.client

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen

private val YACL_IDS = arrayOf("yet_another_config_lib_v3", "yet_another_config_lib")

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        if (YACL_IDS.none { FabricLoader.getInstance().isModLoaded(it) }) {
            return ConfigScreenFactory { null }
        }
        return ConfigScreenFactory { parent: Screen -> buildYaclScreen(parent) }
    }
}