package com.auctions.hunters.controller;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.image.ImageService;
import com.auctions.hunters.service.user.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@Slf4j
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "/seller")
public class SellerController {

    private final CarService carService;
    private final SellerService sellerService;
    private final ImageService imageService;
    private final CarRepository carRepository;
    private final AuctionService auctionService;

    public SellerController(CarService carService,
                            SellerService sellerService,
                            ImageService imageService,
                            CarRepository carRepository,
                            AuctionService auctionService) {
        this.carService = carService;
        this.sellerService = sellerService;
        this.imageService = imageService;
        this.carRepository = carRepository;
        this.auctionService = auctionService;
    }

    @GetMapping("/addCar")
    public String getCar(@ModelAttribute @Valid Car car) {
        return "/add_car";
    }

    @PostMapping("/addCar")
    public String addCar(@ModelAttribute @Valid Car car, RedirectAttributes redirectAttributes,
                         @RequestParam("files") List<MultipartFile> files) {
        User user = sellerService.findByUsername(sellerService.getLoggedUsername());
        car.setUser(user);
        carService.save(car);
        imageService.save(files);

        redirectAttributes.addFlashAttribute("message", "Car added successfully");
        return "/homepage";
    }

    @GetMapping("/cars")
    public String getCars(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 10; // number of elements in a page
        Page<Car> carPage = carRepository.findAll(PageRequest.of(page, pageSize));
        model.addAttribute("carPage", carPage);
        model.addAttribute("currentPage", page);

        int totalPages = carPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        } else {
            model.addAttribute("pageNumbers", null);
            model.addAttribute("emptyMessage", "There are no cars to display.");
            return "/no_car";

        }

        return "/car_list";
    }

    @GetMapping("/cars/{id}")
    public String deleteCar(@PathVariable Integer id) {
        carService.deleteById(id);
        return "redirect:/seller/cars";
    }

    @GetMapping("/getCar/{id}")
    public String getCar(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        Image image = imageService.findImageByCarId(car);
        model.addAttribute("image", Base64.encodeBase64String(image.getData()));
        log.info("Am preluat masina {}", id);
        return "/view_car";
    }

    @GetMapping("/createAuction/car/{id}")
    public String getAuction(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        Image image = imageService.findImageByCarId(car);
        model.addAttribute("image", Base64.encodeBase64String(image.getData()));

        return "/auction";
    }

    @PostMapping("/createAuction/car/{id}")
    public String createAuction(@PathVariable Integer id, @RequestParam("minimumPrice") String minimumPriceStr, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);

        Image image = imageService.findImageByCarId(car);
        model.addAttribute("image", Base64.encodeBase64String(image.getData()));

        float minimumPrice = Float.parseFloat(minimumPriceStr);
        Auction auction = new Auction();
        auction.setMinimumPrice(minimumPrice);
        auctionService.save(car, auction);

        return "/auction";
    }

}
