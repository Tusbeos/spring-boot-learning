package com.emedicalbooking.config;

import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.repository.AllCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApprovalAllCodeInitializer implements CommandLineRunner {

    private final AllCodeRepository allCodeRepository;

    @Override
    public void run(String... args) {
        List<AllCode> approvalStatuses = List.of(
                AllCode.builder()
                        .keyMap("AR1")
                        .type("APPROVAL_REQUEST_STATUS")
                        .valueEn("Pending")
                        .valueVi("Chờ duyệt")
                        .build(),
                AllCode.builder()
                        .keyMap("AR2")
                        .type("APPROVAL_REQUEST_STATUS")
                        .valueEn("Approved")
                        .valueVi("Đã duyệt")
                        .build(),
                AllCode.builder()
                        .keyMap("AR3")
                        .type("APPROVAL_REQUEST_STATUS")
                        .valueEn("Rejected")
                        .valueVi("Từ chối")
                        .build(),
                AllCode.builder()
                        .keyMap("AR4")
                        .type("APPROVAL_REQUEST_STATUS")
                        .valueEn("Cancelled")
                        .valueVi("Đã hủy")
                        .build()
        );

        for (AllCode status : approvalStatuses) {
            if (allCodeRepository.findByKeyMap(status.getKeyMap()).isEmpty()) {
                allCodeRepository.save(status);
            }
        }
    }
}
