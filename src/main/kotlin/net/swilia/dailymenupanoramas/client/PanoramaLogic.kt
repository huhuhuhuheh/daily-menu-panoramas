package net.swilia.dailymenupanoramas.client

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

object PanoramaLogic {
    private const val MOD_ID = "daily_menu_panoramas"
    private const val NUM_NUMERIC_PANORAMAS = 11
    private const val MAX_SCAN_INDEX = 1000
    private const val MAX_CONSECUTIVE_MISSES = 20
    private var cachedNumericIndices: List<Int>? = null
    private val LOGGER = LoggerFactory.getLogger("DailyMenuPanoramas")

    enum class Period {
        DAILY,
        WEEKLY,
        MONTHLY,
        RANDOM
    }

    @JvmStatic
    @JvmOverloads
    fun getPanoramaId(
        global: Boolean = true,
        southernHemisphere: Boolean = false,
        period: Period = Period.DAILY
    ): Identifier {
        val now = LocalDate.now() // For testing dates, change LocalDate.now() to LocalDate.of() followed by its date
        val month = now.month
        val day = now.dayOfMonth

        LOGGER.info("Panorama request → global=$global hemisphereSouth=$southernHemisphere period=$period")

        if ((month == Month.DECEMBER && day == 31) || (month == Month.JANUARY && day == 1)) {
            LOGGER.info("Event override → New Year panorama")
            return Identifier.of(MOD_ID, "textures/gui/title/background/events/newyear/panorama")
        }
        if (month == Month.DECEMBER) {
            if (day == 23) {
                LOGGER.info("Event override → Christmas 23 panorama")
                return Identifier.of(MOD_ID, "textures/gui/title/background/events/christmas_23/panorama")
            }
            if (day == 24) {
                LOGGER.info("Event override → Christmas 24 panorama")
                return Identifier.of(MOD_ID, "textures/gui/title/background/events/christmas_24/panorama")
            }
            if (day == 25) {
                LOGGER.info("Event override → Christmas 25 panorama")
                return Identifier.of(MOD_ID, "textures/gui/title/background/events/christmas_25/panorama")
            }
        }
        if (month == Month.OCTOBER && day == 31) {
            LOGGER.info("Event override → Halloween panorama")
            return Identifier.of(MOD_ID, "textures/gui/title/background/events/halloween/panorama")
        }

        if (!global) {
            val season = monthToSeason(month, southernHemisphere)
            LOGGER.info("Season (local) resolved → $season")
            if (season == "numeric") {
                val id = safeGetNumericPanoramaIdForPeriod(Period.RANDOM)
                LOGGER.info("Numeric panorama resolved → $id")
                val client = MinecraftClient.getInstance()
                if (client != null) {
                    try {
                        client.resourceManager.getResource(id).ifPresent { }
                        LOGGER.info("Numeric panorama exists and is used")
                        return id
                    } catch (_: Exception) {
                        LOGGER.warn("Numeric panorama missing, using fallback winter panorama")
                        return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/winter/panorama")
                    }
                }
                return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/winter/panorama")
            }
            LOGGER.info("Season panorama returned → $season")
            return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/$season/panorama")
        } else {
            val season = monthToSeason(month, false)
            LOGGER.info("Season (global) resolved → $season")
            if (season == "numeric") {
                val id = safeGetNumericPanoramaIdForPeriod(Period.RANDOM)
                LOGGER.info("Numeric panorama resolved → $id")
                val client = MinecraftClient.getInstance()
                if (client != null) {
                    try {
                        client.resourceManager.getResource(id).ifPresent { }
                        LOGGER.info("Numeric panorama exists and is used")
                        return id
                    } catch (_: Exception) {
                        LOGGER.warn("Numeric panorama missing, using fallback winter panorama")
                        return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/winter/panorama")
                    }
                }
                return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/winter/panorama")
            }
            LOGGER.info("Season panorama returned → $season")
            return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/$season/panorama")
        }
    }

    private fun monthToSeason(month: Month, southernHemisphere: Boolean): String {
        val season = when (month) {
            Month.DECEMBER, Month.JANUARY -> "winter"
            Month.JUNE, Month.JULY, Month.AUGUST -> "summer"
            else -> "numeric"
        }
        return if (southernHemisphere) invertSeason(season) else season
    }

    private fun invertSeason(northernSeason: String): String {
        return when (northernSeason) {
            "winter" -> "summer"
            "summer" -> "winter"
            else -> northernSeason
        }
    }

    private fun discoverNumericPanoramaIndices(): List<Int> {
        cachedNumericIndices?.let {
            LOGGER.info("Using cached numeric panorama list → $it")
            return it
        }

        LOGGER.info("Scanning for numeric panoramas up to $MAX_SCAN_INDEX with max $MAX_CONSECUTIVE_MISSES misses")

        val client = MinecraftClient.getInstance() ?: return emptyList()
        val rm = client.resourceManager

        val found = mutableListOf<Int>()
        var consecutiveMisses = 0
        var i = 1

        while (i <= MAX_SCAN_INDEX && consecutiveMisses < MAX_CONSECUTIVE_MISSES) {
            var exists = false
            val base = "textures/gui/title/background/numeric/$i/panorama"
            val candidates = listOf("$base.png", "$base", "${base}_0.png", "${base}_1.png", "${base}_2.png", "${base}_3.png", "${base}_4.png", "${base}_5.png")

            for (candidate in candidates) {
                val id = Identifier.of(MOD_ID, candidate)
                try {
                    if (rm.getResource(id).isPresent) {
                        exists = true
                        break
                    }
                } catch (_: Exception) { }
            }

            if (exists) {
                LOGGER.info("Found numeric panorama index → $i")
                found.add(i)
                consecutiveMisses = 0
            } else {
                consecutiveMisses++
                LOGGER.info("Numeric panorama index $i missing ($consecutiveMisses misses)")
            }

            i++
        }

        val result = if (found.isNotEmpty()) found else (1..NUM_NUMERIC_PANORAMAS).toList()
        LOGGER.info("Numeric panorama scan complete → $result")

        cachedNumericIndices = result
        return result
    }

    private fun safeGetNumericPanoramaIdForPeriod(period: Period): Identifier {
        val indices = try {
            discoverNumericPanoramaIndices()
        } catch (_: Exception) {
            LOGGER.error("Failed to discover numeric panoramas, fallback to index 1")
            emptyList()
        }

        if (indices.isEmpty()) {
            LOGGER.warn("No numeric panoramas found, fallback index=1")
            return Identifier.of(MOD_ID, "textures/gui/title/background/numeric/1/panorama")
        }

        if (period == Period.RANDOM) {
            LOGGER.info("Numeric panorama mode → RANDOM (Unseeded)")
            val choice = indices.random()
            LOGGER.info("Numeric panorama chosen → index $choice")
            return Identifier.of(MOD_ID, "textures/gui/title/background/numeric/$choice/panorama")
        }

        val now = LocalDate.now()
        val seed = when (period) {
            Period.DAILY -> now.toEpochDay()
            Period.WEEKLY -> now.toEpochDay() / 7
            Period.MONTHLY -> now.year.toLong() * 100 + now.monthValue
            else -> now.toEpochDay()
        }

        LOGGER.info("Numeric panorama seed → $seed for period $period")

        val rnd = Random(seed)
        val choice = indices[rnd.nextInt(indices.size)]

        LOGGER.info("Numeric panorama chosen → index $choice")

        return Identifier.of(MOD_ID, "textures/gui/title/background/numeric/$choice/panorama")
    }

    fun invalidateNumericCache() {
        LOGGER.info("Numeric panorama cache invalidated")
        cachedNumericIndices = null
    }
}