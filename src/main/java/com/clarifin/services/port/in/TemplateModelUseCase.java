package com.clarifin.services.port.in;

import com.clarifin.services.domain.TemplateModel;
import com.clarifin.services.domain.TemplateModelByClient;
import java.util.Date;
import java.util.List;

public interface TemplateModelUseCase {

   List<TemplateModel> findAllTemplateModel();

  List<TemplateModelByClient> getTemplateModelByClient(Long idClient, String idBusiness, Date startDate, Date endDate,
      String idTemplateModel);
}
