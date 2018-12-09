package io.bakalux.course.service;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class LastIdService {
    @Getter
    public Long lastId;

    public void setLastId(Long id) {
        if (lastId != null) {
            throw new RuntimeException("Last id is already set.");
        }

        lastId = id;
    }

    public void next() {
        if (lastId == null) {
            lastId = 0L;
        }

        lastId++;
    }
}
