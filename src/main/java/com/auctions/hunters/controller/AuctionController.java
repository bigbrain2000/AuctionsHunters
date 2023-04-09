package com.auctions.hunters.controller;


import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.image.ImageService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "/auction")
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

    @GetMapping("/create/car/{id}")
    public String getAuction(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        Image image = imageService.findImageByCarId(car);
        model.addAttribute("image", Base64.encodeBase64String(image.getData()));

        return "/auction";
    }

    @PostMapping("/create/car/{id}")
    public String createAuction(@PathVariable Integer id, @RequestParam("minimumPrice") String minimumPriceStr, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        Image image = imageService.findImageByCarId(car);

        //encodes binary data using the base64 algorithm
        String encodeBase64String = Base64.encodeBase64String(image.getData());
        model.addAttribute("image", encodeBase64String);

        try {
            float minimumPrice = Float.parseFloat(minimumPriceStr);
            auctionService.save(car, minimumPrice);
        } catch (NumberFormatException e) {
            return "/login"; //TODO:schimbat cu unul de eroare.
        }

        return "/auction";
    }
}
