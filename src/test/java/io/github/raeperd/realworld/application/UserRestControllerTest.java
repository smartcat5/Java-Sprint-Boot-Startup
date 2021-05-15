package io.github.raeperd.realworld.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.raeperd.realworld.application.security.WithMockJWTUser;
import io.github.raeperd.realworld.domain.jwt.JWTDeserializer;
import io.github.raeperd.realworld.domain.jwt.JWTSerializer;
import io.github.raeperd.realworld.domain.user.User;
import io.github.raeperd.realworld.domain.user.UserService;
import io.github.raeperd.realworld.domain.user.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static io.github.raeperd.realworld.domain.user.UserTestUtils.userWithEmailAndName;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private JWTSerializer jwtSerializer;
    @MockBean
    private JWTDeserializer jwtDeserializer;

    @BeforeEach
    void mockJwtSerializer() {
        when(jwtSerializer.jwtFromUser(any())).thenReturn("MOCKED_TOKEN");
    }

    @MethodSource("provideInvalidPostDTO")
    @ParameterizedTest
    void when_post_user_with_invalid_body_expect_status_badRequest(UserPostRequestDTO dto) throws Exception {
        mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_post_user_expect_valid_userModel() throws Exception {
        when(userService.signUp(any(UserSignUpRequest.class))).thenReturn(sampleUser());

        mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(samplePostDTO())))
                .andExpect(status().isOk())
                .andExpect(validUserModel());
    }

    @WithMockJWTUser
    @Test
    void when_get_user_expect_valid_userModel() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(of(sampleUser()));

        mockMvc.perform(get("/user")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(validUserModel());
    }

    private static Stream<Arguments> provideInvalidPostDTO() {
        return Stream.of(
                Arguments.of(new UserPostRequestDTO("not-email", "username", "password")),
                Arguments.of(new UserPostRequestDTO("user@email.com", "", "password")),
                Arguments.of(new UserPostRequestDTO("user@email.com", "username", ""))
        );
    }

    private User sampleUser() {
        return userWithEmailAndName("user@email.com", "username");
    }

    private UserPostRequestDTO samplePostDTO() {
        return new UserPostRequestDTO("user@email.com", "username", "password");
    }

    static ResultMatcher validUserModel() {
        return matchAll(jsonPath("user").hasJsonPath(),
                jsonPath("user.email").isString(),
                jsonPath("user.token").isString(),
                jsonPath("user.username").isString(),
                jsonPath("user.bio").isString(),
                jsonPath("user.image").isString());
    }

}