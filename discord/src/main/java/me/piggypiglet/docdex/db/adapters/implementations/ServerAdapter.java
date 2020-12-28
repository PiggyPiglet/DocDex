package me.piggypiglet.docdex.db.adapters.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.CommandRule;
import me.piggypiglet.docdex.db.adapters.DatabaseObjectAdapter;
import me.piggypiglet.docdex.db.objects.Server;
import me.piggypiglet.docdex.db.tables.RawServer;
import me.piggypiglet.docdex.db.tables.RawServerRules;
import me.piggypiglet.docdex.db.tables.RawServerRulesAllowed;
import me.piggypiglet.docdex.db.tables.RawServerRulesDisallowed;
import me.piggypiglet.docdex.db.tables.framework.RawServerRule;
import me.piggypiglet.docdex.db.tables.framework.RawServerRuleId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ServerAdapter implements DatabaseObjectAdapter<Server> {
    private final Set<RawServer> servers;
    private final Set<RawServerRules> serverRules;
    private final Set<RawServerRulesAllowed> serverRulesAlloweds;
    private final Set<RawServerRulesDisallowed> serverRulesDisalloweds;

    @Inject
    public ServerAdapter(@NotNull final Set<RawServer> servers, @NotNull final Set<RawServerRules> serverRules,
                         @NotNull final Set<RawServerRulesAllowed> serverRulesAlloweds, @NotNull final Set<RawServerRulesDisallowed> serverRulesDisalloweds) {
        this.servers = servers;
        this.serverRules = serverRules;
        this.serverRulesAlloweds = serverRulesAlloweds;
        this.serverRulesDisalloweds = serverRulesDisalloweds;
    }

    @NotNull
    @Override
    public Set<Server> loadFromRaw() {
        return servers.stream()
                .map(server -> {
                    final Map<String, CommandRule> rules = new HashMap<>();

                    serverRules.stream()
                            .filter(rule -> rule.getServer().equals(server.getId()))
                            .forEach(rule -> {
                                final Set<String> allowed = getRuleIds(serverRulesAlloweds, rule);
                                final Set<String> disallowed = getRuleIds(serverRulesDisalloweds, rule);

                                rules.put(rule.getCommand(), new CommandRule(allowed, disallowed, rule.getRecommendation()));
                            });

                    return new Server(server.getId(), server.getPrefix(), rules);
                }).collect(Collectors.toSet());
    }

    @Override
    public void applyToRaw(final @NotNull Server server) {

    }

    @NotNull
    private static Set<String> getRuleIds(@NotNull final Set<? extends RawServerRuleId> serverRuleIds, @NotNull final RawServerRule parent) {
        return serverRuleIds.stream()
                .filter(rule -> rule.getServer().equals(parent.getServer()) && rule.getCommand().equals(parent.getCommand()))
                .map(RawServerRuleId::getId)
                .collect(Collectors.toSet());
    }
}
