package me.piggypiglet.docdex.bot.embed.documentation.flexmark;

import com.google.common.collect.Sets;
import com.vladsch.flexmark.html2md.converter.*;
import com.vladsch.flexmark.util.data.DataHolder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CodeHtmlNodeConvertor implements HtmlNodeRenderer {
    private CodeHtmlNodeConvertor() {}

    @NotNull
    @Override
    public Set<HtmlNodeRendererHandler<?>> getHtmlNodeRendererHandlers() {
        return Sets.newHashSet(
                new HtmlNodeRendererHandler<>("pre", Element.class, this::processCode)
        );
    }

    private void processCode(@NotNull final Element node, @NotNull final HtmlNodeConverterContext context,
                             @NotNull final HtmlMarkdownWriter out) {
        final Element code = node.selectFirst("code");

        if (code == null) {
            return;
        }

        final String text = code.text();

        if (text.contains("\n")) {
            out.append("```java\n").append(text).append("\n```");
        } else {
            out.append('`').append(text).append('`');
        }
    }

    public static final class Factory implements HtmlNodeRendererFactory {
        private Factory() {}

        @NotNull
        public static Factory create() {
            return new Factory();
        }

        @NotNull
        @Override
        public HtmlNodeRenderer apply(@NotNull final DataHolder options) {
            return new CodeHtmlNodeConvertor();
        }
    }
}
