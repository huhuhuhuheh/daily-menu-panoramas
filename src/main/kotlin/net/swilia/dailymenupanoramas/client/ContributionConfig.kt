package net.swilia.dailymenupanoramas.client

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object ContributionConfig {
    private val configFile: File = FabricLoader.getInstance().configDir.resolve("daily_menu_panoramas_contrib.json").toFile()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    var disableContributionScreen: Boolean = false

    fun load() {
        if (configFile.exists()) {
            try {
                FileReader(configFile).use { reader ->
                    val data = gson.fromJson(reader, ConfigData::class.java)
                    disableContributionScreen = data.disableContributionScreen
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun save() {
        try {
            FileWriter(configFile).use { writer ->
                val data = ConfigData(disableContributionScreen)
                gson.toJson(data, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private data class ConfigData(val disableContributionScreen: Boolean)
}