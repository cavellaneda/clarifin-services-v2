package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.UserBusinessEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserBusinessPort {

  Boolean linkBusinessAndUser(@NotNull UserBusinessEntity userBusinessEntity);

  List<UserBusinessEntity> getBusinessByUserId(@NotBlank String idUser) ;


  List<UserBusinessEntity> getUserIdByIdBusiness(@NotBlank String idBusiness) ;

  List<UserBusinessEntity> getUserBusinessByIdUserAndIdBusiness(@NotBlank String idUser, @NotBlank String idBusiness) ;
}
