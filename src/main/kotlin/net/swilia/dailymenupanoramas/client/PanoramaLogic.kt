package net.swilia.dailymenupanoramas.client

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.AbstractTexture
//? if <1.21.6 {
/*import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.texture.TextureManager
*///?}
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate
import java.time.Month
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipFile
import kotlin.random.Random
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

object PanoramaLogic {
    private const val MOD_ID = "daily_menu_panoramas"
    private const val NUM_NUMERIC_PANORAMAS = 37
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

        PanoramaConfig.load()

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
                val numericPeriod = if (PanoramaConfig.shouldBeRandomInsteadOfOneAtAday) {
                    Period.RANDOM
                } else {
                    when (PanoramaConfig.periodic) {
                        "weekly" -> Period.WEEKLY
                        "monthly" -> Period.MONTHLY
                        else -> Period.DAILY
                    }
                }
                val id = safeGetNumericPanoramaIdForPeriod(numericPeriod)
                LOGGER.info("Numeric panorama resolved → $id")
                if (hasAllPanoramaFaces(id)) {
                    LOGGER.info("Numeric panorama exists and is used")
                    return id
                }
                LOGGER.warn("Numeric panorama missing faces, using fallback winter panorama")
                return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/winter/panorama")
            }
            LOGGER.info("Season panorama returned → $season")
            return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/$season/panorama")
        } else {
            val season = monthToSeason(month, false)
            LOGGER.info("Season (global) resolved → $season")
            if (season == "numeric") {
                if (PanoramaConfig.resourcePackMode != "disabled" && PanoramaConfig.resourcePackMode != "only_active") {
                    getResourcePackPanorama().let { pack ->
                        if (pack != null) {
                            LOGGER.info("Resource pack panorama used → $pack (mode ${PanoramaConfig.resourcePackMode})")
                            return pack
                        }
                        LOGGER.info("No resource pack panorama found, falling back (mode ${PanoramaConfig.resourcePackMode})")
                    }
                }
                val numericPeriod = if (PanoramaConfig.shouldBeRandomInsteadOfOneAtAday) {
                    Period.RANDOM
                } else {
                    when (PanoramaConfig.periodic) {
                        "weekly" -> Period.WEEKLY
                        "monthly" -> Period.MONTHLY
                        else -> Period.DAILY
                    }
                }
                val id = safeGetNumericPanoramaIdForPeriod(numericPeriod)
                LOGGER.info("Numeric panorama resolved → $id")
                if (hasAllPanoramaFaces(id)) {
                    LOGGER.info("Numeric panorama exists and is used")
                    return id
                }
                LOGGER.warn("Numeric panorama missing faces, using fallback winter panorama")
                return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/winter/panorama")
            }
            LOGGER.info("Season panorama returned → $season")
            return Identifier.of(MOD_ID, "textures/gui/title/background/seasons/$season/panorama")
        }
    }

    private data class PackCandidate(val ns: String, val path: String, val owner: String)

private var cachedResourcePackRadar: List<PackCandidate>? = null
    private val registeredPackPanoramas = mutableSetOf<String>()

    /* Register resource pack panorama to the manager */
    private fun ensurePackFacesRegistered(candidate: PackCandidate) {
        //? if <1.21.6 {
        /*val key = "${candidate.owner}|${candidate.ns}:${candidate.path}"
        if (registeredPackPanoramas.contains(key)) return

        val packFolder = FabricLoader.getInstance().getGameDir().resolve("resourcepacks").resolve(candidate.owner).toFile()
        val assetsBase = packFolder.resolve("assets/${candidate.ns}")
        val probeFolders = listOf(
            assetsBase.resolve(candidate.path),
            assetsBase.resolve(candidate.path.removeSuffix("/panorama")),
        )
        val listDir = probeFolders.firstOrNull { it.resolve("panorama_0.png").exists() }
        if (listDir == null) {
            LOGGER.warn("Pack panorama folder not found for register: ${packFolder.absolutePath}")
            return
        }

        val identifier = Identifier.of(candidate.ns, candidate.path)
        val tm = MinecraftClient.getInstance().textureManager
        var registeredAny = false
        for (i in 0 until 6) {
            try {
                val file = listDir.resolve("panorama_$i.png")
                if (!file.exists()) continue
                val img = NativeImage.read(file.inputStream())
                val tex = NativeImageBackedTexture({ "dmp-pack-${i}" }, img)
                tm.registerTexture(Identifier.of(identifier.namespace, "${identifier.path}_$i"), tex)
                registeredAny = true
            } catch (_: Exception) { }
        }
        if (registeredAny) {
            registeredPackPanoramas.add(key)
            LOGGER.info("Registered resource pack panorama faces '$key' (dir '${listDir.absolutePath}')")
        }
        *///?}
    }

    fun ensureCurrentPackRegistered() {
        registerCurrentPack()
    }

    fun invalidatePackPanoramaCache() {
        registeredPackPanoramas.clear()
        loadedPackFaceTextures.clear()
    }

    private fun selectPackCandidate(): PackCandidate? {
        val radar = resourcePackPanoramaRadar()
        if (radar.isEmpty()) return null
        return when (PanoramaConfig.resourcePackMode) {
            "random" -> radar.random()
            "always" -> {
                val day = LocalDate.now().toEpochDay()
                radar.minByOrNull { c -> seedHash("${c.ns}:${c.path}", day) } ?: radar.first()
            }
            "only_active", "always_active" -> radar.filter { packHasFacesLoaded(it) }.randomOrNull()
            else -> null
        }
    }

    private fun registerCurrentPack() {
        val candidate = selectPackCandidate() ?: return
        ensurePackFacesRegistered(candidate)
    }

    private val loadedPackFaceTextures = ConcurrentHashMap<Identifier, AbstractTexture>()

    fun getPackPanoramaId(): Identifier? {
        val mode = PanoramaConfig.resourcePackMode
        if (mode == "disabled" || mode == "only_active") return null
        val candidate = if (mode == "always_active") {
            selectPackCandidate()?.takeIf { packHasFacesLoaded(it) }
        } else {
            selectPackCandidate()
        } ?: return null
        return Identifier.of(candidate.ns, candidate.path)
    }

    fun preloadPackFaceTextures() {
        //? if <1.21.6 {
        /*val candidate = selectPackCandidate() ?: return
        val packFolder = FabricLoader.getInstance().getGameDir().resolve("resourcepacks").resolve(candidate.owner).toFile()
        val assetsBase = packFolder.resolve("assets/${candidate.ns}")
        val probes = listOf(
            assetsBase.resolve(candidate.path),
            assetsBase.resolve(candidate.path.removeSuffix("/panorama")),
        )
        val listDir = probes.firstOrNull { it.resolve("panorama_0.png").exists() }
        if (listDir == null) {
            LOGGER.warn("Pack panorama folder not found for preload: ${packFolder.absolutePath}")
            return
        }

        for (i in 0 until 6) {
            val id = Identifier.of(candidate.ns, "${candidate.path}_$i.png")
            if (loadedPackFaceTextures.containsKey(id)) continue
            try {
                val file = listDir.resolve("panorama_$i.png")
                if (!file.exists()) continue
                val img = NativeImage.read(file.inputStream())
                loadedPackFaceTextures[id] = NativeImageBackedTexture({ "dmp-pack-$i" }, img)
            } catch (e: Exception) {
                LOGGER.warn("pack face preload {} failed: {}", id, e.message)
            }
        }
        LOGGER.info("Preloaded {} pack panorama faces (pack '{}')", loadedPackFaceTextures.size, candidate.owner)
        *///?}
    }

    fun ensurePackFaceTexture(id: Identifier): AbstractTexture {
        loadedPackFaceTextures[id]?.let { return it }
        return MinecraftClient.getInstance().textureManager.getTexture(id)
    }

    fun packFaceFileForIndex(index: String): File? {
        val candidate = selectPackCandidate() ?: return null
        val i = index.toIntOrNull() ?: return null
        if (i < 0 || i > 5) return null
        val packFolder = FabricLoader.getInstance().getGameDir().resolve("resourcepacks").resolve(candidate.owner).toFile()
        val assetsBase = packFolder.resolve("assets/${candidate.ns}")
        val probes = listOf(
            assetsBase.resolve(candidate.path),
            assetsBase.resolve(candidate.path.removeSuffix("/panorama")),
        )
        val listDir = probes.firstOrNull { it.resolve("panorama_0.png").exists() }
        val file = listDir?.resolve("panorama_$i.png")
        return file?.takeIf { it.exists() }
    }

    fun packFaceForIndex(packId: Identifier, id: Identifier): Identifier? {
        val base = id.path.removeSuffix(".png")
        val idx = base.indexOf('_')
        if (idx < 0) return null
        val suffix = base.substring(idx + 1)
        if (!suffix.all { it.isDigit() }) return null
        val face = Identifier.of(packId.namespace, "${packId.path}_$suffix.png")
        return face.takeIf { loadedPackFaceTextures.containsKey(it) }
    }

    /* Panorama chooser for resource packs */
    private fun resourcePackPanoramaRadar(): List<PackCandidate> {
        cachedResourcePackRadar?.let { return it }

        val results = LinkedHashMap<Pair<String, String>, PackCandidate>()
        val ANCHOR = "textures/gui/title/background/"

        fun registerTexture(resourcePath: String, owner: String) {
            if (!resourcePath.startsWith("assets/")) {
                LOGGER.debug("Panorama register '${resourcePath}' rejected: no assets/ prefix")
                return
            }
            val ns = resourcePath.split("/").getOrNull(1) ?: return
            if (ns == MOD_ID) {
                LOGGER.debug("Panorama register '${resourcePath}' rejected: own mod namespace")
                return
            }

            val tail = resourcePath.substringAfter("assets/$ns/")
            if (tail.contains("..")) {
                LOGGER.debug("Panorama register '${resourcePath}' rejected: path traversal")
                return
            }

            val isFolder = tail.endsWith("/panorama")
            val isFlat = tail.substringAfterLast("/").startsWith("panorama_") &&
                tail.substringBeforeLast("/").endsWith("/background")
            if (!isFolder && !isFlat) {
                LOGGER.debug("Panorama register '${resourcePath}' skipped (ns='$ns', tail='$tail', not folder/flat)")
                return
            }

            val folder = if (isFolder) {
                tail
            } else {
                val anchor = tail.lastIndexOf(ANCHOR)
                if (anchor < 0) {
                    LOGGER.debug("Panorama register '${resourcePath}' rejected: no anchor")
                    return
                }
                tail.substring(0, anchor + ANCHOR.length).trimEnd('/') + "/panorama"
            }

            results[ns to folder] = PackCandidate(ns, folder, owner)
            LOGGER.info("Panorama radar → '$owner' provides '$ns:$folder'")
        }

        fun handleDir(dir: File, owner: String) {
            if (!dir.isDirectory) return
            dir.walkTopDown().forEach { file ->
                if (!file.isFile) return@forEach
                val name = file.name.lowercase()
                if (!(name.startsWith("panorama_") && name.endsWith(".png"))) return@forEach
                val fullPath = file.absolutePath.replace('\\', '/')
                val assetsIndex = fullPath.lastIndexOf("/assets")
                if (assetsIndex < 0) return@forEach
                registerTexture(fullPath.substring(assetsIndex + 1), owner)
            }
        }

        fun handleZip(zipFile: File, owner: String) {
            try {
                ZipFile(zipFile).use { zip ->
                    zip.entries().asSequence().forEach { entry ->
                        if (entry.isDirectory) return@forEach
                        val name = entry.name
                        if (!name.endsWith(".png")) return@forEach
                        val base = name.substringBeforeLast("/")
                        val leaf = base.substringAfterLast("/")
                        if (!leaf.startsWith("panorama_")) return@forEach
                        registerTexture(base, owner)
                    }
                }
            } catch (_: Exception) {
                LOGGER.warn("Could not read resource pack zip '$owner'")
            }
        }

        val packsDir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks").toFile()
        if (packsDir.isDirectory) {
            packsDir.listFiles()?.forEach { pack ->
                if (pack.name.equals("excluded", ignoreCase = true)) return@forEach
                val owner = pack.name
                if (pack.isDirectory) {
                    handleDir(pack, owner)
                } else if (pack.isFile && pack.name.endsWith(".zip", ignoreCase = true)) {
                    handleZip(pack, owner)
                }
            }
        } else {
            LOGGER.warn("Resource pack folder not found: $packsDir")
        }

        val result = results.values.toList()
        cachedResourcePackRadar = result
        LOGGER.info("Resource pack panorama radar complete → ${result.size} candidates")
        return result
    }

    private fun getResourcePackPanorama(): Identifier? {
        val mode = PanoramaConfig.resourcePackMode
        val radar = resourcePackPanoramaRadar()
        if (radar.isEmpty()) return null

        val requireLoaded = mode == "always_active" || mode == "only_active"
        val pool = if (requireLoaded) {
            radar.filter { packHasFacesLoaded(it) }
        } else {
            radar
        }
        LOGGER.info("Resource pack panorama mode → ${mode.uppercase()} (${pool.size} usable from ${radar.size} scanned)")
        if (pool.isEmpty()) return null

        val candidate = when (mode) {
            "random" -> pool.random()
            "always" -> {
                val day = LocalDate.now().toEpochDay()
                pool.minByOrNull { c -> seedHash("${c.ns}:${c.path}", day) } ?: pool.first()
            }
            else -> pool.random()
        }
        val id = Identifier.of(candidate.ns, candidate.path)
        LOGGER.info("Resource pack panorama chosen → $id")
        return id
    }

    private fun seedHash(key: String, base: Long): Long {
        var h = base
        for (c in key) {
            h = (h * 31 + c.code) and Long.MAX_VALUE
        }
        return h
    }

    private fun packHasFacesLoaded(candidate: PackCandidate): Boolean {
        val rm = MinecraftClient.getInstance().resourceManager
        for (i in 0 until 6) {
            try {
                if (rm.getResource(Identifier.of(candidate.ns, "${candidate.path}_$i.png")).isPresent) return true
            } catch (_: Exception) { }
        }
        return false
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

    private fun hasAllPanoramaFaces(id: Identifier): Boolean {
        val client = MinecraftClient.getInstance() ?: return false
        for (i in 0 until 6) {
            val faceId = Identifier.of(id.namespace, "${id.path}_$i.png")
            try {
                if (!client.resourceManager.getResource(faceId).isPresent) {
                    LOGGER.warn("Panorama face missing → {}", faceId)
                    return false
                }
            } catch (e: Exception) {
                LOGGER.warn("Panorama face check failed → {} ({})", faceId, e.message)
                return false
            }
        }
        return true
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