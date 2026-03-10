package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Markdown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkdownRepository extends JpaRepository<Markdown, Integer> {

    Optional<Markdown> findFirstByDoctorId(int doctorId);
}
