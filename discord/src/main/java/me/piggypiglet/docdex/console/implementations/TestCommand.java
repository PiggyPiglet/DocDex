package me.piggypiglet.docdex.console.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.db.adapters.framework.DatabaseObjectAdapter;
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
    private final DatabaseObjectAdapter<Server> adapter;

    @Inject
    public TestCommand(@NotNull final Set<Server> servers, @NotNull final TableManager tableManager,
                       @NotNull final DatabaseObjectAdapter<Server> adapter) {
        super("test", "");
        this.servers = servers;
        this.tableManager = tableManager;
        this.adapter = adapter;
    }

    @Override
    public void execute() {
//        servers.add(new Server("6969", "d;", Map.of("test", new CommandRule(Set.of("121"), Set.of("4512"), "woo woo"))));
//        servers.stream().findAny().ifPresent(server -> server.getRules().remove("test2"));
//        servers.stream().findAny().ifPresent(server -> server.getRules().put("test2", new CommandRule(Sets.newHashSet("42"), Sets.newHashSet("41"), "ayy")));
//        servers.stream().findAny().ifPresent(server -> server.getRules().get("test").getAllowed().remove("6868"));
        msg(servers);
        servers.stream()
                .map(adapter::applyToRaw)
                .forEach(request -> {
                    request.getModified().forEach(tableManager::save);
                    request.getDeleted().forEach(tableManager::delete);
                });
    }
}
