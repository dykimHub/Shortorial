package com.sleep.sleep.shorts.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * Shorts 객체의 triedShortsList와 일치하는 객체를 tried_shorts 테이블에서 찾음
     * triedShortsList가 비어있는 Shorts 객체(챌린저가 없는 Shorts)도 가져오기 위해 left join 사용함
     * 네이티브 쿼리로 표현하면 LEFT JOIN tried_shorts ts ON s.shorts_id = ts.shorts_id
     */
    @Override
    public List<Shorts> findPopularShorts() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

        return queryFactory.selectFrom(qShorts)
                .leftJoin(qShorts.triedShortsList, qTriedShorts)
                .fetchJoin()
                .groupBy(qShorts)
                .orderBy(qTriedShorts.count().desc())
                .limit(3)
                .fetch();
    }
}
