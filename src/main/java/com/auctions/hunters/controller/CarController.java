package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.car.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@Slf4j
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class CarController {

    private final CarService carService;
    private final CarRepository carRepository;

    public CarController(CarService carService,
                         CarRepository carRepository) {
        this.carService = carService;
        this.carRepository = carRepository;
    }

    @GetMapping("/car/add")
    public String getCar(@ModelAttribute @Valid Car car) {
        return "/add_car";
    }

    @PostMapping("/car/add")
    public String addCar(@RequestParam("vin") String vin) {
        try {
            Car car = carService.save(vin);
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

        return "/view_car";
    }

    //    @GetMapping("/getCar/{id}")
    //    public String getCar(@PathVariable Integer id, Model model) {
    //        Car car = carService.getCarById(id);
    //        model.addAttribute("car", car);
    //
    //        Image image = imageService.findImageByCarId(car);
    //        model.addAttribute("image", Base64.encodeBase64String(image.getData()));
    //        model.addAttribute("contentType", image.getContentType());
    //        log.info("Retrieved car {}", id);
    //        return "/view_car";
    //    }


}
