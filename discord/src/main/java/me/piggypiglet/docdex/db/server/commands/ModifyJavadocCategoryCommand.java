package me.piggypiglet.docdex.db.server.commands;

import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.JavadocCategory;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.util.ModificationOptions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ModifyJavadocCategoryCommand extends ServerCommand {
    private static final Map<String, ModificationTypes> MODIFICATION_TYPES = Map.of(
            "description", ModificationTypes.DESCRIPTION,
            "javadocs", ModificationTypes.JAVADOCS
    );

    private final DatabaseObjects adapters;

    public ModifyJavadocCategoryCommand(@NotNull final String usage, @NotNull final DatabaseObjects adapters) {
        super(usage, 5, adapters);
        this.adapters = adapters;
    }

    @Override
    protected boolean execute(final @NotNull Server server, final @NotNull List<String> args,
                              final @NotNull Consumer<String> messageFunction) {
        final ModificationTypes modificationType = MODIFICATION_TYPES.get(args.get(1));
        final boolean isDescription = modificationType == ModificationTypes.DESCRIPTION;

        if (modificationType == null) {
            sendUsage(messageFunction);
            return false;
        }

        final String name = args.get(isDescription ? 2 : 3);
        final String value = String.join(" ", args.subList(isDescription ? 3 : 4, args.size()));

        final JavadocCategory category = Optional.ofNullable(server.getJavadocCategories().get(name))
                .orElseGet(() -> {
                    final JavadocCategory newCategory = adapters.createInstance(JavadocCategory.class);
                    server.getJavadocCategories().put(name, newCategory);
                    return newCategory;
                });

        if (isDescription) {
            category.setDescription(value);
            messageFunction.accept("Successfully updated description of category " + server.getId() + " - " + name + " to " + value);
            return true;
        }

        final ModificationOptions modificationOption = ModificationOptions.MAP.get(args.get(2));

        if (modificationOption == null) {
            sendUsage(messageFunction);
            return false;
        }

        final Set<String> set = category.getJavadocs();

        if (modificationOption == ModificationOptions.ADD) {
            set.add(value);
        } else {
            set.remove(value);
        }

        messageFunction.accept("Successfully " + modificationOption + ' ' + value + " to category " + server.getId() + " - " + name + "'s javadoc list.");
        return true;
    }

    private enum ModificationTypes {
        DESCRIPTION,
        JAVADOCS
    }
}
