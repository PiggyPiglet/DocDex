package me.piggypiglet.docdex.console.implementations;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.CommandRule;
import me.piggypiglet.docdex.db.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestCommand extends ConsoleCommand {
    private final Set<Server> servers;
    private final DatabaseObjects adapters;

    @Inject
    public TestCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super("test", "");
        this.servers = servers;
        this.adapters = adapters;
    }

    @Override
    public void execute() {
        final Map<String, CommandRule> rules = new HashMap<>();
        rules.put("help", new CommandRule(Sets.newHashSet("339674158596358145", "411094432402636802"), Sets.newHashSet(), "You can't use that command in this channel, please go to <#339674158596358145>."));
        final Server server = new Server("164280494874165248", "bd;", Sets.newHashSet("164525396354793472"), rules);
        servers.add(server);
        msg(servers);
        adapters.save(server);
//        servers.forEach(adapters::save);
    }
}
