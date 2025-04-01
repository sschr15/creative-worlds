package com.sschr15.mods.af25.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sschr15.mods.af25.commands.CustomCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.LevelCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelCommand.class)
public class LevelCommandMixin {
	@Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;", remap = false))
	private static LiteralCommandNode<CommandSourceStack> sschr15_af25$copyRegistration(CommandDispatcher<CommandSourceStack> instance, LiteralArgumentBuilder<CommandSourceStack> command) {
		LiteralCommandNode<CommandSourceStack> registered = instance.register(command);
		CustomCommands.levelCommand = registered;
		return registered;
	}
}
