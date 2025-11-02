package com.example.back_end.exceptions;

import com.example.back_end.dtos.errors.ErrorResponse;
import com.example.back_end.dtos.errors.Violation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // 400 - corpo inválido (JSON malformado, enum inválido, etc.)
        @ExceptionHandler(HttpMessageNotReadableException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.BAD_REQUEST,
                                "Corpo da requisição inválido ou malformado.", req.getRequestURI());
        }

        // 400 - validação do corpo (@Valid em @RequestBody)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
                List<Violation> errors = new ArrayList<>();
                ex.getBindingResult().getFieldErrors().forEach(fe -> errors
                                .add(new Violation(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue())));
                ex.getBindingResult().getGlobalErrors().forEach(
                                ge -> errors.add(new Violation(ge.getObjectName(), ge.getDefaultMessage(), null)));
                return new ErrorResponse(OffsetDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                                "Erro de validação.", errors, req.getRequestURI());
        }

        // 400 - validação de parâmetros (@PathVariable/@RequestParam via @Validated)
        @ExceptionHandler(ConstraintViolationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
                List<Violation> errors = ex.getConstraintViolations().stream()
                                .map(v -> new Violation(v.getPropertyPath().toString(), v.getMessage(),
                                                v.getInvalidValue()))
                                .toList();
                return new ErrorResponse(OffsetDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                                "Parâmetros inválidos.", errors, req.getRequestURI());
        }

        // 400 - tipo inválido na rota/query (ex.: id=abc)
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
                List<Violation> errors = List.of(new Violation(
                                ex.getName(),
                                "Tipo inválido" + (ex.getRequiredType() != null
                                                ? " (esperado: " + ex.getRequiredType().getSimpleName() + ")"
                                                : ""),
                                ex.getValue()));
                return new ErrorResponse(OffsetDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                                "Parâmetros da rota ou query string inválidos.", errors, req.getRequestURI());
        }

        // 400 - parâmetro obrigatório ausente (?page=..., etc.)
        @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleMissingParam(org.springframework.web.bind.MissingServletRequestParameterException ex,
                        HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.BAD_REQUEST,
                                "Parâmetro obrigatório ausente: " + ex.getParameterName(), req.getRequestURI());
        }

        // 400 - regras de negócio
        @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleBadRequest(RuntimeException ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
        }

        // 401 - autenticação falhou (genérico)
        @ExceptionHandler(AuthenticationException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public ErrorResponse handleAuth(AuthenticationException ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.UNAUTHORIZED,
                                "Não autenticado ou token inválido.", req.getRequestURI());
        }

        // 403 - acesso negado
        @ExceptionHandler(AccessDeniedException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public ErrorResponse handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.FORBIDDEN,
                                "Acesso negado.", req.getRequestURI());
        }

        // 404 - recurso não encontrado
        @ExceptionHandler(EntityNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.NOT_FOUND,
                                ex.getMessage() != null ? ex.getMessage() : "Recurso não encontrado.",
                                req.getRequestURI());
        }

        // 405 - método HTTP não suportado
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
        public ErrorResponse handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.METHOD_NOT_ALLOWED,
                                "Método HTTP não suportado para esta rota.", req.getRequestURI());
        }

        // 409 - violação de integridade (unicidade, FK, etc.)
        @ExceptionHandler(DataIntegrityViolationException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
                String msg = "Violação de integridade de dados.";
                String root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : null;
                if (root != null && !root.isBlank())
                        msg += " " + root;
                return ErrorResponse.simple(HttpStatus.CONFLICT, msg, req.getRequestURI());
        }

        // 500 - fallback
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse handleGeneric(Exception ex, HttpServletRequest req) {
                return ErrorResponse.simple(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Erro interno inesperado.", req.getRequestURI());
        }
}
