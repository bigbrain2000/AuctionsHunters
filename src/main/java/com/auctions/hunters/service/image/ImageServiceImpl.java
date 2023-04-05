package com.auctions.hunters.service.image;

import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.ImageRepository;
import com.auctions.hunters.service.car.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


/**
 * Service class used for managing roles and implementing the {@link ImageService} interface.
 */
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final CarService carService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    public ImageServiceImpl(ImageRepository imageRepository,
                            CarService carService) {
        this.imageRepository = imageRepository;
        this.carService = carService;
    }

    @Override
    public void save(List<MultipartFile> files) {
        List<Car> carList = carService.findAll();
        Car lastCar = carList.get(carList.size() - 1);

        for (MultipartFile file : files) {
            Image image = new Image();

            try {
                image.setData(file.getBytes());
            } catch (IOException e) {
                LOGGER.error("Could not upload file!");
            }

            image.setCar(lastCar);
            imageRepository.save(image);
        }
    }

    @Override
    public Image findById(Integer id) {
        return imageRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Could not find the image by the id {} ", id);
            return new ResourceNotFoundException("Image", "id", id);
        });
    }

    @Override
    public void deleteAll(List<Image> images) {
        if (images != null) {
            for (Image image : images) {
                imageRepository.deleteById(image.getId());
            }
            LOGGER.debug("All images were deleted.");
        }

        LOGGER.error("Images list was empty.");
    }

    public Image findImageByCarId(Car car) {
        Optional<Image> optionalImage = imageRepository.findByCarId(car.getId());
        return optionalImage.orElseThrow(() ->
                new IllegalArgumentException(String.format("Provided car id %s is not present", car.getId())));
    }
}