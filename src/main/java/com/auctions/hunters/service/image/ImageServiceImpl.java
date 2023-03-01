package com.auctions.hunters.service.image;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.ImageRepository;
import com.auctions.hunters.service.car.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


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
}
