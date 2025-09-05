package com.dabom.together.repository;

import com.dabom.together.model.entity.Together;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TogetherRepositoryCustom {
    Slice<Together> searchAllByIsOpenTrue(String keyword, Pageable pageable);
}
