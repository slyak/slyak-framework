package com.slyak.license.web;

import com.slyak.license.domain.${entity.name};
import com.slyak.license.domain.${entity.name}Query;
import com.slyak.license.repository.${entity.name}Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/${entity.name?uncap_first}")
public class ${entity.name}Controller {

    @Autowired
    private ${entity.name}Repository repo;

    @GetMapping("/list")
    public void list(${entity.name}Query query, Pageable pageable, ModelMap modelMap) {
        modelMap.put("page", repo.query(query, pageable));
    }

    @GetMapping("/edit")
    public void edit() {
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public void save(@ModelAttribute("${entity.name?uncap_first}") ${entity.name} ${entity.name?uncap_first}) {
        repo.save(${entity.name?uncap_first});
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public void delete(@ModelAttribute("${entity.name?uncap_first}") ${entity.name} ${entity.name?uncap_first}) {
        repo.delete(${entity.name?uncap_first});
    }

    @ModelAttribute("${entity.name?uncap_first}")
    public ${entity.name} get(Long id) {
        return id == null ? new ${entity.name}() : repo.findOne(id);
    }
}