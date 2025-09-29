package com.example.WorkWite_Repo_BE.exceptions;

import com.example.WorkWite_Repo_BE.api.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Xử lý lỗi nghiệp vụ chung (ví dụ: chồng ngày, spam booking banner, ...)
     * Trả về message chi tiết cho frontend để hiển thị đúng lỗi.
     * Đã thêm bởi Copilot - Banner booking conflict handler
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.warn("RuntimeException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                RestResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage()) // Trả về message chi tiết
                        .data(null)
                        .build()
        );
    }
    /**
     * Xử lý lỗi không đủ số dư khi thuê banner
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<com.example.WorkWite_Repo_BE.api.RestResponse<Object>> handleInsufficientBalance(InsufficientBalanceException ex) {
        java.util.Map<String, Object> details = new java.util.HashMap<>();
        details.put("userId", ex.getUserId());
        details.put("balance", ex.getBalance());
        details.put("requiredAmount", ex.getRequiredAmount());
        details.put("bannerType", ex.getBannerType());
        return ResponseEntity.badRequest().body(
            com.example.WorkWite_Repo_BE.api.RestResponse.builder()
                .statusCode(400)
                .error("Insufficient Balance")
                .message("Không đủ số dư để thuê banner. Số dư hiện tại: " + ex.getBalance() + ", số tiền cần: " + ex.getRequiredAmount() + ", loại banner: " + ex.getBannerType())
                .data(details)
                .build()
        );
    }
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý lỗi validate từ @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.badRequest().body(
                RestResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(errors)
                        .data(null)
                        .build()
        );
    }

    /**
     * Xử lý Spring ResponseStatusException (vd: throw new ResponseStatusException(...))
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestResponse<Object>> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(
                RestResponse.builder()
                        .statusCode(ex.getStatusCode().value())
                        .error(String.valueOf(ex.getStatusCode().getClass()))
                        .message(ex.getReason()) // ✅ chỉ lấy lý do, không dính "400 BAD_REQUEST"
                        .data(null)
                        .build()
        );
    }

    /**
     * Xử lý HttpException custom
     */
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<RestResponse<Object>> handleHttpException(HttpException ex) {
    log.warn("HttpException: {} - {}", ex.getStatus(), ex.getMessage());

    return ResponseEntity.status(ex.getStatus()).body(
        RestResponse.builder()
            .statusCode(ex.getStatus().value())
            .error(ex.getStatus().getReasonPhrase())
            .message(ex.getMessage())
            .data(null)
            .build()
    );
    }

    /**
     * Xử lý invalid ID
     */
    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdInvalidException(IdInvalidException ex) {
        log.warn("Invalid ID: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(
                RestResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    /**
     * Xử lý không tìm thấy resource
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                RestResponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    /**
     * Xử lý vi phạm nghiệp vụ
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestResponse<Object>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                RestResponse.builder()
                        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        // .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                        .message(ex.getMessage())
                        // .data(null)
                        .build()
        );
    }

    /**
     * Xử lý lỗi không xác định
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                RestResponse.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message("Đã có lỗi xảy ra, vui lòng thử lại sau.") // ✅ không lộ raw message
                        .data(null)
                        .build()
        );
    }
}
