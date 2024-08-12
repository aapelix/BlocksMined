package blocksmined;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.command.argument.ColorArgumentType;
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
				addScore(player.getServer(), (ServerPlayerEntity) player, "blocksMined");
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->  dispatcher.register(literal("blocksmined")
				.then(argument("color", ColorArgumentType.color())
						.executes(context -> {
							final Formatting value = ColorArgumentType.getColor(context, "color");
							final String result = value.asString();

							Scoreboard scoreboard = context.getSource().getServer().getScoreboard();


							if (Objects.equals(result, "red")) {
								Objects.requireNonNull(scoreboard.getNullableObjective("blocksMined")).setNumberFormat(StyledNumberFormat.RED);
								context.getSource().sendFeedback(() -> Text.literal("Changed scoreboard color to red").formatted(Formatting.RED), true);
							}

							else if (Objects.equals(result, "yellow")) {
								Objects.requireNonNull(scoreboard.getNullableObjective("blocksMined")).setNumberFormat(StyledNumberFormat.YELLOW);
								context.getSource().sendFeedback(() -> Text.literal("Changed scoreboard color to red").formatted(Formatting.YELLOW), true);
							} else {
								context.getSource().sendFeedback(() -> Text.literal("Currently only yellow and red are supported").formatted(Formatting.RED, Formatting.BOLD), false);
							}

							return 1;
						}))));
	}

	public static void addScore(MinecraftServer server, ServerPlayerEntity player, String objectiveName) {
		Scoreboard scoreboard = server.getScoreboard();
		ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);

		if (objective == null) {
			objective = scoreboard.addObjective(
					objectiveName,
					ScoreboardCriterion.DUMMY,
					Text.literal("Blocks Mined"),
					ScoreboardCriterion.RenderType.INTEGER,
					true,
					StyledNumberFormat.RED
			);
		}

		ScoreAccess blocksMinedScore = scoreboard.getOrCreateScore(player, objective, true);
		ReadableScoreboardScore currentScore = scoreboard.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), objective);
		if (currentScore != null)
			blocksMinedScore.setScore(currentScore.getScore() + 1);
		else
			blocksMinedScore.setScore(1);
	}
}