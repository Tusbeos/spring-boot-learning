package com.emedicalbooking.repository;

import com.emedicalbooking.entity.PackageServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PackageServiceItemRepository extends JpaRepository<PackageServiceItem, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PackageServiceItem p WHERE p.medicalPackage.id = :packageId")
    void deleteAllByPackageId(Long packageId);
}
