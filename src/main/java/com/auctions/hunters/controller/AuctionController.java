package com.auctions.hunters.controller;


import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.car.SearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    public AuctionController(CarService carService,
                             AuctionService auctionService) {
        this.carService = carService;
        this.auctionService = auctionService;
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
            SearchCriteria searchCriteria = new SearchCriteria();
            Specification<Car> carSpecification = searchCriteria.buildSpec(producer1, model1, minYear1, maxYear1, minPrice1, maxPrice1);

            Page<Car> carPage = carService.getCarPage(page1, carSpecification);
            int totalPages = carPage.getTotalPages();

            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .toList();

                List<Float> auctionsMinimumPriceList = auctionService.setMinimumPriceForEachPageCar(carPage);

                modelAtr1.addAttribute("carPage", carPage);
                modelAtr1.addAttribute("currentPage", page1);
                modelAtr1.addAttribute("pageNumbers", pageNumbers);
                modelAtr1.addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
            }

            return "/auction_list";
        };

        return pager.createPaginationListForCars(page, producer, model, minYear, maxYear, minPrice, maxPrice, modelAtr);
    }
}
