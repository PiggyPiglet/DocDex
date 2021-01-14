package me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get;

import com.google.gson.annotations.JsonAdapter;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get.deserialization.GettablePowerStateSerializer;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@JsonAdapter(GettablePowerStateSerializer.class)
public enum GettablePowerState {
    OFFLINE,
    STARTING,
    RUNNING,
    STOPPING
}
