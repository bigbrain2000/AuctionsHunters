package com.auctions.hunters.service.image;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Image} entity.
 */
@Repository
public interface ImageService {

    /**
     * Save multiple files in the DB.
     *
     */
     void save(MultipartFile[] files);

    /**
     * Find a specific image based on id.
     *
     * @param id image id
     * @return found user
     */
    Image findById(Integer id);

    /**
     * Delete all images from the list.
     *
     * @param images the images that need to be deleted
     */
    void deleteAll(List<Image> images);

    /**
     * Find a specific image based on the provided car id.
     *
     * @param car the car for what we want to get the image from
     * @return found image
     */
    Image findImageByCarId(Car car);
}