package me.piggypiglet.docdex.db.dbo.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.framework.adapter.DatabaseObjectAdapter;
import me.piggypiglet.docdex.db.dbo.framework.adapter.ModificationRequest;
import me.piggypiglet.docdex.db.server.CommandRule;
import me.piggypiglet.docdex.db.server.JavadocCategory;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.tables.RawServer;
import me.piggypiglet.docdex.db.tables.RawServerRoles;
import me.piggypiglet.docdex.db.tables.framework.RawObject;
import me.piggypiglet.docdex.db.tables.framework.RawServerRule;
import me.piggypiglet.docdex.db.tables.framework.RawServerRuleId;
import me.piggypiglet.docdex.db.tables.javadoc.RawServerJavadocCategories;
import me.piggypiglet.docdex.db.tables.javadoc.RawServerJavadocCategoriesJavadocs;
import me.piggypiglet.docdex.db.tables.rules.RawServerRules;
import me.piggypiglet.docdex.db.tables.rules.RawServerRulesAllowed;
import me.piggypiglet.docdex.db.tables.rules.RawServerRulesDisallowed;
import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ServerAdapter implements DatabaseObjectAdapter<Server> {
    private final Set<RawServer> servers;

    private final Set<RawServerRoles> serverRoles;

    private final Set<RawServerRules> serverRules;
    private final Set<RawServerRulesAllowed> serverRulesAlloweds;
    private final Set<RawServerRulesDisallowed> serverRulesDisalloweds;

    private final Set<RawServerJavadocCategories> serverJavadocCategories;
    private final Set<RawServerJavadocCategoriesJavadocs> serverJavadocCategoriesJavadocs;

    @Inject
    public ServerAdapter(@NotNull final Set<RawServer> servers, @NotNull final Set<RawServerRules> serverRules,
                         @NotNull final Set<RawServerRoles> serverRoles, @NotNull final Set<RawServerRulesAllowed> serverRulesAlloweds,
                         @NotNull final Set<RawServerRulesDisallowed> serverRulesDisalloweds, @NotNull final Set<RawServerJavadocCategories> serverJavadocCategories,
                         @NotNull final Set<RawServerJavadocCategoriesJavadocs> serverJavadocCategoriesJavadocs) {
        this.servers = servers;

        this.serverRoles = serverRoles;

        this.serverRules = serverRules;
        this.serverRulesAlloweds = serverRulesAlloweds;
        this.serverRulesDisalloweds = serverRulesDisalloweds;

        this.serverJavadocCategories = serverJavadocCategories;
        this.serverJavadocCategoriesJavadocs = serverJavadocCategoriesJavadocs;
    }

    @NotNull
    @Override
    public Set<Server> loadFromRaw() {
        return servers.stream()
                .map(server -> {
                    final Algorithm algorithm = Algorithm.NAMES.get(server.getAlgorithm());

                    final Set<String> roles = serverRoles.stream()
                            .map(RawServerRoles::getId)
                            .collect(Collectors.toSet());
                    final Map<String, CommandRule> rules = new HashMap<>();
                    final Map<String, JavadocCategory> categories = new HashMap<>();

                    serverRules.stream()
                            .filter(rule -> rule.getServer().equals(server.getId()))
                            .forEach(rule -> {
                                final Set<String> allowed = getRuleIds(serverRulesAlloweds, rule);
                                final Set<String> disallowed = getRuleIds(serverRulesDisalloweds, rule);

                                rules.put(rule.getCommand(), new CommandRule(allowed, disallowed, rule.getRecommendation()));
                            });
                    serverJavadocCategories.stream()
                            .filter(category -> category.getServer().equals(server.getId()))
                            .forEach(category -> {
                                final Set<String> javadocs = serverJavadocCategoriesJavadocs.stream()
                                        .filter(javadoc -> javadoc.getServer().equals(server.getId()) && javadoc.getCategory().equals(category.getName()))
                                        .map(RawServerJavadocCategoriesJavadocs::getName)
                                        .collect(Collectors.toSet());

                                categories.put(category.getName(), new JavadocCategory(category.getDescription(), javadocs));
                            });

                    return new Server(server.getId(), server.getPrefix(), algorithm, server.getDefaultJavadoc(), roles, rules, categories);
                }).collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public ModificationRequest applyToRaw(final @NotNull Server server) {
        final String id = server.getId();
        final RawServer rawServer = new RawServer(id, server.getPrefix(), server.getAlgorithm().name(), server.getDefaultJavadoc());

        final Set<RawServerRoles> rawServerRoles = server.getRoles().stream()
                .map(role -> new RawServerRoles(server.getId(), role))
                .collect(Collectors.toSet());

        final Set<RawServerRules> rawServerRules = server.getRules().entrySet().stream()
                .map(entry -> new RawServerRules(id, entry.getKey(), entry.getValue().getRecommendation()))
                .collect(Collectors.toSet());
        final Set<RawServerRulesAllowed> rawServerRulesAlloweds = getRawRuleIds(server, CommandRule::getAllowed, RawServerRulesAllowed::new);
        final Set<RawServerRulesDisallowed> rawServerRulesDisalloweds = getRawRuleIds(server, CommandRule::getDisallowed, RawServerRulesDisallowed::new);

        final Set<RawServerJavadocCategories> rawServerJavadocCategories = new HashSet<>();
        final Set<RawServerJavadocCategoriesJavadocs> rawServerJavadocCategoriesJavadocs = new HashSet<>();

        server.getJavadocCategories().forEach((name, category) -> {
            rawServerJavadocCategories.add(new RawServerJavadocCategories(server.getId(), name, category.getDescription()));
            category.getJavadocs().forEach(javadoc -> rawServerJavadocCategoriesJavadocs.add(new RawServerJavadocCategoriesJavadocs(server.getId(), name, javadoc)));
        });

        final Set<Object> modified = new HashSet<>();

        addIfAdded(servers, modified, rawServer);
        rawServerRoles.forEach(object -> addIfAdded(serverRoles, modified, object));
        rawServerRules.forEach(object -> addIfAdded(serverRules, modified, object));
        rawServerRulesAlloweds.forEach(object -> addIfAdded(serverRulesAlloweds, modified, object));
        rawServerRulesDisalloweds.forEach(object -> addIfAdded(serverRulesDisalloweds, modified, object));
        rawServerJavadocCategories.forEach(object -> addIfAdded(serverJavadocCategories, modified, object));
        rawServerJavadocCategoriesJavadocs.forEach(object -> addIfAdded(serverJavadocCategoriesJavadocs, modified, object));

        final Set<Object> deleted = new HashSet<>();

        deleteIfDeleted(serverRoles, role -> role.getServer().equals(id), rawServerRoles, deleted);
        deleteIfDeleted(serverRules, rule -> rule.getServer().equals(id), rawServerRules, deleted);
        deleteIfDeleted(serverRulesAlloweds, allowed -> allowed.getServer().equals(id), rawServerRulesAlloweds, deleted);
        deleteIfDeleted(serverRulesDisalloweds, disallowed -> disallowed.getServer().equals(id), rawServerRulesDisalloweds, deleted);
        deleteIfDeleted(serverJavadocCategoriesJavadocs, javadocs -> javadocs.getServer().equals(id), rawServerJavadocCategoriesJavadocs, deleted);

        return new ModificationRequest(modified, deleted);
    }

    private static <T extends RawObject> void addIfAdded(@NotNull final Set<T> set, @NotNull final Set<Object> resultSet,
                                                         @NotNull final T object) {
        if (set.add(object)) {
            resultSet.add(object);
        } else {
            new HashSet<>(set).stream()
                    .filter(object::equals)
                    .filter(element -> !element.actualEquals(object))
                    .forEach(element -> {
                        set.remove(element);
                        set.add(object);
                        resultSet.add(object);
                    });
        }
    }

    private static <T> void deleteIfDeleted(@NotNull final Set<T> cached, @NotNull final Predicate<T> filter,
                                            @NotNull final Set<T> converted, @NotNull final Set<Object> deleted) {
        cached.removeIf(object -> {
            if (!filter.test(object)) {
                return false;
            }

            if (!converted.contains(object)) {
                deleted.add(object);
                return true;
            }

            return false;
        });
    }

    @NotNull
    private static Set<String> getRuleIds(@NotNull final Set<? extends RawServerRuleId> serverRuleIds, @NotNull final RawServerRule parent) {
        return serverRuleIds.stream()
                .filter(rule -> rule.getServer().equals(parent.getServer()) && rule.getCommand().equals(parent.getCommand()))
                .map(RawServerRuleId::getId)
                .collect(Collectors.toSet());
    }

    @NotNull
    private static <T extends RawServerRuleId> Set<T> getRawRuleIds(@NotNull final Server server, @NotNull final Function<CommandRule, Set<String>> getter,
                                                                    @NotNull final RawServerRuleIdConstructor<T> constructor) {
        return server.getRules().entrySet().stream()
                .flatMap(entry -> getter.apply(entry.getValue()).stream()
                        .map(id -> constructor.construct(server.getId(), entry.getKey(), id)))
                .collect(Collectors.toSet());
    }

    @FunctionalInterface
    private interface RawServerRuleIdConstructor<T> {
        @NotNull
        T construct(@NotNull final String server, @NotNull final String command, @NotNull final String id);
    }
}
