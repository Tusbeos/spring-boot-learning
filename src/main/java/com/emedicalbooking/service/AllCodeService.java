package com.emedicalbooking.service;

import com.emedicalbooking.dto.response.AllCodeResponse;
import com.emedicalbooking.repository.AllCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllCodeService {

    private final AllCodeRepository allCodeRepository;

    public List<AllCodeResponse> getByType(String type) {
        return allCodeRepository.findByType(type)
                .stream()
                .map(code -> AllCodeResponse.builder()
                        .keyMap(code.getKeyMap())
                        .valueEn(code.getValueEn())
                        .valueVi(code.getValueVi())
                        .build())
                .collect(Collectors.toList());
    }
}
