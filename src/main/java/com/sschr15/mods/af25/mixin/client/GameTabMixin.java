package com.sschr15.mods.af25.mixin.client;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public class GameTabMixin {
    @SuppressWarnings("unchecked")
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;withValues([Ljava/lang/Object;)Lnet/minecraft/client/gui/components/CycleButton$Builder;"))
    private <T> CycleButton.Builder<T> sschr15_af25$readdGamemodes(CycleButton.Builder<T> instance, T[] values) {
        if (values[0] instanceof WorldCreationUiState.SelectedGameMode) {
            WorldCreationUiState.SelectedGameMode
                    survival = WorldCreationUiState.SelectedGameMode.SURVIVAL,
                    creative = WorldCreationUiState.SelectedGameMode.CREATIVE,
                    hardcore = WorldCreationUiState.SelectedGameMode.HARDCORE;
            return (CycleButton.Builder<T>) ((CycleButton.Builder<WorldCreationUiState.SelectedGameMode>) instance).withValues(survival, hardcore, creative);
        }

        return instance.withValues(values);
    }
}
