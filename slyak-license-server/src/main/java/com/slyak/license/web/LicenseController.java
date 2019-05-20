package com.slyak.license.web;

import com.slyak.license.domain.License;
import com.slyak.license.domain.LicenseQuery;
import com.slyak.license.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/license")
public class LicenseController {

    @Autowired
    private LicenseRepository repo;

    @GetMapping("/list")
    public void list(LicenseQuery query, Pageable pageable, ModelMap modelMap) {
        modelMap.put("page", repo.query(query, pageable));
    }

    @GetMapping("/edit")
    public void edit() {
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public void save(@ModelAttribute("license") License license) {
        repo.save(license);
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public void delete(@ModelAttribute("license") License license) {
        repo.delete(license);
    }

    @ModelAttribute("license")
    public License get(String id) {
        return id == null ? new License() : repo.findOne(id);
    }
}