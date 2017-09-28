package com.mikaelkulma.kata.stringcalculator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class StringCalculatorTest {

    private StringCalculator calculator;
    private Logger logger;
    private ExceptionWebService webService;

    @Before
    public void setup() {
        logger = mock(Logger.class);
        webService = mock(ExceptionWebService.class);
        calculator = new StringCalculator(logger, webService);
    }

    @Test
    public void should_return_zero_when_given_an_empty_string() {
        assertThat(calculator.add(""), is(0));
    }

    @Test
    public void should_return_the_number_when_given_a_single_number() {
        assertThat(calculator.add("1"), is(1));
    }

    @Test
    public void should_return_the_sum_of_two_numbers() {
        assertThat(calculator.add("1,2"), is(3));
    }

    @Test
    public void should_allow_unknown_number_of_strings() {
        assertThat(calculator.add("1,2,3,4"), is(10));
    }

    @Test
    public void should_allow_newlines_as_separators() {
        assertThat(calculator.add("1,2\n3,4"), is(10));
    }

    @Test
    public void should_support_predefined_delimiters() {
        assertThat(calculator.add("//*\n1,2*3*4"), is(10));
    }

    @Test(expected = NumberFormatException.class)
    public void should_throw_an_exception_when_given_negative_numbers() {
        calculator.add("-1");
    }

    @Test
    public void should_ignore_numbers_larger_than_1000() {
        assertThat(calculator.add("//*\n1,2*3*1001"), is(6));
    }

    @Test
    public void should_support_delimiters_of_any_length() {
        assertThat(calculator.add("//####\n1,2####3####4"), is(10));
    }

    @Test
    public void should_support_multiple_delimiters() {
        assertThat(calculator.add("//[####][****]\n1,2####3****4"), is(10));
    }

    @Test
    public void should_log_each_add_with_a_logger_service() {
        calculator.add("1,2");
        verify(logger).log("3");
    }

    @Test(expected = NumberFormatException.class)
    public void should_send_exceptions_to_a_webservice() {
        calculator.add("-1");
        verify(webService).notifyOfException(contains("Logging has failed"));
    }
}
