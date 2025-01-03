package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CategoriesOn6Entity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoriesOn6Repository extends
    JpaRepository<CategoriesOn6Entity, String> {

  List<CategoriesOn6Entity> findCategoriesOn6EntitiesByIdTemplateMasterCategoriesIn(
      List<String> templateMasterCategoriesIds);


  @Query(value = "select "
      + "    min(`ce`.`id`) AS `id`, "
      + "    substr(`ce`.`code`, 1, 6) AS `code_6_digits`, "
      + "    min(`ce`.`id_key`) AS `id_key`, "
      + "    min(`ce`.`id_template_master_categories`) AS `id_template_master_categories`, "
      + "    min(`le`.`name`) AS `name`, "
      + "    min(`t`.`id_company`) as `id_company`, "
      + "    min(`t`.`id_business_unit`) as `id_business_unit` "
      + "from "
      + "    (`clarifinv2`.`categories_keys_entity` `ce` "
      + "left join `clarifinv2`.`keys_entity` `le` on "
      + "    (`le`.`id` = `ce`.`id_key`)) "
      + "left join `template_master_categories_keys_entity` `t` on "
      + "    (`ce`.`id_template_master_categories` = `t`.`id`) "
      + "where "
      + "`id_template_master_categories` = :templateMasterCategoriesId "
      + "group by "
      + "    substr(`ce`.`code`, 1, 6)", nativeQuery = true)
  List<CategoriesOn6Entity> findCategoriesOn6EntitiesByIdTemplateMasterCategories(
      String templateMasterCategoriesId);
}
