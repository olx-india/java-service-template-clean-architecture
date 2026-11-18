package com.olx.boilerplate.ut.infrastructure.exceptions;

import com.olx.boilerplate.infrastructure.exceptions.CustomExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class CustomExceptionHandlerTest {

    @Test
    void returnsAllFieldAndObjectErrors() throws Exception {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "user");
        bindingResult.addError(new FieldError("user", "email", "must be a valid email"));
        bindingResult.addError(new FieldError("user", "name", "must not be blank"));
        bindingResult.addError(new ObjectError("user", "fields must agree"));
        var parameter = new MethodParameter(String.class.getMethod("substring", int.class), 0);
        var exception = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = new CustomExceptionHandler().handleException(exception,
                        new ServletWebRequest(new MockHttpServletRequest()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOfSatisfying(ProblemDetail.class, problem -> {
            assertThat(problem.getDetail()).isEqualTo("email: must be a valid email");
            assertThat((List<?>) problem.getProperties().get("errors"))
                            .extracting("field", "message")
                            .containsExactly(tuple("email", "must be a valid email"),
                                            tuple("name", "must not be blank"), tuple(null, "fields must agree"));
        });
    }
}
