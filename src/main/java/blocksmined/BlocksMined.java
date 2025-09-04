package blocksmined;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ScoreboardSlotArgumentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.*;

public class BlocksMined implements ModInitializer {

	@Override
	public void onInitialize() {

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (player.getServer() != null)
				addScore(player.getServer(), (ServerPlayerEntity) player, "blocksMined", "Blocks Mined");
		});

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
       if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && player.getServer() != null) {
                ItemStack heldItem = player.getStackInHand(hand);
                if (heldItem.getItem() instanceof BlockItem blockItem) {
                    addScore(player.getServer(), serverPlayer, "blocksPlaced", "Blocks Placed");
                }
            }
            return ActionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("blocks")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> Text.literal("Usage: /blocks <mined|placed> <subcommand> [...]"), false);
                        return 1;
                    })
                    .then(argument("type", StringArgumentType.word())
                            .suggests((context, builder) -> builder.suggest("mined").suggest("placed").buildFuture())
                            .then(literal("color")
                                    .executes(ctx -> {
                                        ctx.getSource().sendFeedback(() -> Text.literal("Please enter a color"), false);
                                        return 1;
                                    })
                                    .then(argument("color", ColorArgumentType.color())
                                            .executes(ctx -> {
                                                Formatting color = ColorArgumentType.getColor(ctx, "color");
                                                String type = StringArgumentType.getString(ctx, "type");
                                                Scoreboard scoreboard = ctx.getSource().getServer().getScoreboard();
                                                ScoreboardObjective obj = scoreboard.getNullableObjective(type.equals("mined") ? "blocksMined" : "blocksPlaced");

                                                StyledNumberFormat format = switch (color) {
                                                    case RED -> StyledNumberFormat.RED;
                                                    case YELLOW -> StyledNumberFormat.YELLOW;
                                                    default -> null;
                                                };

                                                if (format != null) {
                                                    if (obj != null) obj.setNumberFormat(format);
                                                    ctx.getSource().sendFeedback(() -> Text.literal("Changed scoreboard color").formatted(color), true);
                                                } else {
                                                    ctx.getSource().sendFeedback(() -> Text.literal("Only red and yellow are supported").formatted(Formatting.RED, Formatting.BOLD), false);
                                                }
                                                return 1;
                                            })
                                    )
                            )
                            .then(literal("addscore")
                                    .executes(ctx -> {
                                        ctx.getSource().sendFeedback(() -> Text.literal("Please enter a player and amount"), false);
                                        return 1;
                                    })
                                    .then(argument("player", EntityArgumentType.players())
                                            .then(argument("amount", IntegerArgumentType.integer())
                                                    .executes(ctx -> {
                                                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                        String type = StringArgumentType.getString(ctx, "type");
                                                        for (ServerPlayerEntity player : EntityArgumentType.getPlayers(ctx, "player")) {
                                                            addManualScore(ctx.getSource().getServer(), player, type.equals("mined") ? "blocksMined" : "blocksPlaced", amount);
                                                        }
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(literal("removescore")
                                    .executes(ctx -> {
                                        ctx.getSource().sendFeedback(() -> Text.literal("Please enter a player and amount"), false);
                                        return 1;
                                    })
                                    .then(argument("player", EntityArgumentType.players())
                                            .then(argument("amount", IntegerArgumentType.integer())
                                                    .executes(ctx -> {
                                                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                        String type = StringArgumentType.getString(ctx, "type");
                                                        for (ServerPlayerEntity player : EntityArgumentType.getPlayers(ctx, "player")) {
                                                            removeScore(ctx.getSource().getServer(), player, type.equals("mined") ? "blocksMined" : "blocksPlaced", amount);
                                                        }
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(literal("reset")
                                    .executes(ctx -> {
                                        String type = StringArgumentType.getString(ctx, "type");
                                        resetObjective(ctx.getSource().getServer(), type.equals("mined") ? "blocksMined" : "blocksPlaced",
                                                type.equals("mined") ? "Blocks Mined" : "Blocks Placed");
                                        return 1;
                                    })
                            )
                            .then(literal("display")
                                    .executes(ctx -> {
                                        ctx.getSource().sendFeedback(() -> Text.literal("Please enter a display position"), false);
                                        return 1;
                                    })
                                    .then(argument("position", ScoreboardSlotArgumentType.scoreboardSlot())
                                            .executes(ctx -> {
                                                ScoreboardDisplaySlot slot = ScoreboardSlotArgumentType.getScoreboardSlot(ctx, "position");
                                                String type = StringArgumentType.getString(ctx, "type");
                                                displayObjective(ctx.getSource().getServer(), type.equals("mined") ? "blocksMined" : "blocksPlaced", slot);
                                                return 1;
                                            })
                                    )
                            )
                            .then(literal("name")
                                    .executes(ctx -> {
                                        ctx.getSource().sendFeedback(() -> Text.literal("Please enter a name"), false);
                                        return 1;
                                    })
                                    .then(argument("name", StringArgumentType.string())
                                            .executes(ctx -> {
                                                String name = StringArgumentType.getString(ctx, "name");
                                                String type = StringArgumentType.getString(ctx, "type");
                                                Scoreboard scoreboard = ctx.getSource().getServer().getScoreboard();
                                                ScoreboardObjective obj = scoreboard.getNullableObjective(type.equals("mined") ? "blocksMined" : "blocksPlaced");
                                                if (obj != null) obj.setDisplayName(Text.of(name));
                                                return 1;
                                            })
                                    )
                            )
                    )
            );
        });

    }

	public static void addScore(MinecraftServer server, ServerPlayerEntity player, String name, String title) {
        Scoreboard scoreboard = server.getScoreboard();
		ScoreboardObjective objective = scoreboard.getNullableObjective(name);

		if (objective == null) {
			objective = scoreboard.addObjective(
					name,
					ScoreboardCriterion.DUMMY,
					Text.literal(title),
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

    public static void addManualScore(MinecraftServer server, ServerPlayerEntity player, String objectiveName, int amount) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective == null) {
            objective = scoreboard.addObjective(objectiveName, ScoreboardCriterion.DUMMY, Text.literal(objectiveName), ScoreboardCriterion.RenderType.INTEGER, true, StyledNumberFormat.RED);
        }
        ScoreAccess score = scoreboard.getOrCreateScore(player, objective, true);
        ReadableScoreboardScore current = scoreboard.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), objective);
        score.setScore((current != null ? current.getScore() : 0) + amount);
    }

    public static void removeScore(MinecraftServer server, ServerPlayerEntity player, String objectiveName, int amount) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective == null) return;
        ScoreAccess score = scoreboard.getOrCreateScore(player, objective, true);
        ReadableScoreboardScore current = scoreboard.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), objective);
        if (current != null) {
            score.setScore(Math.max(0, current.getScore() - amount));
        }
    }

    public static void resetObjective(MinecraftServer server, String objectiveName, String displayName) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective != null) scoreboard.removeObjective(objective);
        scoreboard.addObjective(objectiveName, ScoreboardCriterion.DUMMY, Text.literal(displayName), ScoreboardCriterion.RenderType.INTEGER, true, StyledNumberFormat.RED);
    }

    public static void displayObjective(MinecraftServer server, String objectiveName, ScoreboardDisplaySlot slot) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective != null) scoreboard.setObjectiveSlot(slot, objective);
    }

}