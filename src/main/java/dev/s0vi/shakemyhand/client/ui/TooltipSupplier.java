package dev.s0vi.shakemyhand.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public record TooltipSupplier(Text tooltip) implements ButtonWidget.TooltipSupplier {

    @Override
    public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
        if (!button.active) {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            assert currentScreen != null;

            List<OrderedText> wrappedText = MinecraftClient.getInstance().textRenderer.wrapLines(
                    tooltip,
                    Math.max(currentScreen.width / 2 - 43, 170)
            );
            currentScreen.renderOrderedTooltip(matrices, wrappedText, mouseX, mouseY);
        }
    }

    @Override
    public void supply(Consumer<Text> consumer) {
        ButtonWidget.TooltipSupplier.super.supply(consumer);
    }
}
