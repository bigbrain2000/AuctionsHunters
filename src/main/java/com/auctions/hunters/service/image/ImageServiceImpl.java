package com.auctions.hunters.service.image;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.ImageRepository;
import com.auctions.hunters.service.car.CarService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.auctions.hunters.service.image.ImageUtil.compressImage;

/**
 * Service class used for managing images and implementing the {@link ImageService} interface.
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
     * Retrieve the latest saved {@link Car} from the database and save the files as parameters for that specific {@link Car}.
     *
     * @param files the files that will be persisted in the database
     */
    @Override
    public void save(MultipartFile[] files) throws IOException {
        List<Car> carList = carService.findAll();
        Car lastCar = carList.get(carList.size() - 1);

        //iterate through the multipart files and set the data and content type of the images
        List<Image> imagesList = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = new Image();
            byte[] originalImageBytes = file.getBytes();
            byte[] thumbnailImageBytes = createThumbnail(originalImageBytes, 600, 600);

            image.setData(compressImage(thumbnailImageBytes));
            image.setContentType(file.getContentType());

            imagesList.add(image);
        }

        //save the images for the corresponded car
        for (Image image : imagesList) {
            image.setCar(lastCar);
        }
        imageRepository.saveAll(imagesList);
    }

    /**
     * Create thumbnails of the given images.
     *
     * @param imageBytes   image data
     * @param targetWidth  the wanted width of the thumbnail
     * @param targetHeight the wanted height of the thumbnail
     */
    private byte[] createThumbnail(byte[] imageBytes, int targetWidth, int targetHeight) throws IOException {
        // Check if the input imageBytes is empty or null
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Get all images from the database for a specific car id.
     *
     * @return found images
     */
    @Override
    public List<Image> findAllImagesByCarId(Integer carId) {
        List<Image> allImages = imageRepository.findAll();
        List<Image> wantedImageList = new ArrayList<>();

        for (Image image : allImages) {
            if (image.getCar().getId().equals(carId)) {
                wantedImageList.add(image);
            }
        }

        return wantedImageList;
    }
}