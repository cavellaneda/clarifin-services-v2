package com.clarifin.services.port.in;

import com.clarifin.services.domain.Login;
import com.clarifin.services.domain.Session;
import com.clarifin.services.domain.User;
import com.clarifin.services.domain.UserComplete;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserUseCase {

  List<User> findAllUsersByClientId(@NotNull Long idClient);

  User createUser(@NotNull Long idClient, @NotNull UserComplete userComplete);

  Optional<User> findUserById(@NotBlank String idUser, @NotNull Long idClient);

  User saveUser(@NotNull User user, @NotNull Long idClient);

  void deleteUser(@NotBlank String idUser);

  Optional<Session> login(@NotNull Login login);

  Boolean linkBusinessAndUser(String idUser, Long idClient, String idBusiness);
}
