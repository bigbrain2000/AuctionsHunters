package com.auctions.hunters.repository;

import com.auctions.hunters.model.Image;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Optional<Image> findByCarId(Integer carId);

    @Override
    @NotNull
    List<Image> findAll();
}