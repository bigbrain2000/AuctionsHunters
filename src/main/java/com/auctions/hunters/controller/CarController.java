package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.car.SearchCriteria;
import com.auctions.hunters.service.image.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@Slf4j
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class CarController {

    private final CarService carService;
    private final CarRepository carRepository;
    private final ImageService imageService;
    private final AuctionService auctionService;

    public CarController(CarService carService,
                         CarRepository carRepository,
                         ImageService imageService,
                         AuctionService auctionService) {
        this.carService = carService;
        this.carRepository = carRepository;
        this.imageService = imageService;
        this.auctionService = auctionService;
    }

    @GetMapping("/car/add")
    public String getCar(@ModelAttribute @Valid Car car) {
        return "/add_car";
    }

    @PostMapping("/car/add")
    public String addCar(@RequestParam("vin") String vin) {
        try {
            carService.save(vin);
        } catch (NotEnoughLookupsException e) {
            return "/login";  //TODO: add different maps
        } catch (CarPayloadFailedToCreateException e) {
            return "/login";
        } catch (UnrecognizedVinException e) {
            return "/login";
        } catch (CarVinAlreadyExistsException e) {

            return "redirect:/images/upload";
        }

        return "redirect:/images/upload";
    }

    @GetMapping("/cars")
    public String getCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "producer", required = false) String producer,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "minYear", required = false) Integer minYear,
            @RequestParam(value = "maxYear", required = false) Integer maxYear,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            Model modelAtr) {

        SearchCriteria searchCriteria = new SearchCriteria();
        Specification<Car> carSpecification = searchCriteria.buildSpec(producer, model, minYear, maxYear, minPrice, maxPrice);

        Page<Car> carPage = getCarPage(page, carSpecification);
        modelAtr.addAttribute("carPage", carPage);
        modelAtr.addAttribute("currentPage", page);

        List<Float> auctionsMinimumPriceList = setMinimumPriceForEachCar(carPage);

        int totalPages = carPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .toList();
            modelAtr.addAttribute("pageNumbers", pageNumbers);
            modelAtr.addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        } else {
            modelAtr.addAttribute("pageNumbers", null);
            modelAtr.addAttribute("emptyMessage", "There are no cars to display.");
            return "/no_car";
        }

        return "/car_list";
    }

    @NotNull
    private List<Float> setMinimumPriceForEachCar(Page<Car> carPage) {
        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        List<Car> content = carPage.getContent();
        for (Car car : content) {
            Auction auction = auctionService.getAuctionByCarId(car.getId());

            if (auction != null) {
                auctionsMinimumPriceList.add(auction.getMinimumPrice());
            }
        }
        return auctionsMinimumPriceList;
    }

    @NotNull
    private Page<Car> getCarPage(int page, Specification<Car> spec) {
        int pageSize = 10; // number of elements in a page
        return carRepository.findAll(spec, PageRequest.of(page, pageSize));
    }

    @GetMapping("/cars/{id}")
    public String deleteCar(@PathVariable Integer id) {
        try {
            carService.deleteById(id);
        } catch (CarExistsInAuctionException e) {
            return "redirect:/user/cars"; //TODO: ADD a new redirect for the car the user cannot delete the car
            //because the car is in an auction
        }

        return "redirect:/cars";
    }

    @GetMapping("/car/{id}")
    public String getCar(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        List<Image> images = imageService.findAllImagesByCarId(id);
        List<String> base64Images = images.stream()
                .map(image -> Base64.getEncoder().encodeToString(image.getData()))
                .collect(Collectors.toList());

        model.addAttribute("images", base64Images);
        model.addAttribute("contentTypes", images.stream().map(Image::getContentType).collect(Collectors.toList()));

        return "/view_car";
    }
}