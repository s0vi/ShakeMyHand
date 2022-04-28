package dev.s0vi.shakemyhand.mixin;

import dev.s0vi.shakemyhand.ShakeMyHand;
import dev.s0vi.shakemyhand.client.ShakeMyHandClient;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin {
    @Shadow protected MultiplayerServerListWidget serverListWidget;

    @Shadow private ButtonWidget buttonEdit;

    @Shadow private boolean initialized;

    @Shadow private ServerInfo selectedEntry;

    @Shadow private ButtonWidget buttonDelete;

    @Inject(at = @At("TAIL"), method ="render")
    void updateSeeModsButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        List<ClickableWidget> buttons = Screens.getButtons((MultiplayerScreen) (Object) this);

        for (int i = 0; i < buttons.size(); i++) {
            ClickableWidget button = buttons.get(i);
            if(ShakeMyHandClient.buttonHasText(button, "shakemyhand.ui.modsButton")) {
                EntryListWidget.Entry<MultiplayerServerListWidget.Entry> selection = serverListWidget.getSelectedOrNull();
                button.y = ((EntryListWidgetAccessor)serverListWidget).getItemHeight() * i + ((EntryListWidgetAccessor)serverListWidget).getTop();
                ShakeMyHand.LOGGER.info(((EntryListWidgetAccessor)serverListWidget).getItemHeight());
                ShakeMyHand.LOGGER.info("Button Y: {}", button.y);

                buttons.set(i, button);
                break;
            }
        }
    }
}