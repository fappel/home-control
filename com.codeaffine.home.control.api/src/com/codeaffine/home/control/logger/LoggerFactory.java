package com.codeaffine.home.control.logger;

public interface LoggerFactory {

  /**
   * Return a logger named corresponding to the class passed as parameter
   *
   * @param clazz the returned logger will be named after clazz
   * @return logger
   */
  Logger getLogger( Class<?> clazz );
}
