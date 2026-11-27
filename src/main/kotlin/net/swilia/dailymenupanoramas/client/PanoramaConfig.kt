package net.swilia.dailymenupanoramas.client

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object PanoramaConfig {
    private val configFile: File = FabricLoader.getInstance().configDir.resolve("daily_menu_panoramas.json").toFile()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    var shouldBeRandomInsteadOfOneAtAday: Boolean = false
    var periodic: String = "daily"
    var resourcePackMode: String = "disabled"

    val VALID_PERIODS: List<String> = listOf("daily", "weekly", "monthly")
    val VALID_RESOURCE_PACK_MODES: List<String> = listOf("random", "always", "always_active", "only_active", "disabled")

    fun load() {
        if (configFile.exists()) {
            try {
                FileReader(configFile).use { reader ->
                    val data = gson.fromJson(reader, ConfigData::class.java)
                    shouldBeRandomInsteadOfOneAtAday = data.shouldBeRandomInsteadOfOneAtAday
                    periodic = data.periodic?.takeIf { it in VALID_PERIODS } ?: "daily"
                    resourcePackMode = data.resourcePackMode?.takeIf { it in VALID_RESOURCE_PACK_MODES } ?: "disabled"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                save()
            }
        } else {
            save()
        }
    }

    fun save() {
        try {
            FileWriter(configFile).use { writer ->
                val data = ConfigData(shouldBeRandomInsteadOfOneAtAday, periodic, resourcePackMode)
                gson.toJson(data, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private data class ConfigData(
        val shouldBeRandomInsteadOfOneAtAday: Boolean,
        val periodic: String? = "daily",
        val resourcePackMode: String? = "disabled"
    )
}