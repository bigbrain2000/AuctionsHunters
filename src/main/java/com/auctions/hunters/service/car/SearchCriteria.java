package com.auctions.hunters.service.car;

import com.auctions.hunters.model.Car;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toUpperCase;

@Data
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;

    public SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SearchCriteria() {
    }

    /**
     * Method builds up a {@link Specification} object that is used to construct a JPA query for the {@link Car} entity.
     */
    public Specification<Car> buildSpec(String producer, String model, Integer minYear, Integer maxYear, Integer minPrice, Integer maxPrice) {
        Specification<Car> spec = Specification.where(null);
        if (producer != null && !producer.isEmpty()) {
            if (isUpperCase(model.charAt(0))) {
                spec = spec.and(new CarSpecification(new SearchCriteria("producer", ":", producer)));
            }

            String insensitiveCaseModelString = getStringWithFirstCharToUpperCase(producer);
            spec = spec.and(new CarSpecification(new SearchCriteria("producer", ":", insensitiveCaseModelString)));
        }
        if (model != null && !model.isEmpty()) {
            if (isUpperCase(model.charAt(0))) {
                spec = spec.and(new CarSpecification(new SearchCriteria("model", ":", model)));
            }

            String insensitiveCaseModelString = getStringWithFirstCharToUpperCase(model);
            spec = spec.and(new CarSpecification(new SearchCriteria("model", ":", insensitiveCaseModelString)));
        }
        if (minYear != null) {
            spec = spec.and(new CarSpecification(new SearchCriteria("modelYear", ">", minYear)));
        }
        if (maxYear != null) {
            spec = spec.and(new CarSpecification(new SearchCriteria("modelYear", "<", maxYear)));
        }
        if (minPrice != null) {
            spec = spec.and(new CarSpecification(new SearchCriteria("minimumPrice", ">", minPrice)));
        }
        if (maxPrice != null) {
            spec = spec.and(new CarSpecification(new SearchCriteria("minimumPrice", "<", maxPrice)));
        }
        return spec;
    }

    private String getStringWithFirstCharToUpperCase(String model) {
        char firstCharacter = model.charAt(0);
        char firstCharacterToUpperCase = toUpperCase(firstCharacter);
        return model.replace(firstCharacter, firstCharacterToUpperCase);
    }
}