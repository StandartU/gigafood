package ru.gigafood.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.dto.DishDto;
import ru.gigafood.backend.entity.Meal;
import ru.gigafood.backend.service.DishService;

@RestController
@RequestMapping(value = "/gigafood/api/v1/dish", produces = {"application/json"})
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping("/analyze")
	public ResponseEntity<DishDto.analyzeResponse> analyze(@RequestBody DishDto.analyzeRequest dtoRequest, HttpServletRequest httpRequest) throws Exception {
        DishDto.analyzeResponse response = dishService.analyze(dtoRequest, httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/analyze")
            .body(response);
	}

    @PostMapping("/get/{uuid}")
	public ResponseEntity<DishDto.getDichResponse> getDish(@PathVariable String uuid, HttpServletRequest httpRequest) throws Exception {
        DishDto.getDichResponse response = dishService.getDish(uuid, httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/get")
            .body(response);
	}

    @PostMapping("/redact/{uuid}")
	public ResponseEntity<DishDto.redactResponse> redactDish(@PathVariable String uuid, HttpServletRequest httpRequest, @RequestBody DishDto.redactRequest dtoRequest) throws Exception {
        DishDto.redactResponse response = dishService.redact(uuid, httpRequest, dtoRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/redact")
            .body(response);
	}

    @PostMapping("/all")
	public ResponseEntity<List<Meal>> getAllDishes(HttpServletRequest httpRequest) throws Exception {
        List<Meal> response = dishService.all(httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/all")
            .body(response);
	}

    @PostMapping("/get_photo/{photoUrl}")
	public ResponseEntity<Resource> getPhotoDish(@PathVariable String photoUrl, HttpServletRequest httpRequest) throws Exception {
        Resource response = dishService.getPhoto(photoUrl, httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/get_photo")
            .body(response);
	}
}
