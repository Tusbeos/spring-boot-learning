package com.emedicalbooking.config;

import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.DoctorInfosRepository;
import com.emedicalbooking.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds DOCTOR_STATUS codes into all_codes table on startup.
 * Uses INSERT IGNORE equivalent logic via existsByKeyMap check.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DoctorStatusSeeder implements CommandLineRunner {

    private final AllCodeRepository allCodeRepository;
    private final DoctorInfosRepository doctorInfosRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) {
        List<AllCode> initialCodes = List.of(
            // Doctor Statuses
            AllCode.builder().keyMap("SD1").type("DOCTOR_STATUS").valueEn("Pending").valueVi("Chờ duyệt").build(),
            AllCode.builder().keyMap("SD2").type("DOCTOR_STATUS").valueEn("Active").valueVi("Hoạt động").build(),
            AllCode.builder().keyMap("SD3").type("DOCTOR_STATUS").valueEn("Inactive").valueVi("Ngưng hoạt động").build(),
            AllCode.builder().keyMap("SD4").type("DOCTOR_STATUS").valueEn("On Leave").valueVi("Nghỉ phép").build(),
            AllCode.builder().keyMap("SD5").type("DOCTOR_STATUS").valueEn("Suspended").valueVi("Bị khóa").build(),
            // Roles
            AllCode.builder().keyMap("R4").type("ROLE").valueEn("Clinic Manager").valueVi("Quản lý phòng khám").build(),
            // General Statuses
            AllCode.builder().keyMap("G1").type("STATUS").valueEn("Active").valueVi("Hoạt động").build(),
            AllCode.builder().keyMap("G2").type("STATUS").valueEn("Inactive").valueVi("Ngưng hoạt động").build()
        );

        for (AllCode code : initialCodes) {
            if (allCodeRepository.findByKeyMap(code.getKeyMap()).isEmpty()) {
                allCodeRepository.save(code);
                log.info("Seeded code: {} - {} ({})", code.getKeyMap(), code.getValueEn(), code.getType());
            }
        }

        // Initialize existing data with SD1 if status is null
        AllCode sd1 = allCodeRepository.findByKeyMap("SD1").orElse(null);
        if (sd1 != null) {
            // Update Doctors
            doctorInfosRepository.findAll().stream()
                .filter(di -> di.getStatusData() == null)
                .forEach(di -> {
                    di.setStatusData(sd1);
                    doctorInfosRepository.save(di);
                    log.info("Initialized status SD1 for doctor ID: {}", di.getDoctor().getId());
                });

            // Update Specialties
            specialtyRepository.findAll().stream()
                .filter(s -> s.getStatusData() == null)
                .forEach(s -> {
                    s.setStatusData(sd1);
                    specialtyRepository.save(s);
                    log.info("Initialized status SD1 for specialty: {}", s.getName());
                });
        }
    }
}
