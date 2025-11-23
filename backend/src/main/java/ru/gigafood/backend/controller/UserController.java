package ru.gigafood.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.dto.UserDto;
import ru.gigafood.backend.service.UserService;

@RestController
@RequestMapping(value = "/gigafood/api/v1/user", produces = {"application/json"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/redact")
	public ResponseEntity<UserDto.redactResponse> redactUser(@RequestBody UserDto.redactRequest dtoRequest, HttpServletRequest httpRequest){
        UserDto.redactResponse response = userService.redact(dtoRequest, httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/user/redact")
            .body(response);
	}

    @GetMapping("/get")
	public ResponseEntity<UserDto.getUserDataResponce> getUserData(HttpServletRequest httpRequest){
        UserDto.getUserDataResponce response = userService.getUserData(httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/user/get")
            .body(response);
	}


}
