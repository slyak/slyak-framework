package com.slyak.license.support.dict;

import java.util.List;

public interface DictProvider {

    String getName();

    List<DictItem> search(String key, int limit);

    List<DictItem> searchByCodes(List<String> itemCodes);
}
