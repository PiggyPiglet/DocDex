package me.piggypiglet.docdex.bootstrap;

import com.google.common.collect.Lists;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.bot.commands.registerables.BotCommandsRegisterable;
import me.piggypiglet.docdex.bot.registerables.JDAListenerRegisterable;
import me.piggypiglet.docdex.bot.registerables.JDAPresenceRegisterable;
import me.piggypiglet.docdex.bot.registerables.JDARegisterable;
import me.piggypiglet.docdex.console.registerables.ConsoleCommandListenerRegisterable;
import me.piggypiglet.docdex.console.registerables.ConsoleCommandsRegisterable;
import me.piggypiglet.docdex.db.dbo.registerables.DatabaseObjectCreatorRegisterable;
import me.piggypiglet.docdex.db.dbo.registerables.DatabaseObjectRegisterable;
import me.piggypiglet.docdex.db.orm.registerables.TableObjectsRegisterable;
import me.piggypiglet.docdex.db.orm.registerables.TablesRegisterable;
import me.piggypiglet.docdex.db.orm.structure.registerables.TableStructuresRegisterable;
import me.piggypiglet.docdex.db.registerables.MysqlRegisterable;
import me.piggypiglet.docdex.shutdown.registerables.ShutdownHookRegisterable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class BotBootstrap extends DocDexBootstrap {
    private static final List<Class<? extends Registerable>> REGISTERABLES = Lists.newArrayList(
            JDARegisterable.class,

            MysqlRegisterable.class,
            TableObjectsRegisterable.class,
            TableStructuresRegisterable.class,
            TablesRegisterable.class,
            DatabaseObjectCreatorRegisterable.class,
            DatabaseObjectRegisterable.class,

            ConsoleCommandsRegisterable.class,
            ConsoleCommandListenerRegisterable.class,

            JDAPresenceRegisterable.class,
            BotCommandsRegisterable.class,
            JDAListenerRegisterable.class,

            ShutdownHookRegisterable.class
    );

    @Override
    protected @NotNull List<Class<? extends Registerable>> provideRegisterables() {
        return REGISTERABLES;
    }
}
