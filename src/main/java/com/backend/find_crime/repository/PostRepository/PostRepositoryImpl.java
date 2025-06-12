package com.backend.find_crime.repository.PostRepository;

import com.backend.find_crime.config.QueryDSLUtil;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.QPost;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QPost post = QPost.post;

    @Override
    public Page<Post> findPostPagesByCrimeArea(CrimeArea crimeArea, Pageable pageable) {
        // 조건 builder 생성
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.crimeArea.eq(crimeArea));

        // order
        List<OrderSpecifier<?>> orders = QueryDSLUtil.getOrderSpecifiers(pageable, Post.class, "post");

        List<Post> content = queryFactory
                .selectFrom(post)
                .where(builder)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
