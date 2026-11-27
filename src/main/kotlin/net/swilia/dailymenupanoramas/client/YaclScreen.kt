package net.swilia.dailymenupanoramas.client

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

private const val PREFIX = "text.daily_menu_panoramas.config"

private fun key(suffix: String) = Text.translatable("$PREFIX.$suffix")
private fun desc(suffix: String) = OptionDescription.of(key("$suffix.desc"))

private fun randomOption(): Option<Boolean> =
    Option.createBuilder<Boolean>()
        .name(key("random"))
        .description(desc("random"))
        .binding(
            false,
            { PanoramaConfig.shouldBeRandomInsteadOfOneAtAday },
            { PanoramaConfig.shouldBeRandomInsteadOfOneAtAday = it }
        )
        .controller { BooleanControllerBuilder.create(it).coloured(true).yesNoFormatter() }
        .build()

private fun periodOption(): Option<String> =
    Option.createBuilder<String>()
        .name(key("periodic"))
        .description(desc("periodic"))
        .binding(
            "daily",
            { PanoramaConfig.periodic },
            { PanoramaConfig.periodic = it }
        )
        .controller {
            CyclingListControllerBuilder.create<String>(it)
                .values(PanoramaConfig.VALID_PERIODS)
                .valueFormatter { value ->
                    val flag = when (value) {
                        "daily" -> key("periodic.daily")
                        "weekly" -> key("periodic.weekly")
                        else -> key("periodic.monthly")
                    }
                    key("periodic.value").copy().append(Text.literal(" ")).append(flag)
                }
        }
        .build()

private fun resourcePackOption(): Option<String> =
    Option.createBuilder<String>()
        .name(key("resourcepack"))
        .description(desc("resourcepack"))
        .binding(
            "disabled",
            { PanoramaConfig.resourcePackMode },
            { PanoramaConfig.resourcePackMode = it }
        )
        .controller {
            CyclingListControllerBuilder.create<String>(it)
                .values(PanoramaConfig.VALID_RESOURCE_PACK_MODES)
                .valueFormatter { value ->
                    when (value) {
                        "random" -> key("resourcepack.random")
                        "always" -> key("resourcepack.always")
                        "always_active" -> key("resourcepack.always_active")
                        "only_active" -> key("resourcepack.only_active")
                        else -> key("resourcepack.disabled")
                    }
                }
        }
        .build()

fun buildYaclScreen(parent: Screen): Screen =
    YetAnotherConfigLib.createBuilder()
        .title(key("title"))
        .category(
            ConfigCategory.createBuilder()
                .name(key("category"))
                .group(
                    OptionGroup.createBuilder()
                        .name(key("group"))
                        .description(desc("group"))
                        .option(randomOption())
                        .option(periodOption())
                        .option(resourcePackOption())
                        .build()
                )
                .build()
        )
        .save { PanoramaConfig.save(); PanoramaLogic.invalidateNumericCache() }
        .build()
        .generateScreen(parent)