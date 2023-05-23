package com.auctions.hunters.repository;

import com.auctions.hunters.model.Image;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Image, Integer> {

    /**
     * Retrieve all images from the database.
     *
     * @return a list with all the images found
     */
    @Override
    @NotNull
    List<Image> findAll();
}