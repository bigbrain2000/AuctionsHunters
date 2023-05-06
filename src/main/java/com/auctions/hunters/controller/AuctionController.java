package com.auctions.hunters.controller;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.car.SearchCriteria;
import com.auctions.hunters.service.ml.RecommendationServiceImpl;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class AuctionController {

    private final CarService carService;
    private final AuctionService auctionService;
    private final UserService userService;
    private final RecommendationServiceImpl recommendationService;

    public AuctionController(CarService carService,
                             AuctionService auctionService,
                             UserService userService,
                             RecommendationServiceImpl recommendationService) {
        this.carService = carService;
        this.auctionService = auctionService;
        this.userService = userService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/create/auction/car/{id}")
    public String getAuction(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

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

    @GetMapping("/auctions")
    public String getAuctions(
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

            SearchCriteria searchCriteria = new SearchCriteria();
            Specification<Car> carSpecification = searchCriteria.buildSpec(producer1, model1, minYear1, maxYear1, minPrice1, maxPrice1);

            //filter for displaying only the cars that DO NOT MATCH the authenticated user id
            carSpecification = carSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("user"), user));

            //retrieve all ACTIVE auctions
            Page<Car> carPage = carService.getCarPage(page1, carSpecification);
            List<Auction> allActiveAuctionsList = auctionService.findAllActiveAuctions();
            if (allActiveAuctionsList.isEmpty()) {
                return "/no_car";
            }

            //get the cars from the auctions
            List<Car> carList = allActiveAuctionsList.stream()
                    .map(Auction::getCar)
                    .toList();

            Page<Car> updatedPage = new PageImpl<>(carList, carPage.getPageable(), carPage.getTotalElements() - carList.size());
            int totalPages = updatedPage.getTotalPages();

            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .toList();

                //set the minimum price for each car
                List<Float> auctionsMinimumPriceList = auctionService.setCurrentPriceForEachCarPage(updatedPage);

                modelAtr1.addAttribute("carPage", updatedPage);
                modelAtr1.addAttribute("currentPage", page1);
                modelAtr1.addAttribute("pageNumbers", pageNumbers);
                modelAtr1.addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
                modelAtr1.addAttribute("allActiveAuctionsList", allActiveAuctionsList);
            }

            return "/auction_list";
        };

        return pager.createPaginationListForCars(page, producer, model, minYear, maxYear, minPrice, maxPrice, modelAtr);
    }


    @GetMapping("/auctions/recommended")
    public String getRecommendedAuctions(
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

            List<Car> recommendedAuctionedCarsList = recommendationService.getRecommendedAuctionedCarsForUser(user);
            List<Auction> recommendedAuctionsList = recommendationService.getUnfinishedRecommendedAuctions(user);

            SearchCriteria searchCriteria = new SearchCriteria();
            Specification<Car> carSpecification = searchCriteria.buildSpec(producer1, model1, minYear1, maxYear1, minPrice1, maxPrice1);
            carSpecification = carSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("user"), user));

            // Modify carSpecification to include the condition
            carSpecification = carSpecification.and((root, query, criteriaBuilder) -> root.in(recommendedAuctionedCarsList));

            Page<Car> carPage = carService.getCarPage(page1, carSpecification);
            Page<Car> updatedCarPage = auctionService.manageFinishedAuctions(user, carPage);

            int totalPages = updatedCarPage.getTotalPages();

            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .toList();

                List<Car> authenticatedUserCarsList = carService.getAuthenticatedUserCarsList();
                List<Float> auctionsMinimumPriceList = auctionService.setCurrentPriceForEachCarPage(updatedCarPage);

                modelAtr1.addAttribute("carPage", updatedCarPage);
                modelAtr1.addAttribute("currentPage", page1);
                modelAtr1.addAttribute("pageNumbers", pageNumbers);
                modelAtr1.addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
                modelAtr1.addAttribute("authenticatedUserCarsListSize", authenticatedUserCarsList.size());
                modelAtr1.addAttribute("recommendedAuctionsList", recommendedAuctionsList);
            }

            return "/recommended_car_list";
        };

        return pager.createPaginationListForCars(page, producer, model, minYear, maxYear, minPrice, maxPrice, modelAtr);
    }
}
