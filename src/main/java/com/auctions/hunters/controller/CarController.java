package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.bid.BidService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.car.SearchCriteria;
import com.auctions.hunters.service.image.ImageService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
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

@Slf4j
@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class CarController {

    private final CarService carService;
    private final ImageService imageService;
    private final AuctionService auctionService;
    private final UserService userService;
    private final BidService bidService;

    public CarController(CarService carService,
                         ImageService imageService,
                         AuctionService auctionService,
                         UserService userService,
                         BidService bidService) {
        this.carService = carService;
        this.imageService = imageService;
        this.auctionService = auctionService;
        this.userService = userService;
        this.bidService = bidService;
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

        CarsListPaginator pager = (page1, producer1, model1, minYear1, maxYear1, minPrice1, maxPrice1, modelAtr1) -> {
            String loggedUsername = userService.getLoggedUsername();

            SearchCriteria searchCriteria = new SearchCriteria();
            Specification<Car> carSpecification = searchCriteria.buildSpec(producer1, model1, minYear1, maxYear1, minPrice1, maxPrice1);

            Page<Car> carPage = carService.getCarPage(page1, carSpecification);
            int totalPages = carPage.getTotalPages();

            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .toList();

                List<Car> authenticatedUserCarsList = getAuthenticatedUserCarsList(loggedUsername, carPage);

                List<Float> auctionsMinimumPriceList = auctionService.setMinimumPriceForEachPageCar(carPage);

                modelAtr1.addAttribute("carPage", carPage);
                modelAtr1.addAttribute("currentPage", page1);
                modelAtr1.addAttribute("pageNumbers", pageNumbers);
                modelAtr1.addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
                modelAtr1.addAttribute("authenticatedUserCarsListSize",  authenticatedUserCarsList.size());
            }

            return "/car_list";
        };

        return pager.createPaginationListForCars(page, producer, model, minYear, maxYear, minPrice, maxPrice, modelAtr);
    }

    /**
     * Map all the cars from a page and return all cars which username matches the authenticated username.
     */
    @NotNull
    private static List<Car> getAuthenticatedUserCarsList(String loggedUsername, Page<Car> carPage) {
        List<Car> allCarsList = carPage.getContent();
        List<Car> authenticatedUserCarsList = new ArrayList<>();

        for(Car car : allCarsList) {
            if(car.getUser().getUsername().equals(loggedUsername)) {
                authenticatedUserCarsList.add(car);
            }
        }

        return authenticatedUserCarsList;
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
                .toList();

        model.addAttribute("images", base64Images);
        model.addAttribute("contentTypes", images.stream().map(Image::getContentType).collect(Collectors.toList()));

        return "/view_car";
    }

    @GetMapping("/bid/car/{id}")
    public String getAuctionedCar(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        List<Image> images = imageService.findAllImagesByCarId(id);
        List<String> base64Images = images.stream()
                .map(image -> Base64.getEncoder().encodeToString(image.getData()))
                .toList();

        model.addAttribute("images", base64Images);
        model.addAttribute("contentTypes", images.stream().map(Image::getContentType).collect(Collectors.toList()));

        return "/view_auctioned_car";
    }

    @PostMapping("/bid/car/{carId}")
    public String createNewBid( @PathVariable Integer carId, @RequestParam("bidAmount") Integer bidAmount) {
        Auction auction = auctionService.getAuctionByCarId(carId);
        bidService.save(bidAmount, auction);

        log.info("Bidul este {}", bidAmount);
        return "redirect:/auctions"; //redirect to the auctions endpoint
    }
}
