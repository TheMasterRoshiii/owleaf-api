package me.master.owleaf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.master.owleaf.api.OwleafGravityAPI;
import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.Gravity;
import me.master.owleaf.util.RotationUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;

public class GravityCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalSet = Commands.literal("set");
        for (Direction direction : Direction.values()) {
            literalSet.then(Commands.literal(direction.getName())
                    .then(Commands.argument("priority", IntegerArgumentType.integer())
                            .then(Commands.argument("duration", IntegerArgumentType.integer())
                                    .executes(context -> executeSet(
                                            context.getSource(),
                                            direction,
                                            IntegerArgumentType.getInteger(context, "priority"),
                                            IntegerArgumentType.getInteger(context, "duration"),
                                            Collections.singleton(context.getSource().getEntityOrException())
                                    ))
                                    .then(Commands.argument("entities", EntityArgument.entities())
                                            .executes(context -> executeSet(
                                                    context.getSource(),
                                                    direction,
                                                    IntegerArgumentType.getInteger(context, "priority"),
                                                    IntegerArgumentType.getInteger(context, "duration"),
                                                    EntityArgument.getEntities(context, "entities")
                                            ))
                                    )
                            )
                    )
            );
        }

        LiteralArgumentBuilder<CommandSourceStack> literalSetDefault = Commands.literal("setdefault");
        for (Direction direction : Direction.values()) {
            literalSetDefault.then(Commands.literal(direction.getName())
                    .executes(context -> executeSetDefault(
                            context.getSource(),
                            direction,
                            Collections.singleton(context.getSource().getEntityOrException())
                    ))
                    .then(Commands.argument("entities", EntityArgument.entities())
                            .executes(context -> executeSetDefault(
                                    context.getSource(),
                                    direction,
                                    EntityArgument.getEntities(context, "entities")
                            ))
                    )
            );
        }

        LiteralArgumentBuilder<CommandSourceStack> literalGet = Commands.literal("get")
                .executes(context -> executeGet(
                        context.getSource(),
                        context.getSource().getEntityOrException()
                ))
                .then(Commands.argument("entity", EntityArgument.entity())
                        .executes(context -> executeGet(
                                context.getSource(),
                                EntityArgument.getEntity(context, "entity")
                        ))
                );

        LiteralArgumentBuilder<CommandSourceStack> literalClear = Commands.literal("clear")
                .executes(context -> executeClearGravity(
                        context.getSource(),
                        Collections.singleton(context.getSource().getEntityOrException())
                ))
                .then(Commands.argument("entities", EntityArgument.entities())
                        .executes(context -> executeClearGravity(
                                context.getSource(),
                                EntityArgument.getEntities(context, "entities")
                        ))
                );

        dispatcher.register(Commands.literal("gravity")
                .requires(source -> source.hasPermission(2))
                .then(literalSet)
                .then(literalSetDefault)
                .then(literalGet)
                .then(literalClear)
        );
    }

    private static void getSendFeedback(CommandSourceStack source, Entity entity, Direction gravityDirection) {
        Component text = Component.literal("direction." + gravityDirection.getName());
        if (source.getEntity() != null && source.getEntity() == entity) {
            source.sendSuccess(() -> Component.translatable("commands.gravity.get.self", text), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.gravity.get.other", entity.getDisplayName(), text), true);
        }
    }

    private static int executeGet(CommandSourceStack source, Entity entity) {
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(entity);
        getSendFeedback(source, entity, gravityDirection);
        return gravityDirection.get3DDataValue();
    }

    private static int executeSet(CommandSourceStack source, Direction gravityDirection, int priority, int duration, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            OwleafGravityAPI.addGravity(entity, new Gravity(gravityDirection, priority, duration, "command"));
            ++i;
        }
        return i;
    }

    private static int executeSetDefault(CommandSourceStack source, Direction gravityDirection, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            if (OwleafGravityAPI.getDefaultGravityDirection(entity) != gravityDirection) {
                OwleafGravityAPI.setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
                getSendFeedback(source, entity, gravityDirection);
                ++i;
            }
        }
        return i;
    }

    private static int executeClearGravity(CommandSourceStack source, Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            OwleafGravityAPI.clearGravity(entity, new RotationParameters());
            ++i;
        }
        return i;
    }
}
