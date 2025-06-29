package dev.corusoft.eticketia.application;

import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Generic interface to define an application use case.
 * It defines the type for the arguments and the results.
 *
 * @param <INPUT>   Type of the input argument to execute the use case.
 *                  Use {@link Void} if no arguments are required.
 * @param <OUTPUT>> Type of the result of executing the use case.
 *                  Use {@link Void} if no response is generated.
 */
public interface UseCase<INPUT, OUTPUT> {

  /**
   * Get the name of the {@link UseCase}
   */
  String getUseCaseName();

  /**
   * Execute the use case.
   *
   * @param input Arguments to execute the use case.
   * @return The result of executing the use case.
   * @throws DomainException if an error occurs during execution.
   */
  OUTPUT execute(INPUT input) throws DomainException;

  /**
   * Creates the use case response.
   *
   * @param result Object to generate the response
   */
  OUTPUT buildOutput(Object result);
}
