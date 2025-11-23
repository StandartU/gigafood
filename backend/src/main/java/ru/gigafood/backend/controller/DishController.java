package ru.gigafood.backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.dto.DishDto;
import ru.gigafood.backend.entity.Meal;
import ru.gigafood.backend.service.DishService;

@RestController
@CrossOrigin
@RequestMapping(value = "/gigafood/api/v1/dish", produces = {"application/json"})
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping(value =  "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
	public ResponseEntity<DishDto.analyzeResponse> analyze(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) throws Exception {
        DishDto.analyzeRequest dtoRequest = new DishDto.analyzeRequest(file);
        DishDto.analyzeResponse response = dishService.analyze(dtoRequest, httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/analyze")
            .body(response);
	}

    @PostMapping("/get/{uuid}")
    @CrossOrigin
	public ResponseEntity<DishDto.getDichResponse> getDish(@PathVariable String uuid, HttpServletRequest httpRequest) throws Exception {
        DishDto.getDichResponse response = dishService.getDish(uuid, httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/get")
            .body(response);
	}

    @PostMapping("/redact/{uuid}")
    @CrossOrigin
	public ResponseEntity<DishDto.redactResponse> redactDish(@PathVariable String uuid, HttpServletRequest httpRequest, @RequestBody DishDto.redactRequest dtoRequest) throws Exception {
        DishDto.redactResponse response = dishService.redact(uuid, httpRequest, dtoRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/redact")
            .body(response);
	}

    @PostMapping(value = "/all")
    @CrossOrigin
	public ResponseEntity<List<Meal>> getAllDishes(HttpServletRequest httpRequest) throws Exception {
        List<Meal> response = dishService.all(httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/all")
            .body(response);
	}

    @PostMapping(value = "/get_photo/{photoUrl}", produces = {
        MediaType.IMAGE_JPEG_VALUE,
        MediaType.IMAGE_PNG_VALUE,
        MediaType.IMAGE_GIF_VALUE
    })
    @CrossOrigin
	public ResponseEntity<Resource> getPhotoDish(@PathVariable String photoUrl, HttpServletRequest httpRequest) throws Exception {
        Map<String, Object> data = dishService.getPhoto(photoUrl, httpRequest);

        System.out.println(data.get("path"));
        String contentType = Files.probeContentType((Path) data.get("path"));
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/dish/get_photo")
            .contentType(MediaType.parseMediaType(contentType))
            .body((Resource) data.get("photo"));
	}
}
