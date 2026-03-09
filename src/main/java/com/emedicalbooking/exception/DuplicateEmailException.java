package com.emedicalbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ném ra khi đăng ký email đã tồn tại trong hệ thống.
 * Ví dụ: user dùng email đã có → throw new DuplicateEmailException("Email đã được sử dụng")
 */
@ResponseStatus(HttpStatus.CONFLICT)  // HTTP 409 Conflict
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super(String.format("Email '%s' đã được sử dụng", email));
    }
}
