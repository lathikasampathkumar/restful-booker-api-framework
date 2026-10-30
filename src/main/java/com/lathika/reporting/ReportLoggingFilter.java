package com.lathika.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * REST Assured filter that records every HTTP call.
 * <p>
 * Added once in {@code SpecBuilder.requestSpec()}, so it runs around every request with
 * no logging code in the tests. For each call it:
 * <ul>
 *   <li>logs method, URI, status and response time to Log4j2 (console + log file)</li>
 *   <li>writes method, URI, request body, status, time and response body into the
 *       current test's Extent report entry</li>
 * </ul>
 * A failed test therefore shows exactly which request broke and what came back.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class ReportLoggingFilter implements Filter {

    private static final Logger log = LogManager.getLogger(ReportLoggingFilter.class);

    /**
     * Sends the request, then records the request and response.
     *
     * @param request      the outgoing request (method, URI, headers, body)
     * @param responseSpec the response specification of the call
     * @param context      lets the filter pass the request on to be sent
     * @return the response, unchanged
     */
    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {

        log.info("--> {} {}", request.getMethod(), request.getURI());
        Response response = context.next(request, responseSpec);
        log.info("<-- {} in {} ms", response.getStatusCode(), response.getTime());

        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {                              // no entry during @BeforeClass setup calls
            test.info(request.getMethod() + " " + request.getURI());

            Object body = request.getBody();
            if (body != null) {
                test.info("Request body:");
                test.info(MarkupHelper.createCodeBlock(String.valueOf(body)));
            }

            test.info("Status: " + response.getStatusCode() + " | Time: " + response.getTime() + " ms");
            test.info("Response body:");
            test.info(MarkupHelper.createCodeBlock(response.getBody().asPrettyString()));
        }

        return response;
    }
}
