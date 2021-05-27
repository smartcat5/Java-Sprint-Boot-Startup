package io.github.raeperd.realworld.domain.article;

import io.github.raeperd.realworld.domain.user.User;
import io.github.raeperd.realworld.domain.user.UserFindService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    private ArticleService articleService;

    @Mock
    private UserFindService userFindService;
    @Mock
    private ArticleRepository repository;

    @Spy
    private User author;

    @BeforeEach
    private void initializeService() {
        articleService = new ArticleService(userFindService, repository);
    }

    @Test
    void when_author_not_found_expect_NoSuchElementException(@Mock ArticleContents contents) {
        when(userFindService.findById(anyLong())).thenReturn(empty());

        assertThatThrownBy(() ->
                articleService.createNewArticle(1L, contents)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void given_author_createNewArticle_then_author_writeArticle_contents(@Mock ArticleContents contents) {
        given(userFindService.findById(1L)).willReturn(of(author));
        given(repository.save(any())).willReturn(mock(Article.class));

        articleService.createNewArticle(1L, contents);

        then(author).should(times(1)).writeArticle(contents);
    }

    @Test
    void given_author_writeArticle_then_userRepository_save(@Mock ArticleContents contents, @Mock Article article) {
        given(userFindService.findById(1L)).willReturn(of(author));
        given(author.writeArticle(contents)).willReturn(article);
        given(repository.save(article)).willReturn(article);

        articleService.createNewArticle(1L, contents);

        then(repository).should(times(1)).save(article);
    }
}