package com.auctions.hunters.service.image;

import com.auctions.hunters.model.Image;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Image} entity.
 */
@Repository
public interface ImageService {

    /**
     * Save multiple files in the DB.
     */
    void save(MultipartFile[] files) throws IOException;

    /**
     * Get all images from the database for a specific car id.
     *
     * @return found images
     */
    List<Image> findAllImagesByCarId(Integer carId);
}