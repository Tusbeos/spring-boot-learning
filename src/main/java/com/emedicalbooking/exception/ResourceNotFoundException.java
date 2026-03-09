package com.emedicalbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ném ra khi không tìm thấy resource trong database.
 * Ví dụ: tìm Doctor theo id không tồn tại → throw new ResourceNotFoundException("Doctor", "id", 5)
 * @ResponseStatus(404) → Spring tự trả về HTTP 404 nếu không có GlobalExceptionHandler
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // Tự động tạo message: "Doctor không tìm thấy với id: 5"
        super(String.format("%s không tìm thấy với %s: %s", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
