package me.piggypiglet.docdex.pterodactyl.requests.implementations.command;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.pterodactyl.requests.PterodactylRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandRequest extends PterodactylRequest {
    private static final String ENDPOINT = "/api/client/servers/%s/command";

    @Inject
    public CommandRequest(@NotNull final Config config) {
        super(config);
    }

    public void send(@NotNull final String server, @NotNull final String command)
            throws InterruptedException, IOException, URISyntaxException {
        final HttpResponse<String> response = post(String.format(ENDPOINT, server), new CommandPayload(command));

        switch (response.statusCode()) {
            case 502:
                LOGGER.warn(server + " is not online in pterodactyl.");
                break;

            case 204:
                LOGGER.info("Successfully sent " + command + " to " + server + '.');
                break;
        }
    }
}
