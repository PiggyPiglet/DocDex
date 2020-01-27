package me.piggypiglet.helpdocs.data;

import com.google.gson.annotations.SerializedName;
import me.piggypiglet.framework.managers.implementations.SearchableManager;
import me.piggypiglet.framework.managers.objects.KeyTypeInfo;
import me.piggypiglet.framework.utils.SearchUtils;
import me.piggypiglet.framework.utils.annotations.reflection.Disabled;

import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Disabled
public final class Type extends SearchableManager<Metadata> implements SearchUtils.Searchable {
    private final String name;
    @SerializedName("package") private final String pckg;
    private final Metadata metadata;
    private final List<String> implementations;
    private final String extension;

    public Type(String name, String pckg, Metadata metadata, List<Metadata> members,
                List<String> implementations, String extension) {
        this.name = name;
        this.pckg = pckg;
        this.metadata = metadata;
        items.addAll(members);
        this.implementations = implementations;
        this.extension = extension;
    }

    @Override
    protected KeyTypeInfo configure(KeyTypeInfo.Builder builder) {
        return null;
    }

    @Override
    protected void insert(Metadata item) {}

    @Override
    protected void delete(Metadata item) {}

    @Override
    public String getName() {
        return name;
    }

    public String getPackage() {
        return pckg;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<String> getImplementations() {
        return implementations;
    }

    public String getExtension() {
        return extension;
    }
}
