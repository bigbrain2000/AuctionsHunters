package com.auctions.hunters.service.image;

import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.ImageRepository;
import com.auctions.hunters.service.car.CarService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Service class used for managing roles and implementing the {@link ImageService} interface.
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final CarService carService;

    public ImageServiceImpl(ImageRepository imageRepository, CarService carService) {
        this.imageRepository = imageRepository;
        this.carService = carService;
    }

    /**
     * Save multiple files in the DB.
     * Retrieve the latest car saved in the database and save the files as parameters for that specific car.
     * @param files the file that will be persisted in the database
     */
    @Override
    public void save(MultipartFile[] files) {
        List<Car> carList = carService.findAll();
        Car lastCar = carList.get(carList.size() - 1);

        //iterate through the multipart files and set the data and content type of the images
        List<Image> imagesList = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = new Image();
            try {
                image.setData(file.getBytes());
                image.setContentType(file.getContentType());
            } catch (IOException e) {
                log.error("Error at setting the image fields for image with id {}", image.getId());
            }

            imagesList.add(image);
        }

        //save the images for the corresponded car
        for (Image image : imagesList) {
            image.setCar(lastCar);
        }
        imageRepository.saveAll(imagesList);
    }

    @Override
    public Image findById(Integer id) {
        return imageRepository.findById(id).orElseThrow(() -> {
            log.debug("Could not find the image by the id {} ", id);
            return new ResourceNotFoundException("Image", "id", id);
        });
    }

    @Override
    public void deleteAll(List<Image> images) {
        if (images != null) {
            for (Image image : images) {
                imageRepository.deleteById(image.getId());
            }
            log.debug("All images were deleted.");
        }

        log.error("Images list was empty.");
    }

    /**
     * Find a specific image based on the provided car id.
     *
     * @param car the car for what we want to get the image from
     * @return found image
     */
    public Image findImageByCarId(Car car) {
        Optional<Image> optionalImage = imageRepository.findByCarId(car.getId());
        return optionalImage.orElseThrow(() ->
                new IllegalArgumentException(String.format("Provided car id %s is not present", car.getId())));
    }
}