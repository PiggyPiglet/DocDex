package me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.pterodactyl.requests.PterodactylRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class GetPowerStatusRequest extends PterodactylRequest {
    private static final String ENDPOINT = "/api/client/servers/%s/resources";

    @Inject
    public GetPowerStatusRequest(@NotNull final Config config) {
        super(config);
    }

    @NotNull
    public GettablePowerState get(@NotNull final String server)
            throws InterruptedException, IOException, URISyntaxException {
        return get(String.format(ENDPOINT, server), GettablePowerState.class).body();
    }
}
