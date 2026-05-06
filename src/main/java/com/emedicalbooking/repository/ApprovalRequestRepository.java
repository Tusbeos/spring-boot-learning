package com.emedicalbooking.repository;

import com.emedicalbooking.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    Optional<ApprovalRequest> findByRequestCode(String requestCode);

    List<ApprovalRequest> findByClinic_IdAndStatusData_KeyMap(Long clinicId, String statusId);

    List<ApprovalRequest> findByTargetTypeAndTargetId(String targetType, Long targetId);

    boolean existsByRequestCode(String requestCode);
}
