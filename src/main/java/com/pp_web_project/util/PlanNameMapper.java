package com.pp_web_project.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlanNameMapper {

    public static final Map<String, String> PLAN_MAP;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("NA00007679", "Red eSIM 1일");
        map.put("NA00008813", "Red eSIM 2일");
        map.put("NA00008249", "Red eSIM 3일");
        map.put("NA00008814", "Red eSIM 4일");
        map.put("NA00008250", "Red eSIM 5일");
        map.put("NA00008815", "Red eSIM 6일");
        map.put("NA00008777", "Red eSIM 7일");
        map.put("NA00008816", "Red eSIM 8일");
        map.put("NA00008817", "Red eSIM 9일");
        map.put("NA00008251", "Red eSIM 10일");
        map.put("NA00008778", "Red eSIM 15일");
        map.put("NA00008252", "Red eSIM 20일");
        map.put("NA00008253", "Red eSIM 30일");
        map.put("NA00008779", "Red eSIM 60일");
        map.put("NA00008780", "Red eSIM 90일");
        PLAN_MAP = Collections.unmodifiableMap(map); // 불변 처리
    }
}
