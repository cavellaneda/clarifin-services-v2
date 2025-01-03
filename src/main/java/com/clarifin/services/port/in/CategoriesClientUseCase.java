package com.clarifin.services.port.in;

import com.clarifin.services.domain.CategoriesClient;
import com.clarifin.services.domain.CodeLevels;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CategoriesClientUseCase {

  List<CategoriesClient> getCategoriesByClient(@NotNull Long idClient, @NotBlank String idBusiness);

  List<CategoriesClient> getCategories();

  List<String> postCategoriesByTemplateClient(Long idClient, String idBusiness, String idTemplate, List<CodeLevels> codeLevels);

  List<String> patchCategoriesByTemplateClient(Long idClient, String idBusiness, String idTemplate, List<CodeLevels> codeLevels);
}
