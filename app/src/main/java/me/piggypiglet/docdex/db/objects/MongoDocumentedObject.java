package me.piggypiglet.docdex.db.objects;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoDocumentedObject {
    private final String identifier;
    private final String name;
    private final String fqn;
    private final String fullParams;
    private final String typeParams;
    private final String fqnTypeParams;
    private final String nameParams;
    private final String fqnNameParams;
    private final DocumentedObject object;

    private MongoDocumentedObject(@NotNull final String identifier, @NotNull final String name,
                                 @NotNull final String fqn, @NotNull final String fullParams,
                                 @NotNull final String typeParams, @NotNull final String fqnTypeParams,
                                 @NotNull final String nameParams, @NotNull final String fqnNameParams,
                                 @NotNull final DocumentedObject object) {
        this.identifier = identifier;
        this.name = name;
        this.fqn = fqn;
        this.fullParams = fullParams;
        this.typeParams = typeParams;
        this.fqnTypeParams = fqnTypeParams;
        this.nameParams = nameParams;
        this.fqnNameParams = fqnNameParams;
        this.object = object;
    }

    @NotNull
    public String getIdentifier() {
        return name;
    }

    @NotNull
    public DocumentedObject getObject() {
        return object;
    }

    @NotNull
    public static Builder builder(@NotNull final DocumentedObject object) {
        return new Builder(object);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MongoDocumentedObject that = (MongoDocumentedObject) o;
        return identifier.equals(that.identifier) && name.equals(that.name) && fqn.equals(that.fqn) && fullParams.equals(that.fullParams) && typeParams.equals(that.typeParams) && fqnTypeParams.equals(that.fqnTypeParams) && nameParams.equals(that.nameParams) && fqnNameParams.equals(that.fqnNameParams) && object.equals(that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, name, fqn, fullParams, typeParams, fqnTypeParams, nameParams, fqnNameParams, object);
    }

    @Override
    public String toString() {
        return "MongoDocumentedObject{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", fqn='" + fqn + '\'' +
                ", fullParams='" + fullParams + '\'' +
                ", typeParams='" + typeParams + '\'' +
                ", fqnTypeParams='" + fqnTypeParams + '\'' +
                ", nameParams='" + nameParams + '\'' +
                ", fqnNameParams='" + fqnNameParams + '\'' +
                ", object=" + object +
                '}';
    }

    public static final class Builder {
        private String identifier;
        private String name;
        private String fqn;
        private String fullParams;
        private String typeParams;
        private String fqnTypeParams;
        private String nameParams;
        private String fqnNameParams;
        private final DocumentedObject object;

        private Builder(@NotNull final DocumentedObject object) {
            this.object = object;
        }

        @NotNull
        public Builder identifier(@NotNull final String value) {
            identifier = value;
            return this;
        }

        @NotNull
        public Builder name(@NotNull final String value) {
            name = value;
            return this;
        }

        @NotNull
        public Builder fqn(@NotNull final String value) {
            fqn = value;
            return this;
        }

        @NotNull
        public Builder fullParams(@NotNull final String value) {
            fullParams = value;
            return this;
        }

        @NotNull
        public Builder typeParams(@NotNull final String value) {
            typeParams = value;
            return this;
        }

        @NotNull
        public Builder fqnTypeParams(@NotNull final String value) {
            fqnTypeParams = value;
            return this;
        }

        @NotNull
        public Builder nameParams(@NotNull final String value) {
            nameParams = value;
            return this;
        }

        @NotNull
        public Builder fqnNameParams(@NotNull final String value) {
            fqnNameParams = value;
            return this;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @Nullable
        public String getFqn() {
            return fqn;
        }

        @NotNull
        public MongoDocumentedObject build() {
            return new MongoDocumentedObject(identifier, name, fqn, fullParams, typeParams, fqnTypeParams,
                    nameParams, fqnNameParams, object);
        }
    }
}
