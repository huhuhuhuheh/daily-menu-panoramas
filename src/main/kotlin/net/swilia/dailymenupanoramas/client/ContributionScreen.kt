package net.swilia.dailymenupanoramas.client

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import java.net.URI

class ContributionScreen(private val parent: Screen?) : Screen(Text.translatable("text.daily_menu_panoramas.contrib.title")) {

    private var page = 0

    override fun init() {
        this.clearChildren()

        val midX = this.width / 2
        val bottomY = this.height - 40

        if (page == 0) {
            this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.daily_menu_panoramas.contrib.source")) {
                    openLink("https://github.com/huhuhuhuheh/daily-menu-panoramas")
                }
                    .dimensions(midX - 105, bottomY - 20, 100, 20)
                    .build()
            )

            this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.daily_menu_panoramas.contrib.next")) {
                    page = 1
                    this.init()
                }
                    .dimensions(midX + 5, bottomY - 20, 100, 20)
                    .build()
            )

        } else {
            this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.daily_menu_panoramas.contrib.kofi")) {
                    openLink("https://ko-fi.com/whyiexist")
                }
                    .dimensions(midX - 105, bottomY - 70, 100, 20)
                    .build()
            )

            this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.daily_menu_panoramas.contrib.paypal")) {
                    openLink("https://www.paypal.com/paypalme/ehwiththings")
                }
                    .dimensions(midX + 5, bottomY - 70, 100, 20)
                    .build()
            )

            this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.daily_menu_panoramas.contrib.dont_show")) {
                    ContributionConfig.disableContributionScreen = true
                    ContributionConfig.save()
                    client?.setScreen(parent)
                }
                    .dimensions(midX - 105, bottomY - 45, 210, 20)
                    .build()
            )

            this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.daily_menu_panoramas.contrib.continue")) {
                    client?.setScreen(parent)
                }
                    .dimensions(midX - 105, bottomY - 20, 210, 20)
                    .build()
            )
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderPanoramaBackground(context, delta)
        super.render(context, mouseX, mouseY, delta)

        val midX = this.width / 2
        val topY = 40
        val maxWidth = 300

        drawCenteredText(context, this.title, midX, 15, Formatting.GOLD, Formatting.BOLD)

        val rawText = if (page == 0) {
            Text.translatable("text.daily_menu_panoramas.contrib.text_p1")
        } else {
            Text.translatable("text.daily_menu_panoramas.contrib.text_p2")
        }

        val lines = textRenderer.wrapLines(rawText, maxWidth)

        var currentY = topY
        for (line in lines) {
            context.drawCenteredTextWithShadow(textRenderer, line, midX, currentY, 0xFFFFFFFF.toInt())
            currentY += 12
        }
    }

    private fun drawCenteredText(context: DrawContext, text: Text, x: Int, y: Int, vararg formats: Formatting) {
        val formattedText = text.copy().formatted(*formats)
        context.drawCenteredTextWithShadow(textRenderer, formattedText, x, y, 0xFFFFFFFF.toInt())
    }

    private fun openLink(url: String) {
        try {
            Util.getOperatingSystem().open(URI(url))
        } catch (_: Exception) {}
    }

    override fun close() {
        client?.setScreen(parent)
    }
}