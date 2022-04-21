package dev.s0vi.shakemyhand.client;

import dev.s0vi.shakemyhand.client.ui.TooltipSupplier;
import dev.s0vi.shakemyhand.config.ClientConfig;
import dev.s0vi.shakemyhand.config.ConfigManager;
import dev.s0vi.shakemyhand.mixin.MultiplayerScreenAccessor;
import dev.s0vi.shakemyhand.mixin.ScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ShakeMyHandClient implements ClientModInitializer {
    private final FabricLoaderImpl loader = FabricLoaderImpl.InitHelper.get();
    private final ModManager modManager = new ModManager(loader.getGameDir().resolve("shakemyhand").toFile(), loader.getGameDir().resolve("mods").toFile());
    private final ConfigManager<ClientConfig> clientConfigManager = new ConfigManager<>(ClientConfig.class, ClientConfig::new);

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if(screen instanceof MultiplayerScreen) {
                List<ClickableWidget> buttons = Screens.getButtons(screen);
                buttons.forEach(button -> {
                    if(buttonHasText(button, "selectServer.edit")) {
                        int x = ((MultiplayerScreenAccessor)screen).getServerListWidget().getRowWidth() + ((ScreenAccessor)screen).getWidth() / 2 + 5;
                        int y = -999; //this starts off-screen, but is updated every tick
                        int width = button.getWidth();
                        int height = button.getHeight();
                        TranslatableText text = new TranslatableText("shakemyhand.ui.modsButton");
                        ButtonWidget.PressAction pressAction = (self) -> {

                        };
                        TooltipSupplier tooltipSupplier = new TooltipSupplier(new TranslatableText("shakemyhand.ui.modsButton.tooltip"));


                        ButtonWidget modsButton = new ButtonWidget(x, y, width, height, text, pressAction, tooltipSupplier);
                        buttons.add(modsButton);
                    }
                });
            }
        });
    }

    public static boolean buttonHasText(ClickableWidget button, String translationKey) {
        Text text = button.getMessage();
        return text instanceof TranslatableText && ((TranslatableText)text).getKey().equals(translationKey);
    }
}
