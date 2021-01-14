package me.piggypiglet.docdex.pterodactyl.requests.implementations.status.set;

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
public final class SetPowerStatusRequest extends PterodactylRequest {
    private static final String ENDPOINT = "/api/client/servers/%s/power";

    @Inject
    protected SetPowerStatusRequest(final @NotNull Config config) {
        super(config);
    }

    public boolean send(@NotNull final String server, @NotNull final SettablePowerState state)
            throws InterruptedException, IOException, URISyntaxException {
        final HttpResponse<String> response = post(String.format(ENDPOINT, server), new SetPowerStatusPayload(state.toString().toLowerCase()));

        return response.statusCode() == 204;
    }
}
