package blocksmined;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardSlotArgumentType;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;
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
					dispatcher.register(literal("bm")
							.requires(source -> source.hasPermissionLevel(2))
						.executes(context -> {
							context.getSource().sendFeedback(() -> Text.literal("Subcommands: color, addscore, removescore, display, reset"), false);
							return 1;
						})
						.then(literal("color")
								.executes(context -> {
									context.getSource().sendFeedback(() -> Text.literal("Please enter a color with the command"), false);
									return 1;
								}).then(argument("color", ColorArgumentType.color())
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
						).then(literal("addscore").executes(context -> {
								context.getSource().sendFeedback(() -> Text.literal("Please enter a player with the command"), false);
								return 1;
							}).then(argument("player", EntityArgumentType.players())
									.then(argument("amount", IntegerArgumentType.integer())
											.executes(context -> {
												final Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
												final int amount = IntegerArgumentType.getInteger(context, "amount");

												Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
												ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

												if (objective == null) {
													context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
													return 1;
												}

												for (ServerPlayerEntity player : players) {
													final ScoreHolder scoreHolder = ScoreHolder.fromName(player.getNameForScoreboard());

													ScoreAccess blocksMinedScore = scoreboard.getOrCreateScore(scoreHolder, objective, true);
													ReadableScoreboardScore currentScore = scoreboard.getScore(scoreHolder, objective);

													if (currentScore != null) {
														blocksMinedScore.setScore(currentScore.getScore() + amount);
													} else {
														blocksMinedScore.setScore(amount);
													}
												}

												return 1;
											})))
							).then(literal("removescore").executes(context -> {
								context.getSource().sendFeedback(() -> Text.literal("Please enter a player with the command"), false);
								return 1;
							}).then(argument("player", EntityArgumentType.players())
									.then(argument("amount", IntegerArgumentType.integer())
											.executes(context -> {
												final Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
												final int amount = IntegerArgumentType.getInteger(context, "amount");

												Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
												ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

												if (objective == null) {
													context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
													return 1;
												}

												for (ServerPlayerEntity player : players) {
													final ScoreHolder scoreHolder = ScoreHolder.fromName(player.getNameForScoreboard());

													ScoreAccess blocksMinedScore = scoreboard.getOrCreateScore(scoreHolder, objective, true);
													ReadableScoreboardScore currentScore = scoreboard.getScore(scoreHolder, objective);

													if (currentScore != null) {
														blocksMinedScore.setScore(currentScore.getScore() - amount);
													} else {
														context.getSource().sendFeedback(
																() -> Text.literal("Score can't be below zero for " + player.getName().getString()).formatted(Formatting.RED),
																false
														);
													}
												}

												return 1;
											})))
							).then(literal("reset").executes(context -> {
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
							})
							).then(literal("display").executes(context -> {
								context.getSource().sendFeedback(() -> Text.literal("Please enter a display position with the command"), false);
								return 1;
							}).then(argument("position", ScoreboardSlotArgumentType.scoreboardSlot())
									.executes(context -> {
										final ScoreboardDisplaySlot value = ScoreboardSlotArgumentType.getScoreboardSlot(context, "position");

										Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
										ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

										if (objective == null) {
											context.getSource().sendFeedback(() -> Text.literal("Scoreboard doesn't exits!").formatted(Formatting.RED), false);
											return 1;
										}

										scoreboard.setObjectiveSlot(value, objective);

										return 1;
									}))
							).then(literal("name").executes(context -> {
								context.getSource().sendFeedback(() -> Text.literal("Please enter a display position with the command"), false);
								return 1;
							}).then(argument("name", StringArgumentType.string()).executes(context -> {

									final String name = StringArgumentType.getString(context, "name");

									Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
									ScoreboardObjective objective = scoreboard.getNullableObjective("blocksMined");

									objective.setDisplayName(Text.of(name));

									return 1;
									}))
							)
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