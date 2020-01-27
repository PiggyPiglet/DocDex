package me.piggypiglet.helpdocs.mappers;

import com.google.inject.Inject;
import me.piggypiglet.framework.logging.Logger;
import me.piggypiglet.framework.logging.LoggerFactory;
import me.piggypiglet.framework.utils.StringUtils;
import me.piggypiglet.helpdocs.data.Documentation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentationMapper implements Mapper<Documentation> {
    private static final Logger<?> LOGGER = LoggerFactory.getLogger("Documentation Mapper");
    private static final TypeMapper TYPE_MAPPER = new TypeMapper();

    @Override
    public Documentation generate(Element classes) {
        final Elements elements = classes.select(".indexContainer a");

        elements.parallelStream().forEach(anchor -> {
            final Element element;

            try {
                element = Jsoup.connect(anchor.absUrl("href")).get().selectFirst(".contentContainer .description pre");
            } catch (Exception e) {
                LOGGER.error(e);
                return;
            }

            final Elements optionalAttributes = element.select("a");
            final String text = element.text();
            String pre = text;
            String extension = "";
            String implementation = "";

            if (text.contains("extends")) {
                final String[] extensionParts = text.split("extends");
                final String[] preExtensionParts = extensionParts[0].trim().split("\n");
                pre = preExtensionParts[preExtensionParts.length - 1];
                extension = extensionParts[1];

                if (extension.contains("implements")) {
                    final String[] implementationParts = extension.split("implements");
                    extension = implementationParts[0].trim();
                    implementation = implementationParts[1].trim();
                }
            }
        });

        return null;
    }
}
