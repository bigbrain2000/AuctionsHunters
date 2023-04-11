package com.auctions.hunters.service.image;

import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.ImageRepository;
import com.auctions.hunters.service.car.CarService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
     *
     * @param files the file that will be persisted in the database
     */
    @Override
    public void save(MultipartFile[] files) throws IOException {
        List<Car> carList = carService.findAll();
        Car lastCar = carList.get(carList.size() - 1);
        int targetWidth = 400; // Desired width
        //iterate through the multipart files and set the data and content type of the images
        List<Image> imagesList = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = new Image();
            byte[] originalImageBytes = file.getBytes();
            byte[] thumbnailImageBytes = createThumbnail(originalImageBytes, 400, 300); // Desired dimensions

            image.setData(thumbnailImageBytes);
            image.setContentType(file.getContentType());

            imagesList.add(image);
        }

        //save the images for the corresponded car
        for (Image image : imagesList) {
            image.setCar(lastCar);
        }
        imageRepository.saveAll(imagesList);
    }

    public byte[] createThumbnail(byte[] imageBytes, int targetWidth, int targetHeight) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public Image findById(Integer id) {
        return imageRepository.findById(id).orElseThrow(() -> {
            log.debug("Could not find the image by the id {} ", id);
            return new ResourceNotFoundException("Image", "id", id);
        });
    }

    @Override
    public List<Image> findAll() {
        return imageRepository.findAll();
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