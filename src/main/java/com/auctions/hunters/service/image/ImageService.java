package com.auctions.hunters.service.image;

import com.auctions.hunters.model.Image;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Image} entity.
 */
@Validated
@Repository
public interface ImageService {

    /**
     * Save a role in the DB.
     *
     * @param files the car images that will be saved in the database
     */
    void save(@NotNull List<MultipartFile> files);
}
