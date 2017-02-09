package com.codeaffine.home.control.internal.activation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.codeaffine.home.control.SystemConfiguration;

public class SystemConfigurationTrackerTest {

  private SystemConfigurationTracker tracker;
  private SystemLifeCycle lifeCycle;
  private BundleContext context;

  @Before
  public void setUp() {
    context = mock( BundleContext.class );
    lifeCycle = mock( SystemLifeCycle.class );
    tracker = new SystemConfigurationTracker( context, lifeCycle );
  }

  @Test
  public void addingService() {
    SystemConfiguration expected = mock( SystemConfiguration.class );
    ServiceReference<SystemConfiguration> reference = stubContextWithConfigurationService( expected );

    SystemConfiguration actual = tracker.addingService( reference );

    verify( lifeCycle ).start( expected );
    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void removedService() {
    SystemConfiguration configuration = mock( SystemConfiguration.class );
    ServiceReference<SystemConfiguration> reference = stubContextWithConfigurationService( configuration );

    tracker.removedService( reference, configuration );

    verify( lifeCycle ).stop( configuration );
    verify( context ).ungetService( reference );
  }

  @SuppressWarnings("unchecked")
  private ServiceReference<SystemConfiguration> stubContextWithConfigurationService( SystemConfiguration service ) {
    ServiceReference<SystemConfiguration> reference = mock( ServiceReference.class );
    when( context.getService( reference ) ).thenReturn( service );
    return reference;
  }
}