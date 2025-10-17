package com.drgdeveloper.products_service.client;

import java.util.Map;

public class JsonApiResponse {

    private final Map<String, Object> data;

    public JsonApiResponse(Object object) {
        this.data = Map.of(
                "type", object.getClass().getSimpleName().toLowerCase(),
                "attributes", object
        );
    }

    public Map<String, Object> getData() {
        return data;
    }
}
