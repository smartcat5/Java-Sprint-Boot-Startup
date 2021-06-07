package io.github.raeperd.realworld.domain.article.comment;

import io.github.raeperd.realworld.domain.article.ArticleFindService;
import io.github.raeperd.realworld.domain.user.UserFindService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.springframework.data.util.Optionals.mapIfAllPresent;

@Service
public class CommentService {

    private final UserFindService userFindService;
    private final ArticleFindService articleFindService;

    CommentService(UserFindService userFindService, ArticleFindService articleFindService) {
        this.userFindService = userFindService;
        this.articleFindService = articleFindService;
    }

    @Transactional
    public Comment createComment(long userId, String slug, String body) {
        return mapIfAllPresent(userFindService.findById(userId), articleFindService.getArticleBySlug(slug),
                (user, article) -> user.writeCommentToArticle(article, body))
                .orElseThrow(NoSuchElementException::new);
    }
}
