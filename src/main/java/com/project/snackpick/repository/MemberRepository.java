package com.project.snackpick.repository;

import com.project.snackpick.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 회원 존재 여부 확인
    Boolean existsById(String id);

    // ID로 회원 정보 조회
    Optional<MemberEntity> findById(String id);

    // memberID로 회원 정보 조회
    @Query("SELECT m, COUNT(DISTINCT r), COUNT(DISTINCT c) FROM MemberEntity m " +
            "LEFT JOIN m.reviewList r " +
            "LEFT JOIN m.commentList c " +
            "WHERE m.memberId = :memberId " +
            "GROUP BY m")
    Optional<Object[]> findMemberByMemberId(@Param("memberId") int memberId);
}
