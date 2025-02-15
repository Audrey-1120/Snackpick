package com.project.snackpick.repository;

import com.project.snackpick.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 회원 존재 여부 확인
    Boolean existsById(String id);

    Optional<MemberEntity> findById(String id);
}
