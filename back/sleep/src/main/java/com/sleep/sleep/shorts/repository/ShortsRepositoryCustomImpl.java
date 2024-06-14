package com.sleep.sleep.shorts.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.entity.QMember;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * Shorts 객체의 triedShortsList와 일치하는 객체를 tried_shorts 테이블에서 찾음
     * triedShortsList가 비어있는 Shorts 객체(챌린저가 없는 Shorts)도 가져오기 위해 left join 사용함
     * 인기 쇼츠를 불러올 때 쇼츠 정보도 필요해서 fetch Join 사용함
     * 네이티브 쿼리로 표현하면 LEFT JOIN tried_shorts ts ON s.shorts_id = ts.shorts_id
     */
    @Override
    public List<Shorts> findPopularShorts() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

        return queryFactory.selectFrom(qShorts)
                .leftJoin(qShorts.triedShortsList, qTriedShorts).fetchJoin()
                .groupBy(qShorts)
                .orderBy(qTriedShorts.count().desc())
                .limit(3)
                .fetch();

    }

    /**
     * 특정 id의 회원과 특정 id의 쇼츠로 만든 TriedShorts 객체가 이미 있는지 확인함
     *
     * @param member 특정 id의 회원
     * @param shorts 특정 id의 쇼츠
     */
    @Override
    public TriedShorts findShortsAlreadyTried(Member member, Shorts shorts) {
        QMember qMember = QMember.member;
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

        return queryFactory.selectFrom(qTriedShorts)
                .where(qTriedShorts.member.eq(member)
                        .and(qTriedShorts.shorts.eq(shorts)))
                .fetchOne();

    }

}
