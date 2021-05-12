package io.github.raeperd.realworld.domain.article.title;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Slug {

    private static final String SLUG_TRANSFORM_REGEX = "\\$,'\"|\\s|\\.|\\?";

    @Column(name = "slug")
    private String value;

    static Slug of(ArticleTitle articleTitle) {
        return new Slug(articleTitle.toString()
                .toLowerCase()
                .replaceAll(SLUG_TRANSFORM_REGEX, "-"));
    }

    private Slug(String value) {
        this.value = value;
    }

    protected Slug() {
    }

    @Override
    public String toString() {
        return value;
    }
}
