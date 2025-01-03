package com.clarifin.services.port.in;

import com.clarifin.services.domain.TemplateCategories;
import java.util.List;

public interface TemplateCategoriesUseCase {

  TemplateCategories create(Long idClient, String idCompany, String idBusinessUnit,
      TemplateCategories templateCategories);

  List<TemplateCategories> get(Long idClient);
}
