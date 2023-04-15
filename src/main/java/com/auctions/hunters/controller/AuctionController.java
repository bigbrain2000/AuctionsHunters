package com.auctions.hunters.controller;


import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.image.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@Slf4j
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class AuctionController {

    private final CarService carService;
    private final ImageService imageService;
    private final AuctionService auctionService;

    public AuctionController(CarService carService,
                             ImageService imageService,
                             AuctionService auctionService) {
        this.carService = carService;
        this.imageService = imageService;
        this.auctionService = auctionService;
    }

    @GetMapping("/create/auction/car/{id}")
    public String getAuction(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

//        List<Image> images = imageService.findAllImagesByCarId(id);
//        List<String> base64Images = images.stream()
//                .map(image -> java.util.Base64.getEncoder().encodeToString(image.getData()))
//                .collect(Collectors.toList());
//
//        model.addAttribute("images", base64Images);
//        model.addAttribute("contentTypes", images.stream().map(Image::getContentType).collect(Collectors.toList()));


        return "/auction";
    }

    @PostMapping("/create/auction/car/{id}")
    public String createAuction(@PathVariable Integer id, @RequestParam("minimumPrice") String minimumPriceStr, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        try {
            float minimumPrice = Float.parseFloat(minimumPriceStr);
            auctionService.save(car, minimumPrice);
        } catch (NumberFormatException e) {
            return "/login"; //TODO:schimbat cu unul de eroare.
        }

        return "redirect:/";
    }
}
