package com.auctions.hunters.controller;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.user.SellerService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "/seller")
public class SellerController {

    private final CarService carService;
    private final SellerService sellerService;

    public SellerController(CarService carService,
                            SellerService sellerService) {
        this.carService = carService;
        this.sellerService = sellerService;
    }

    @GetMapping("/addCar")
    public String getCar(@ModelAttribute @Valid Car car) {
        return "/add_car";
    }

    @PostMapping("/addCar")
    public String addCar(@ModelAttribute @Valid Car car, RedirectAttributes redirectAttributes) {
        carService.save(car);
        sellerService.addCarToUserInventory(car);

        redirectAttributes.addFlashAttribute("message", "Car added successfully");
        return "/homepage";
    }
}
