package com.enosistudio.docktailor.fx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class CssMerger {

    private final String[] cssUrls;

    public CssMerger(String... cssUrls) {
        this.cssUrls = cssUrls;
    }

    public String toUserAgentStylesheet() {
        try {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(cssUrls).forEach(url ->
                    sb.append("@import url(\"").append(url).append("\");\n")
            );

            Path tmp = Files.createTempFile("merged-", ".css");
            Files.writeString(tmp, sb.toString());

            return tmp.toUri().toString();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le CSS fusionné", e);
        }
    }
}
