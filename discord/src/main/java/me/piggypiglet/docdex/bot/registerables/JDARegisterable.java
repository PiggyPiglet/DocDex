package me.piggypiglet.docdex.bot.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JDARegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("JDA");

    private final Config config;

    @Inject
    public JDARegisterable(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    protected void execute() {
        final JDA jda;

        try {
            jda = JDABuilder.createDefault(config.getToken()).build();
        } catch (LoginException e) {
            LOGGER.error("Something went wrong when logging into JDA", e);
            System.exit(0);
            return;
        }

        addBinding(JDA.class, jda);
    }
}
