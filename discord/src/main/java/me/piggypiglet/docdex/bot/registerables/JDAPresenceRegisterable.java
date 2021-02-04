package me.piggypiglet.docdex.bot.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Presence;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JDAPresenceRegisterable extends Registerable {
    private final ShardManager shardManager;
    private final Config config;

    @Inject
    public JDAPresenceRegisterable(@NotNull final ShardManager shardManager, @NotNull final Config config) {
        this.shardManager = shardManager;
        this.config = config;
    }

    @Override
    public void execute() {
        final Presence presence = config.getPresence();
        shardManager.getShards().forEach(shard -> shard.getPresence().setPresence(presence.getStatus(), Activity.of(presence.getActivity(), presence.getMessage())));
    }
}
