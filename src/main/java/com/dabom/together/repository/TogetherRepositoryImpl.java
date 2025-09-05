package com.dabom.together.repository;

import com.dabom.together.model.entity.Together;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static com.dabom.together.model.entity.QTogether.together;

import java.util.List;

@RequiredArgsConstructor
public class TogetherRepositoryImpl implements TogetherRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Together> searchAllByIsOpenTrue(String keyword, Pageable pageable) {
        List<Together> searchTogether = queryFactory
                .selectFrom(together)
                .where(
                        together.isOpen.eq(true)
                                .and(
                                        together.title.containsIgnoreCase(keyword)
                                                .or(together.master.name.containsIgnoreCase(keyword))
                                )
                )
                .orderBy(together.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return createSlice(searchTogether, pageable);
    }

    private Slice<Together> createSlice(List<Together> togethers, Pageable pageable) {
        boolean hasNext = togethers.size() > pageable.getPageSize();

        if (hasNext) {
            togethers.remove(togethers.size() - 1);
        }

        return new SliceImpl<>(togethers, pageable, hasNext);
    }
}
