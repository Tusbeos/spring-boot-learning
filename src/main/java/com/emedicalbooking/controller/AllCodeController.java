package com.emedicalbooking.controller;

import com.emedicalbooking.dto.response.AllCodeResponse;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.service.AllCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/all-codes")
@RequiredArgsConstructor
public class AllCodeController {

    private final AllCodeService allCodeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AllCodeResponse>>> getByType(
            @RequestParam String type) {
        List<AllCodeResponse> data = allCodeService.getByType(type);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thành công", data));
    }
}
