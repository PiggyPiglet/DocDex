package me.piggypiglet.docdex.pterodactyl;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.UpdaterJavadoc;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.command.CommandRequest;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get.GetPowerStatusRequest;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get.GettablePowerState;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.status.set.SetPowerStatusRequest;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.status.set.SettablePowerState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PterodactylManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Pterodactyl");

    private final Config config;

    private final CommandRequest commandRequest;
    private final GetPowerStatusRequest getPowerStatusRequest;
    private final SetPowerStatusRequest setPowerStatusRequest;

    @Inject
    public PterodactylManager(@NotNull final Config config, @NotNull final CommandRequest commandRequest,
                              @NotNull final GetPowerStatusRequest getPowerStatusRequest, @NotNull final SetPowerStatusRequest setPowerStatusRequest) {
        this.config = config;

        this.commandRequest = commandRequest;
        this.getPowerStatusRequest = getPowerStatusRequest;
        this.setPowerStatusRequest = setPowerStatusRequest;
    }

    public void deleteJavadocsAndStop(@NotNull final Set<UpdaterJavadoc> javadocs) {
        final Set<String> collections = javadocs.stream()
                .map(UpdaterJavadoc::getNames)
                .map(names -> String.join("-", names))
                .filter(name -> {
                    final String path = config.getPterodactyl().getDirectory() + "/docs/" + name + ".json";

                    try {
                        Files.delete(Paths.get(path));
                    } catch (NoSuchFileException exception) {
                        LOGGER.warn("No file exists at path: {}", path);
                    } catch (IOException exception) {
                        LOGGER.error("Something went wrong when deleting " + path, exception);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toSet());

        run(() -> commandRequest.send(config.getPterodactyl().getServer(), "delete " + collections));
    }

    @Nullable
    public GettablePowerState getPowerState() {
        return run(() -> getPowerStatusRequest.get(config.getPterodactyl().getServer()));
    }

    public void setPowerState(@NotNull final SettablePowerState powerState) {
        run(() -> setPowerStatusRequest.send(config.getPterodactyl().getServer(), powerState));
    }

    private void run(@NotNull final ThrowingRequest request) {
        run(() -> {
            request.apply();
            return null;
        });
    }

    @Nullable
    private <T> T run(@NotNull final ThrowingRequestWithResponse<T> request) {
        try {
            return request.supply();
        } catch (InterruptedException exception) {
            LOGGER.error("Interrupted.", exception);
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException | URISyntaxException exception) {
            LOGGER.error("Something went wrong with a request.", exception);
        }

        return null;
    }

    @FunctionalInterface
    private interface ThrowingRequest {
        void apply() throws InterruptedException, IOException, URISyntaxException;
    }

    @FunctionalInterface
    private interface ThrowingRequestWithResponse<T> {
        T supply() throws InterruptedException, IOException, URISyntaxException;
    }
}
