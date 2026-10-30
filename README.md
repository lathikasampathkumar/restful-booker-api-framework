# Restful Booker – API Automation Framework

![API Tests](https://github.com/lathikasampathkumar/restful-booker-api-framework/actions/workflows/api-tests.yml/badge.svg)

A layered **REST Assured + TestNG** framework for the public [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) API, built the way I'd structure API tests on a real product: config-driven, independent tests, format-agnostic test data, contract checks, and every request/response captured in the report.

## Tech stack

| Area | Tools |
| --- | --- |
| Language / build | Java 11+, Maven |
| HTTP + assertions | REST Assured 5, TestNG 7 |
| Serialization | Jackson (POJOs) |
| Contract testing | REST Assured JSON Schema Validator |
| Test data | JSON and Excel (Apache POI) |
| Reporting / logging | Extent Reports 5, Log4j2 |
| CI | GitHub Actions, Jenkins (Jenkinsfile) |

## Project structure

```
src/main/java/com/lathika
├── api/          AuthApi, BookingApi – one class per resource (the "page objects" of an API framework)
├── core/         SpecBuilder (request/response specifications, timeouts, SLA), TokenManager (cached token, refreshed before expiry)
├── config/       ConfigReader – properties file, overridable with -Dkey=value
├── data/         BookingDataFactory – unique test data per run ("auto_" prefix + timestamp)
├── models/       Request/response POJOs
├── reporting/    ReportManager, ExtentTestManager (ThreadLocal), ReportLoggingFilter (logs every request/response)
├── listener/     TestListener – pass/fail/skip into the report and log
└── utils/        TestDataReader, DataReader (picks JSON or Excel reader by file extension), ExcelReader

src/main/resources
├── config.properties        base URI, credentials, timeouts, SLA, token lifetime
├── log4j2.xml               console + logs/test-run.log
├── schemas/                 JSON schemas for contract tests
└── testdata/                booking.json, missing-fields.json, missing-fields.xlsx

src/test/java/com/lathika
├── base/          BaseTest – API clients, token before each test, listener
├── dataproviders/ BookingDataProvider – reads JSON or Excel based on config
└── tests/         Create, Get, Lifecycle (CRUD), Schema, Validation, Auth
```

## Design decisions

- **API classes return raw `Response` objects.** The same method serves positive and negative tests; the test decides what's correct.
- **Specs are built fresh per call**, so parallel tests can't leak headers into each other.
- **Config first from `-D`, then the properties file.** CI switches environments without code changes.
- **TokenManager** caches one token for the suite and refreshes it a configurable buffer before expiry (`token.ttl.seconds`, `token.refresh.buffer.seconds`).
- **Independent tests**: each test creates its own data and cleans up in `@AfterMethod(alwaysRun = true)`.
- **Format-agnostic test data**: `DataReader` chooses the JSON or Excel reader by file extension, so the same DataProvider works with either.
- **Test pyramid**: business rules are covered at the API layer, where tests are fast and stable; UI tests are kept for key user journeys (see my UI framework).

## Test coverage

| Test class | Group(s) | What it checks |
| --- | --- | --- |
| CreateBookingTest | smoke, regression | Valid booking is created; response matches the request |
| BookingLifecycleTest | e2e, regression | Create → GET → PUT → PATCH (other fields unchanged) → DELETE → GET 404 |
| BookingSchemaTest | contract, regression | GET response matches `schemas/booking-schema.json` |
| GetBookingTest | negative, regression | Unknown id returns 404 |
| CreateBookingValidationTest | negative, known-defects | Each missing required field should return 400 (data from JSON or Excel) |
| BookingAuthTest | negative, known-defects | Missing / invalid token should return 401 |

### Known defects found in Restful Booker

Restful Booker is a practice API with deliberate bugs. These tests assert the **correct** REST behaviour and are grouped as `known-defects`, excluded from the default run:

| Scenario | Expected | Actual |
| --- | --- | --- |
| Create booking with a required field missing | 400 Bad Request | 500 / 200 |
| Update with missing or invalid token | 401 Unauthorized | 403 Forbidden |
| Login with wrong password | 401 Unauthorized | 200 with `{"reason":"Bad credentials"}` |
| Delete booking | 204 No Content | 201 Created |

## How to run

**Eclipse:** right-click `testng.xml` → Run As → TestNG Suite.

**Command line:**

```bash
mvn clean test                                   # regression (default, excludes known-defects)
mvn clean test -Dsuite=testng-smoke.xml          # smoke only
mvn clean test -Dsuite=testng-full.xml           # everything, including known-defect tests
mvn clean test -Dbase.uri=https://my-qa-env.com  # point at another environment
mvn clean test -Dtestdata.missing.fields=missing-fields.xlsx   # negative data from Excel instead of JSON
```

Any key in `config.properties` can be overridden the same way with `-Dkey=value`.

## Reports and logs

- **Extent report:** `reports/ExtentReport_<yyyy-MM-dd_HH-mm-ss>.html` – a new file for every run, so earlier reports are kept. Each test shows the requests sent, status, response time and response body.
- **Log file:** `logs/test-run.log`.
- **CI:** GitHub Actions runs the regression suite on every push and uploads the report as an artifact. A `Jenkinsfile` with suite and environment parameters is included.

> The Restful Booker credentials in `config.properties` (`admin` / `password123`) are the public demo credentials from its documentation. Real credentials should come from CI secrets via `-D`.

## Author

**Lathika Sampathkumar** – SDET / QA Automation Engineer · [LinkedIn](https://linkedin.com/in/lathika-sampathkumar-24630822b) · [GitHub](https://github.com/lathikasampathkumar)
