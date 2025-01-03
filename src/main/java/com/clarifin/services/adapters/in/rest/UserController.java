package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.Login;
import com.clarifin.services.domain.Session;
import com.clarifin.services.domain.User;
import com.clarifin.services.domain.UserComplete;
import com.clarifin.services.port.in.UserUseCase;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class UserController {

  @Autowired
  private UserUseCase userUseCase;

  @GetMapping("/client/{idClient}/user")
  public List<User> getAllUserByClient(@PathVariable final Long idClient) {
    return userUseCase.findAllUsersByClientId(idClient);
  }

  @PostMapping("/client/{idClient}/user")
  public User createUser(@RequestBody UserComplete userComplete, @PathVariable final Long idClient) {
    return userUseCase.createUser(idClient, userComplete);
  }

  @GetMapping("/client/{idClient}/user/{idUser}")
  public ResponseEntity<User> getUserById(@PathVariable final Long idClient, @PathVariable String idUser) {
    return userUseCase.findUserById(idUser, idClient)
        .map(user -> ResponseEntity.ok().body(user))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/client/{idClient}/user/{idUser}")
  public ResponseEntity<User> updateUser(@PathVariable Long idClient, @PathVariable String idUser,
      @RequestBody User userUpdate) {
    return userUseCase.findUserById(idUser, idClient)
        .map(user -> {
          user.setName(userUpdate.getName());
          user.setDocument(userUpdate.getDocument());
          user.setTypeDocument(userUpdate.getTypeDocument());
          user.setEmail(userUpdate.getEmail());
          user.setPhone(userUpdate.getPhone());
          user.setAddress(userUpdate.getAddress());
          user.setCity(userUpdate.getCity());
          User updatedUser = userUseCase.saveUser(user, idClient);
          return ResponseEntity.ok().body(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/client/{idClient}/user/{idUser}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long idClient, @PathVariable String idUser) {
    final Optional<User> user = userUseCase.findUserById(idUser, idClient);
    if (user.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      userUseCase.deleteUser(user.get().getId());
      return ResponseEntity.ok().build();
    }
  }

  @PostMapping("/session/login")
  public ResponseEntity<Session> loginUser(@RequestBody Login login) {
    final Optional<Session> session = userUseCase.login(login);
    if (session.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } else {
      return ResponseEntity.ok().body(session.get());
    }
  }

  @PostMapping("/client/{idClient}/business/{idBusiness}/user/{idUser}")
  public ResponseEntity<Void> linkBusinessAndUser(@PathVariable final Long idClient, @PathVariable String idBusiness, @PathVariable String idUser) {
    return userUseCase.linkBusinessAndUser(idUser, idClient, idBusiness)? ResponseEntity.ok().build():
        ResponseEntity.notFound().build();
  }
}
