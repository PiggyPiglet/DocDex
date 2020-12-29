package me.piggypiglet.docdex.db.server.commands;

import me.piggypiglet.docdex.db.adapters.DatabaseObjectAdapters;
import me.piggypiglet.docdex.db.server.CommandRule;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.util.ModificationOptions;
import me.piggypiglet.docdex.db.server.creation.CommandRuleCreator;
import me.piggypiglet.docdex.db.utils.MysqlUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ModifyCommandRuleCommand extends ServerCommand {
    private static final Map<String, ModificationTypes> MODIFICATION_TYPES = Map.of(
            "allow", ModificationTypes.ALLOW,
            "disallow", ModificationTypes.DISALLOW,
            "recommendation", ModificationTypes.RECOMMENDATION
    );

    public ModifyCommandRuleCommand(@NotNull final String usage, @NotNull final DatabaseObjectAdapters adapters) {
        super(usage,5, adapters);
    }

    @Override
    protected void execute(final @NotNull Server server, final @NotNull List<String> args,
                           @NotNull final Consumer<String> messageFunction) {
        final ModificationTypes modificationType = MODIFICATION_TYPES.get(args.get(1));
        final boolean isRecommendation = modificationType == ModificationTypes.RECOMMENDATION;

        if (modificationType == null) {
            sendUsage(messageFunction);
            return;
        }

        final String command = args.get(isRecommendation ? 2 : 3);
        final String value = MysqlUtils.escapeSql(String.join(" ", args.subList(isRecommendation ? 3 : 4, args.size())));

        final CommandRule rule = Objects.requireNonNull(Optional.ofNullable(server.getRules().get(command))
                .orElseGet(() -> server.getRules().put(command, CommandRuleCreator.createInstance())));

        if (isRecommendation) {
            rule.setRecommendation(value);
            messageFunction.accept("Successfully updated recommendation of " + server.getId() + " - " + command + " to " + value);
            return;
        }

        final ModificationOptions modificationOption = ModificationOptions.MAP.get(args.get(2));

        if (modificationOption == null) {
            sendUsage(messageFunction);
            return;
        }

        final Set<String> set;

        switch (modificationType) {
            case ALLOW:
                set = rule.getAllowed();
                break;

            case DISALLOW:
                set = rule.getDisallowed();
                break;

            default:
                return;
        }

        switch (modificationOption) {
            case ADD:
                set.add(value);
                break;

            case REMOVE:
                set.remove(value);
                break;
        }

        messageFunction.accept("Successfully " + modificationOption + ' ' + value + " to " + server.getId() + " - " + command + ' ' + modificationType + " list.");
    }

    private enum ModificationTypes {
        ALLOW("ed"),
        DISALLOW("ed"),
        RECOMMENDATION("");

        private final String extension;

        ModificationTypes(@NotNull final String extension) {
            this.extension = extension;
        }

        @Override
        public String toString() {
            return name().toLowerCase() + extension;
        }
    }
}