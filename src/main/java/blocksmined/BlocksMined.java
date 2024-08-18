package blocksmined;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Formatting;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.*;

public class BlocksMined implements ModInitializer {

	@Override
	public void onInitialize() {

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (player.getServer() != null)
				addScore(player.getServer(), (ServerPlayerEntity) player);
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
					dispatcher.register(literal("bmcolor")
							.then(argument("color", ColorArgumentType.color())
									.executes(context -> {
										final Formatting value = ColorArgumentType.getColor(context, "color");
										final String result = value.asString();

										Scoreboard scoreboard = context.getSource().getServer().getScoreboard();

										if (Objects.equals(result, "red")) {
											Objects.requireNonNull(scoreboard.getNullableObjective("blocksMined")).setNumberFormat(StyledNumberFormat.RED);
											context.getSource().sendFeedback(() -> Text.literal("Changed scoreboard color to red").formatted(Formatting.RED), true);
										} else if (Objects.equals(result, "yellow")) {
											Objects.requireNonNull(scoreboard.getNullableObjective("blocksMined")).setNumberFormat(StyledNumberFormat.YELLOW);
											context.getSource().sendFeedback(() -> Text.literal("Changed scoreboard color to red").formatted(Formatting.YELLOW), true);
										} else {
											context.getSource().sendFeedback(() -> Text.literal("Currently only yellow and red are supported").formatted(Formatting.RED, Formatting.BOLD), false);
										}

										return 1;
									}))
					);
					dispatcher.register(literal("bmreset")
							.requires(source -> source.hasPermissionLevel(2))
									.executes(context -> {

										Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
										ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

										if (objective == null) {
											context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
											return 1;
										}


											scoreboard.removeObjective(objective);
											scoreboard.addObjective(
													"blocksMined",
													ScoreboardCriterion.DUMMY,
													Text.literal("Blocks Mined"),
													ScoreboardCriterion.RenderType.INTEGER,
													true,
													StyledNumberFormat.RED);




										return 1;
					}));
					dispatcher.register(literal("bmdisplay")
							.requires(source -> source.hasPermissionLevel(2))
							.then(argument("position", StringArgumentType.string())
									.executes(context -> {
										final String value = StringArgumentType.getString(context, "position");

										Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
										ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

										if (objective == null) {
											context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
											return 1;
										}

										if (Objects.equals(value, "sidebar")) {
											scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
										} else if (Objects.equals(value, "list")) {
											scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.LIST, objective);
										} else if (Objects.equals(value, "belowName") || Objects.equals(value, "belowname")) {
											scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.BELOW_NAME, objective);
										}

										return 1;
									}))
					);
					dispatcher.register(literal("bmaddscore")
							.requires(source -> source.hasPermissionLevel(2))
							.then(argument("player", ScoreHolderArgumentType.scoreHolder())
									.then(argument("amount", IntegerArgumentType.integer())
										.executes(context -> {
											final ScoreHolder player = ScoreHolderArgumentType.getScoreHolder(context, "player");
											final int amount = IntegerArgumentType.getInteger(context, "amount");

											Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
											ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

											if (objective == null) {
												context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
												return 1;
											}

											ScoreAccess blocksMinedScore = scoreboard.getOrCreateScore(player, objective, true);
											ReadableScoreboardScore currentScore = scoreboard.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), objective);

											if (currentScore != null)
												blocksMinedScore.setScore(currentScore.getScore() + amount);
											else
												blocksMinedScore.setScore(amount);

											return 1;
										})))
					);
					dispatcher.register(literal("bmremovescore")
						.requires(source -> source.hasPermissionLevel(2))
						.then(argument("player", ScoreHolderArgumentType.scoreHolder())
							.then(argument("amount", IntegerArgumentType.integer())
								.executes(context -> {
									final ScoreHolder player = ScoreHolderArgumentType.getScoreHolder(context, "player");
									final int amount = IntegerArgumentType.getInteger(context, "amount");

									Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
									ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

									if (objective == null) {
										context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
										return 1;
									}

									ScoreAccess blocksMinedScore = scoreboard.getOrCreateScore(player, objective, true);
									ReadableScoreboardScore currentScore = scoreboard.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), objective);

									if (currentScore != null)
										blocksMinedScore.setScore(currentScore.getScore() - amount);
									else
										context.getSource().sendFeedback(() -> Text.literal("Score can't be below zero").formatted(Formatting.RED), false);

									return 1;
								})))
			);
			}
		);
	}

	public static void addScore(MinecraftServer server, ServerPlayerEntity player) {
		Scoreboard scoreboard = server.getScoreboard();
		ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

		if (objective == null) {
			objective = scoreboard.addObjective(
					"blocksMined",
					ScoreboardCriterion.DUMMY,
					Text.literal("Blocks Mined"),
					ScoreboardCriterion.RenderType.INTEGER,
					true,
					StyledNumberFormat.RED
			);
			scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
		}

		ScoreAccess blocksMinedScore = scoreboard.getOrCreateScore(player, objective, true);
		ReadableScoreboardScore currentScore = scoreboard.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), objective);
		if (currentScore != null)
			blocksMinedScore.setScore(currentScore.getScore() + 1);
		else
			blocksMinedScore.setScore(1);
	}
}