package ru.gigafood.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.dto.ReportDto;
import ru.gigafood.backend.service.ReportService;

@RestController
@RequestMapping(value = "/gigafood/api/v1/report", produces = {"application/json"})
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/week")
	public ResponseEntity<ReportDto.getWeekReportResponce> weekReport(HttpServletRequest httpRequest) throws JsonMappingException, JsonProcessingException {
        ReportDto.getWeekReportResponce response = reportService.weekReport(httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/report/week")
            .body(response);
	}

    @GetMapping("/day")
	public ResponseEntity<ReportDto.getDailyReportResponce> dayReport(HttpServletRequest httpRequest) {
        ReportDto.getDailyReportResponce response = reportService.dayReport(httpRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, "/gigafood/api/v1/report/day")
            .body(response);
	}
}
