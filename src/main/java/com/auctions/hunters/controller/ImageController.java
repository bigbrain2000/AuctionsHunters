package com.auctions.hunters.controller;

import com.auctions.hunters.service.image.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public String uploadImages(@RequestParam("files") List<MultipartFile> files) {
        imageService.save(files);
        return "/homepage";
    }

    @GetMapping
    public String getImages() {
        return "/images";
    }

//    @GetMapping("/images/{id}")
//    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
//        Optional<Image> optionalImage = imageRepository.findById(id);
//        if (optionalImage.isPresent()) {
//            Image image = optionalImage.get();
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_JPEG)
//                    .body(image.getData());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
