package me.snaptime.snap.data.repository;

import me.snaptime.snap.data.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> ,
        QuerydslPredicateExecutor<Photo> {
    Photo findByFileName(String name);
}
