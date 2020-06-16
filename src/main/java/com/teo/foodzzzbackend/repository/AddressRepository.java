package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Override
    <S extends Address> S save(S entity);
}
