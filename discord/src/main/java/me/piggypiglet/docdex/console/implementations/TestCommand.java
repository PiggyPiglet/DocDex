package me.piggypiglet.docdex.console.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.db.objects.Server;
import me.piggypiglet.docdex.db.orm.TableManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestCommand extends ConsoleCommand {
    private final Set<Server> servers;
    private final TableManager tableManager;

    @Inject
    public TestCommand(@NotNull final Set<Server> servers, @NotNull final TableManager tableManager) {
        super("test", "");
        this.servers = servers;
        this.tableManager = tableManager;
    }

    @Override
    public void execute() {
        msg(servers);
    }
}
