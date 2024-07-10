package org.prodacc.webapi.controllers

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<ErrorDetails> {
        val errorDetails = ErrorDetails(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Entity not found",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointerException(ex: NullPointerException, request: WebRequest): ResponseEntity<ErrorDetails> {
        val errorDetails = ErrorDetails(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Null value encountered",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorDetails> {
        val errorDetails = ErrorDetails(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Illegal argument encountered",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<ErrorDetails> {
        val errorDetails = ErrorDetails(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "An unexpected error occurred",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

data class ErrorDetails(
    val timestamp: LocalDateTime,
    val message: String,
    val details: String
)