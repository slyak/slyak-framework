package com.slyak.license.support.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/dict")
public class DictController {

    @Autowired
    private DictProviders dictProviders;

    @GetMapping("/search")
    public List<DictItem> getDictItems(String code, String key, @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        DictProvider dictProvider = dictProviders.getDictProvider(code);
        if (dictProvider == null) {
            return Collections.emptyList();
        }
        return dictProvider.search(key, limit);
    }

    @GetMapping("/searchByCodes")
    public List<DictItem> getDictItems(String code, @RequestParam("itemCodes") List<String> itemCodes) {
        DictProvider dictProvider = dictProviders.getDictProvider(code);
        if (dictProvider == null) {
            return Collections.emptyList();
        }
        return dictProvider.searchByCodes(itemCodes);
    }
}