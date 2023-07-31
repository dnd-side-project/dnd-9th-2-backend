package org.baggle.global.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.baggle.global.error.dto.ErrorBaseResponse;
import org.baggle.global.error.exception.ErrorCode;
import org.baggle.global.error.exception.InvalidValueException;
import org.baggle.global.error.exception.UnauthorizedException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException | InvalidValueException e) {
            log.error("filter exception: ", e);
            handleJwtException(response, e);
        } catch (Exception ee) {
            log.error("filter exception: ", ee);
            handleException(response);
        }
    }

    private void handleJwtException(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        if (e instanceof UnauthorizedException ue) {
            response.setStatus(ue.getErrorCode().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ErrorBaseResponse.of(ue.getErrorCode())));
        } else if (e instanceof InvalidValueException ie) {
            response.setStatus(ie.getErrorCode().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ErrorBaseResponse.of(ie.getErrorCode())));
        }
    }

    private void handleException(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(ErrorBaseResponse.of(ErrorCode.INTERNAL_SERVER_ERROR)));
    }
}
