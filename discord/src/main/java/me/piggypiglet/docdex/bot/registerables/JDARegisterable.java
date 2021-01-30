package me.piggypiglet.docdex.bot.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JDARegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("JDA");
    private static final Set<GatewayIntent> GATEWAY_INTENTS = Set.of(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS
    );

    private final Config config;

    @Inject
    public JDARegisterable(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    public void execute() {
        final ShardManager shardManager;

        try {
            shardManager = DefaultShardManagerBuilder.create(config.getToken(), GATEWAY_INTENTS)
                    .setShardsTotal(-1) // get recommended shard count from Discord
                    .setMemberCachePolicy(MemberCachePolicy.NONE) // disable member caching
                    .setChunkingFilter(ChunkingFilter.NONE) // disable eager Guild loading
                    .disableCache(EnumSet.allOf(CacheFlag.class)) // disable unnecessary cache flags
                    .build();
        } catch (LoginException e) {
            LOGGER.error("Something went wrong when logging into JDA, perhaps you haven't populated the config.", e);
            System.exit(0);
            return;
        }

        addBinding(ShardManager.class, shardManager);
    }
}
