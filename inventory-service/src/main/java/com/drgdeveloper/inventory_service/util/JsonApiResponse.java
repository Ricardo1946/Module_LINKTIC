package com.drgdeveloper.inventory_service.util;

import java.util.Map;

public class JsonApiResponse {

    private final Map<String, Object> data;

    public JsonApiResponse(Object obj) {
        this.data = Map.of(
                "type", obj.getClass().getSimpleName().toLowerCase(),
                "attributes", obj
        );
    }
    public Map<String, Object> getData() {
        return data;
    }
}
