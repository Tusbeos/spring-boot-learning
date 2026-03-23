package com.emedicalbooking.exception;

import com.emedicalbooking.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi 404 - Không tìm thấy resource.
     * Ví dụ: GET /api/doctors/999 → Doctor không tồn tại
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bắt lỗi 409 - Email bị trùng khi đăng ký.
     * Ví dụ: POST /api/auth/register với email đã tồn tại
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException ex) {
        log.warn("Duplicate email: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials attempt");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Email hoặc mật khẩu không chính xác"));
    }

    /**
     * Bắt lỗi validate input - khi dữ liệu request không hợp lệ.
     *
     * Ví dụ: gửi POST /api/auth/register với body:
     *   { "email": "không-phải-email", "password": "" }
     *
     * @Valid trong Controller sẽ trigger exception này trước khi vào Service.
     * Trả về map lỗi theo từng field:
     *   { "email": "Email không hợp lệ", "password": "Mật khẩu không được trống" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        // Thu thập tất cả lỗi validation theo từng field
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Dữ liệu không hợp lệ")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bắt lỗi IllegalStateException - trạng thái không hợp lệ.
     * Ví dụ: khung giờ đã đầy, không thể đặt thêm
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bắt lỗi OptimisticLockException - xử lý race condition khi 2 người đặt cùng slot cuối.
     * @Version trên Schedule entity sẽ throw exception này khi 2 transaction cùng sửa 1 record.
     */
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic locking conflict: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Có người khác vừa đặt slot này, vui lòng thử lại"));
    }

    /**
     * Bắt lỗi 403 - Forbidden (Không có quyền truy cập)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage() != null && !ex.getMessage().isEmpty() ? 
                        ex.getMessage() : "Bạn không có quyền thực hiện hành động này"));
    }

    /**
     * Bắt tất cả lỗi còn lại không được xử lý ở trên.
     * Đây là "lưới an toàn" cuối cùng - trả về 500 Internal Server Error.
     * Log đầy đủ để debug, nhưng KHÔNG lộ stacktrace ra ngoài.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);  // log đầy đủ để developer xem
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Đã xảy ra lỗi hệ thống, vui lòng thử lại sau"));
    }
}
