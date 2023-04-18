package com.auctions.hunters.controller;

import org.springframework.ui.Model;

@FunctionalInterface
public interface CarsListPaginator {
    String createPaginationListForCars(int page, String producer, String model, Integer minYear, Integer maxYear, Integer minPrice, Integer maxPrice, Model modelAtr);
}
