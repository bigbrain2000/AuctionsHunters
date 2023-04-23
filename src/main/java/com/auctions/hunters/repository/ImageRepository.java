package com.auctions.hunters.repository;

import com.auctions.hunters.model.Image;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    /**
     * Retrieved an {@link Optional<Image>} from the database where the foreign key, car_id is equal to the parameter value.
     */
    Optional<Image> findByCarId(Integer carId);

    /**
     * Retrieve all images from the database.
     *
     * @return a list with all the images found
     */
    @Override
    @NotNull
    List<Image> findAll();
}