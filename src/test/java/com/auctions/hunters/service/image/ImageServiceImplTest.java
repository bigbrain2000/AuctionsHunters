package com.auctions.hunters.service.image;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.ImageRepository;
import com.auctions.hunters.service.car.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CarService carService;
    @Mock
    private Car car;
    @Mock
    private Image image;

    private ImageService uut;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new ImageServiceImpl(imageRepository, carService));
    }

    @Test
    void save_uploadsImages_savesImages() throws IOException {
        MultipartFile[] files = {mock(MultipartFile.class)};
        when(carService.findAll()).thenReturn(List.of(car));

        uut.save(files);

        ArgumentCaptor<List<Image>> imagesCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(imageRepository, times(1)).saveAll(imagesCaptor.capture());
        List<Image> savedImages = imagesCaptor.getValue();

        assertEquals(1, savedImages.size());
        for (Image image : savedImages) {
            assertEquals(car, image.getCar());
        }
    }

    @Test
    void findAllImagesByCarId_returnsImagesByCarId() {
        Car car1 = new Car();
        car1.setId(1);
        Car car2 = new Car();
        car2.setId(2);

        Image image1 = new Image();
        image1.setCar(car1);
        Image image2 = new Image();
        image2.setCar(car1);
        Image image3 = new Image();
        image3.setCar(car2);

        List<Image> allImages = Arrays.asList(image1, image2, image3);
        when(imageRepository.findAll()).thenReturn(allImages);


        List<Image> imagesByCarId = uut.findAllImagesByCarId(1);

        assertEquals(2, imagesByCarId.size());
        for (Image image : imagesByCarId) {
            assertEquals(Integer.valueOf(1), image.getCar().getId());
        }
    }
}