package org.example.place.component;

import org.springframework.data.domain.Pageable;

public interface LocalOpenApi {
    public <T> T getLocalByKeyword(String keyword, Class<T> clazz);
    public <T> T getLocalByKeyword(String keyword, Pageable pageable, Class<T> clazz);
}
