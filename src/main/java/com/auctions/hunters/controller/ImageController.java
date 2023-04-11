package com.auctions.hunters.controller;

import com.auctions.hunters.model.Image;
import com.auctions.hunters.service.image.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/upload")
    public String displayUploadForm() {
        return "/upload_images";
    }

    @PostMapping("/upload")
    public String uploadImages(@RequestParam("images") MultipartFile[] files) throws IOException {
        imageService.save(files);

        return "redirect:/cars";
    }

//    @GetMapping("/display")
//    public String displayImages(Model model) {
//        List<Image> images = imageService.getAllImages();
//        List<String> base64Images = images.stream()
//                .map(image -> Base64.getEncoder().encodeToString(image.getData()))
//                .collect(Collectors.toList());
//
//        model.addAttribute("images", base64Images);
//        model.addAttribute("contentTypes", images.stream().map(Image::getContentType).collect(Collectors.toList()));
//        return "display_images";
//    }
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
