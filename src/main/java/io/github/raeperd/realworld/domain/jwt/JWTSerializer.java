package io.github.raeperd.realworld.domain.jwt;

import io.github.raeperd.realworld.domain.user.User;

public interface JWTSerializer {

    String jwtFromUser(User user);

}
