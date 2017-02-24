package com.codeaffine.home.control.internal.logger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.logger.Logger;

public class LoggerFactoryAdapterTest {

  private LoggerFactoryAdapter factory;

  @Before
  public void setUp() {
    factory = new LoggerFactoryAdapter();
  }

  @Test
  public void getLogger() {
    Logger actual = factory.getLogger( Runnable.class );

    assertThat( actual ).isNotNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void getLoggerWithNullAsArgument() {
    factory.getLogger( null );
  }
}
