package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.bid.BidService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.car.SearchCriteria;
import com.auctions.hunters.service.image.ImageService;
import com.auctions.hunters.service.ml.RecommendationService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;
import java.util.List;
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
    private final RecommendationService recommendationService;

    public CarController(CarService carService,
                         ImageService imageService,
                         AuctionService auctionService,
                         UserService userService,
                         BidService bidService,
                         RecommendationService recommendationService) {
        this.carService = carService;
        this.imageService = imageService;
        this.auctionService = auctionService;
        this.userService = userService;
        this.bidService = bidService;
        this.recommendationService = recommendationService;
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
            User user = userService.findByUsername(loggedUsername);

            List<Auction> recommendedAuctionsList = recommendationService.getUnfinishedRecommendedAuctions(user);

            SearchCriteria searchCriteria = new SearchCriteria();
            Specification<Car> carSpecification = searchCriteria.buildSpec(producer1, model1, minYear1, maxYear1, minPrice1, maxPrice1);

            //filter for displaying only the cars that MATCH the authenticated user id
            carSpecification = carSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user));

            Page<Car> carPage = carService.getCarPage(page1, carSpecification);
            int totalPages = carPage.getTotalPages();

            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .toList();

                //hook needed for displaying the car list if there are ay cars to display,
                //a message will appear otherwise
                List<Car> authenticatedUserCarsList = carService.getAuthenticatedUserCarsList();

                //set the minimum price for each car
                List<Float> auctionsMinimumPriceList = auctionService.setCurrentPriceForEachCarPage(carPage);

                modelAtr1.addAttribute("carPage", carPage);
                modelAtr1.addAttribute("currentPage", page1);
                modelAtr1.addAttribute("pageNumbers", pageNumbers);
                modelAtr1.addAttribute("authenticatedUserCarsListSize", authenticatedUserCarsList.size());
                modelAtr1.addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList); //TODO: schimbat numele modelului in current
                modelAtr1.addAttribute("recommendedAuctionsList", recommendedAuctionsList);
            }

            return "/car_list";
        };

        return pager.createPaginationListForCars(page, producer, model, minYear, maxYear, minPrice, maxPrice, modelAtr);
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
        getAllImagesForTheSavedCar(id, model);

        return "/view_car";
    }

    @GetMapping("/bid/car/{id}")
    public String getAuctionedCar(@PathVariable Integer id, Model model) {
        getAllImagesForTheSavedCar(id, model);

        return "/view_auctioned_car";
    }

    @PostMapping("/bid/car/{carId}")
    public String createNewBid(@PathVariable Integer carId, @RequestParam("bidAmount") Integer bidAmount) {
        Auction auction = auctionService.getAuctionByCarId(carId);

        try {
            bidService.save(bidAmount, auction);
        } catch (LowBidAmountException e) {
            return "redirect:/auctions"; //TODO: add a new page for error
        }

        return "redirect:/auctions"; //redirect to the auctions endpoint
    }

    private void getAllImagesForTheSavedCar(Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        List<Image> images = imageService.findAllImagesByCarId(id);
        List<String> base64Images = images.stream()
                .map(image -> Base64.getEncoder().encodeToString(image.getData()))
                .toList();

        model.addAttribute("images", base64Images);
        model.addAttribute("contentTypes", images.stream().map(Image::getContentType).toList());
    }
}
