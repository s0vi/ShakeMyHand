package dev.s0vi.shakemyhand.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor {
    @Accessor("itemHeight")
    int getItemHeight();

    @Accessor("top")
    int getTop();
}
