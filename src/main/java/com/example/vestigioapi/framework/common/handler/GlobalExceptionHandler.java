package com.example.vestigioapi.framework.common.handler;

import com.example.vestigioapi.framework.common.dto.ErrorResponseDTO;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.GameFrameworkException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String getMessage(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return key; // Fallback to key if not found
        }
    }

    private String getMessage(GameFrameworkException ex) {
        return getMessage(ex.getMessageKey(), ex.getArgs());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(
        BusinessRuleException exception, HttpServletRequest request
    ) {
        String message = getMessage(exception);
        log.warn("Business Rule violated: {} - URI: {}", message, request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredJwt(
        ExpiredJwtException exception, HttpServletRequest request
    ) {

        log.info("JWT Expired for URI: {}", request.getRequestURI());
        String message = getMessage(ErrorMessages.TOKEN_EXPIRED);

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            message,
            request.getRequestURI()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<ErrorResponseDTO> handleInvalidJwt(
        Exception exception, HttpServletRequest request
    ) {

        log.info("Invalid JWT signature/format for URI: {}", request.getRequestURI());
        String message = getMessage(ErrorMessages.INVALID_TOKEN);

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            message,
            request.getRequestURI()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
        BadCredentialsException exception, HttpServletRequest request
    ) {
        
        log.info("Bad Credentials: {} for URI: {}", exception.getMessage(), request.getRequestURI());
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenAction(
        ForbiddenActionException exception, HttpServletRequest request
    ) {
        String message = getMessage(exception);
        log.warn("Forbidden action: {} - URI: {}", message, request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
        ResourceNotFoundException exception, HttpServletRequest request
    ) {
        String message = getMessage(exception);
        log.warn("Resource Not Found: {} - URI: {}", message, request.getRequestURI());
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(
        DataIntegrityViolationException exception, HttpServletRequest request
    ) {

        log.warn("Data Integrity Violation: {} - URI: {}", exception.getMessage(), request.getRequestURI());
        String message = getMessage(ErrorMessages.DATA_CONFLICT);

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            message,
            request.getRequestURI()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
        MethodArgumentNotValidException exception, HttpServletRequest request
    ) {

        String errorMessage = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse(ErrorMessages.INVALID_INPUT);
        
        String resolvedMessage = getMessage(errorMessage);

        log.warn("Validation failed (422) on URI: {} - Error: {}", request.getRequestURI(), resolvedMessage);
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            resolvedMessage,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
        Exception exception, HttpServletRequest request
    ) {
        
        log.error("An unexpected error occurred in URI: {}", request.getRequestURI(), exception);
        String message = getMessage(ErrorMessages.INTERNAL_SEVER_ERROR);
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
