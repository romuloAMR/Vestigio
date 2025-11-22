package com.example.vestigioapi.framework.common.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.vestigioapi.framework.common.dto.ErrorResponseDTO;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(
        BusinessRuleException exception, HttpServletRequest request
    ) {
        
        log.warn("Business Rule violated: {} - URI: {}", exception.getMessage(), request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredJwt(
        ExpiredJwtException exception, HttpServletRequest request
    ) {

        log.info("JWT Expired for URI: {}", request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            ErrorMessages.TOKEN_EXPIRED,
            request.getRequestURI()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<ErrorResponseDTO> handleInvalidJwt(
        Exception exception, HttpServletRequest request
    ) {

        log.info("Invalid JWT signature/format for URI: {}", request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            ErrorMessages.INVALID_TOKEN,
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
        
        log.warn("Forbidden action: {} - URI: {}", exception.getMessage(), request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
        ResourceNotFoundException exception, HttpServletRequest request
    ) {

        log.warn("Resource Not Found: {} - URI: {}", exception.getMessage(), request.getRequestURI());
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(
        DataIntegrityViolationException exception, HttpServletRequest request
    ) {

        log.warn("Data Integrity Violation: {} - URI: {}", exception.getMessage(), request.getRequestURI());

        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ErrorMessages.DATA_CONFLICT,
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

        
        log.warn("Validation failed (422) on URI: {} - Error: {}", request.getRequestURI(), errorMessage);
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            errorMessage,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
        Exception exception, HttpServletRequest request
    ) {
        
        log.error("An unexpected error occurred in URI: {}", request.getRequestURI(), exception);
        
        ErrorResponseDTO errorDetails = new ErrorResponseDTO(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            ErrorMessages.INTERNAL_SEVER_ERROR,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}