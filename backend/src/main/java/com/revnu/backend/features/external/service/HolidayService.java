package com.revnu.backend.features.external.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.revnu.backend.features.external.dto.HolidayDto;

@Service
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private final RestTemplate restTemplate;

    public HolidayService() {
        this.restTemplate = new RestTemplate();
    }

    @Cacheable(value = "philippineHolidays", key = "#year")
    public List<HolidayDto> getPhilippineHolidays(int year) {
        String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/PH";

        try {
            HolidayDto[] response = restTemplate.getForObject(url, HolidayDto[].class);
            if (response != null) {
                return Arrays.asList(response);
            }
        } catch (RestClientException e) {
            logger.warn("Failed to fetch holidays from external API: {}", e.getMessage());
        }

        return Collections.emptyList();
    }
}
