package me.piggypiglet.helpdocs.data;

import me.piggypiglet.framework.managers.implementations.SearchableManager;
import me.piggypiglet.framework.managers.objects.KeyTypeInfo;
import me.piggypiglet.framework.utils.SearchUtils;

import java.util.List;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Type extends SearchableManager<Method> implements SearchUtils.Searchable {
    private final String name;
    private final String type;
    private final String description;
    private final List<String> fields;
    private final List<String> constants;
    private final Map<String, Method> methods;

    public Type(String name, String type, String description, List<String> fields,
                List<String> constants, Map<String, Method> methods) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.fields = fields;
        this.constants = constants;
        this.methods = methods;
    }

    @Override
    protected KeyTypeInfo configure(KeyTypeInfo.Builder builder) {
        return null;
    }

    @Override
    protected void insert(Method item) {

    }

    @Override
    protected void delete(Method item) {

    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getConstants() {
        return constants;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }
}
