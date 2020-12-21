package me.piggypiglet.docdex.config;

import com.google.gson.annotations.JsonAdapter;
import me.piggypiglet.docdex.config.deserialization.ActivityDeserializer;
import me.piggypiglet.docdex.config.deserialization.StatusDeserializer;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Presence {
    @JsonAdapter(StatusDeserializer.class) private OnlineStatus status;
    @JsonAdapter(ActivityDeserializer.class) private Activity.ActivityType activity;
    private String message;

    @NotNull
    public OnlineStatus getStatus() {
        return status;
    }

    @NotNull
    public Activity.ActivityType getActivity() {
        return activity;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Presence{" +
                "status=" + status +
                ", activity=" + activity +
                ", message='" + message + '\'' +
                '}';
    }
}
