package com.sschr15.mods.af25.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Text;
import net.minecraft.server.commands.LevelCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.ApiStatus;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CustomCommands {
	@ApiStatus.Internal
	public static LiteralCommandNode<CommandSourceStack> levelCommand;

	private static boolean isHardKnock(Holder<PlayerUnlock> unlock) {
		return unlock.equals(PlayerUnlocks.SCHOOL_OF_HARD_KNOCKS) || unlock.value().parent().map(CustomCommands::isHardKnock).orElse(false);
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection, CommandBuildContext context) {
		if (!SharedConstants.IS_RUNNING_IN_IDE) LevelCommand.register(dispatcher, context);

		dispatcher.register(literal("af25")
			.requires(source -> source.hasPermission(2))
			.then(levelCommand) // Inject built-in command to get them all in the same spot
			.then(literal("unlock")
				.then(literal("world_effect")
					.then(argument("effect", ResourceArgument.resource(context, Registries.WORLD_EFFECT)).executes(ctx -> {
						WorldEffect effect = ResourceArgument.getResource(ctx, "effect", Registries.WORLD_EFFECT).value();
						WorldData data = ctx.getSource().theGame().getWorldData();
						if (!(data instanceof PrimaryLevelData primaryLevelData)) {
							throw new SimpleCommandExceptionType(Text.literal("Internal error with level data")).create();
						}

						primaryLevelData.unlockEffect(effect);
						return Command.SINGLE_SUCCESS;
					}))
					.then(literal("all").executes(ctx -> {
						WorldData data = ctx.getSource().theGame().getWorldData();
						if (!(data instanceof PrimaryLevelData primaryLevelData)) {
							throw new SimpleCommandExceptionType(Text.literal("Internal error with level data")).create();
						}

						for (WorldEffect effect : BuiltInRegistries.WORLD_EFFECT) {
							primaryLevelData.unlockEffect(effect);
						}

						return Command.SINGLE_SUCCESS;
					}))
				).then(literal("special_mine")
					.then(argument("mine", ResourceArgument.resource(context, Registries.SPECIAL_MINE)).executes(ctx -> {
						SpecialMine mine = ResourceArgument.getResource(ctx, "mine", Registries.SPECIAL_MINE).value();
						WorldData data = ctx.getSource().theGame().getWorldData();
						if (!(data instanceof PrimaryLevelData primaryLevelData)) {
							throw new SimpleCommandExceptionType(Text.literal("Internal error with level data")).create();
						}

						primaryLevelData.unlockSpecialMine(mine);
						return Command.SINGLE_SUCCESS;
					}))
					.then(literal("all").executes(ctx -> {
						WorldData data = ctx.getSource().theGame().getWorldData();
						if (!(data instanceof PrimaryLevelData primaryLevelData)) {
							throw new SimpleCommandExceptionType(Text.literal("Internal error with level data")).create();
						}

						for (SpecialMine mine : BuiltInRegistries.SPECIAL_MINE) {
							primaryLevelData.unlockSpecialMine(mine);
						}

						return Command.SINGLE_SUCCESS;
					}))
				).then(literal("player_ability")
					.then(argument("player", EntityArgument.player())
						.then(argument("unlock", ResourceArgument.resource(context, Registries.PLAYER_UNLOCK)).executes(ctx -> {
							Holder<PlayerUnlock> unlock = ResourceArgument.getResource(ctx, "unlock", Registries.PLAYER_UNLOCK);
							ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
							player.forceUnlock(unlock);
							return Command.SINGLE_SUCCESS;
						}))
						.then(literal("all")
							.then(Commands.argument("bad", BoolArgumentType.bool()).executes(ctx -> {
								ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
								boolean includeBad = BoolArgumentType.getBool(ctx, "bad");
								BuiltInRegistries.PLAYER_UNLOCK.streamElements()
									.filter(ref -> includeBad || !isHardKnock(ref))
									.forEach(player::forceUnlock);
								return Command.SINGLE_SUCCESS;
							}))
							.executes(ctx -> {
								ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
								BuiltInRegistries.PLAYER_UNLOCK.streamElements()
									.filter(ref -> !isHardKnock(ref))
									.forEach(player::forceUnlock);
								return Command.SINGLE_SUCCESS;
							})
						)
					)
				)
			)
		);
	}
}
