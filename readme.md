# Spring Boot Rest Project

[![Build Status](https://app.travis-ci.com/AlexanderBanar/job4j_chat.svg?branch=master)](https://app.travis-ci.com/AlexanderBanar/job4j_chat)
[![codecov](https://codecov.io/gh/AlexanderBanar/job4j_chat/branch/master/graph/badge.svg?token=IHJ9SI2EKB)](https://codecov.io/gh/AlexanderBanar/job4j_chat)

Technologies:

- Java JWT
- PostgreSQL
- Spring Boot (REST)
- Spring Data
- Spring Security

Annotations:

- @Bean
- @ControllerAdvice
- @DeleteMapping
- @EnableWebSecurity
- @Entity
- @ExceptionHandler
- @GeneratedValue
- @GetMapping
- @Id
- @JoinColumn
- @JsonBackReference
- @JsonManagedReference
- @ManyToOne
- @NotEmpty
- @OneToMany
- @OneToOne
- @PatchMapping
- @PathVariable
- @Positive
- @PostMapping
- @PutMapping
- @RequestBody
- @RequestMapping
- @RequestParam
- @ResponseStatus
- @RestController
- @Service
- @SpringBootApplication
- @Table
- @Valid
- @Validated

Description:

Models <br />
Three models: Person, Room and Message. Person can create a Room to enter there (with other Persons) the Messages. 
All CRUD operations in the models (and controllers) are provided.

Security <br />
UserDetailsServiceImpl (that extends UserDetailsService) will load to SecurityHolder the details (UserDetails) of 
the authenticated user. JWTAuthenticationFilter (that extends UsernamePasswordAuthenticationFilter) will "catch" 
the user: the attempt method checks the login and password, the success method generates the token. Meanwhile, 
the JWTAuthorizationFilter (that extends BasicAuthenticationFilter) will check the presence of token in headers of 
request and in case of its absence will send 403 status. The configuration class WebSecurity (that extends 
WebSecurityConfigurerAdapter) in its methods configures the permissions (mappings), adds filters, password encoder 
and CORS strategy.

Controllers <br />
All controllers have validation (null checking) of incoming parameters. The NPEs (that are thrown if the check fails) 
are caught by the GlobalExceptionHandler class that changes in runtime the response status and body.

Services <br />
The Service layer is omitted in the project for simplicity. The Controllers talk directly to the Repos.

Validation <br />
Spring Boot uses Hibernate Validator by default. All models have validation annotations that force throwing 
MethodArgumentNotValidException to the application context if the validation (of the very models) fails. Therefore, 
we have ValidationControllerAdvice that catches such exception and modifies the response. Also, we have validation of 
separate path variables (not complete models) that in case of fail throw ConstraintViolationException. This exception 
is caught at class level (of each controller) with response status modification to HTTP 400 (as by default there will 
be HTTP 500) as well as body modification.

