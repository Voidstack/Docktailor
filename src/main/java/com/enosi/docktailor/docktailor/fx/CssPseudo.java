package com.enosi.docktailor.docktailor.fx;

/**
 * Css Pseudo class.
 */
public record CssPseudo(String name) {

    @Override
    public String toString() {
        return name();
    }
}
