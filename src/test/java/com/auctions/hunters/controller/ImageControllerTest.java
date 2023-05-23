package com.auctions.hunters.controller;

import com.auctions.hunters.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController uut;

    @Test
    void displayUploadForm() {
        String result = uut.displayUploadForm();

        assertEquals("/upload_images", result);
    }

    @Test
    void uploadImages() throws IOException {
        MultipartFile[] files = {mock(MultipartFile.class)};
        doNothing().when(imageService).save(files);

        String result = uut.uploadImages(files);

        assertEquals("redirect:/cars", result);
    }
}