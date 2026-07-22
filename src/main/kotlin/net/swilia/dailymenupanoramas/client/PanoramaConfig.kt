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

    fun load() {
        if (configFile.exists()) {
            try {
                FileReader(configFile).use { reader ->
                    val data = gson.fromJson(reader, ConfigData::class.java)
                    shouldBeRandomInsteadOfOneAtAday = data.shouldBeRandomInsteadOfOneAtAday
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
                val data = ConfigData(shouldBeRandomInsteadOfOneAtAday)
                gson.toJson(data, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private data class ConfigData(val shouldBeRandomInsteadOfOneAtAday: Boolean)
}