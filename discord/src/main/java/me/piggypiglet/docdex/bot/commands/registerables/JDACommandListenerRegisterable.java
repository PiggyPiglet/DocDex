package me.piggypiglet.docdex.bot.commands.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.bot.commands.listener.JDACommandListener;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JDACommandListenerRegisterable extends Registerable {
    private final JDA jda;
    private final JDACommandListener listener;

    @Inject
    public JDACommandListenerRegisterable(@NotNull final JDA jda, @NotNull final JDACommandListener listener) {
        this.jda = jda;
        this.listener = listener;
    }

    @Override
    protected void execute() {
        jda.addEventListener(listener);
    }
}
