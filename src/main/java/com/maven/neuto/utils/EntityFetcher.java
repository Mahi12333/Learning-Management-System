package com.maven.neuto.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@RequiredArgsConstructor
public class EntityFetcher {
    private final EntityManager entityManager;
    private static final ObjectMapper mapper = new ObjectMapper();


    public Object fetchOldEntity(Object entity) {
        if (entity == null) return null;

        Object id = extractId(entity);
        if (id == null) return null;

        return entityManager.find(entity.getClass(), id);
    }

    private Object extractId(Object entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return field.get(entity);
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
