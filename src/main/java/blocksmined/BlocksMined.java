package blocksmined;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class BlocksMined implements ModInitializer {

	@Override
	public void onInitialize() {

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (player.getServer() != null)
				addScore(player.getServer(), (ServerPlayerEntity) player, "blocksMined");
		});
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