package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.LocalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalTypeRepository extends JpaRepository<LocalType, Integer> {
    @Override
    List<LocalType> findAll();
}
