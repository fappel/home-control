package com.codeaffine.home.control.engine.component.util;

import static com.codeaffine.home.control.engine.component.util.Messages.ERROR_BUNDLE_IS_NOT_ACTIVE;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.BundleEvent.*;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Version;

@SuppressWarnings( "unchecked" )
public class BundleDeactivationTrackerTest {

  private static final long BUNDLE_ID_1 = 4711L;
  private static final long BUNDLE_ID_2 = 007L;

  private Function<Class<?>, Bundle> bundleSupplier;
  private BundleDeactivationTracker tracker;

  private BundleListener bundleListener;

  static class ClassOfBundle1 {}
  static class ClassOfBundle2 {}

  @Before
  public void setUp() {
    BundleContext context = mock( BundleContext.class );
    bundleSupplier = mock( Function.class );
    tracker = new BundleDeactivationTracker( context, bundleSupplier );
    bundleListener = captureBundleListener( context );
  }

  @Test
  public void unloadBundleWithRegisteredHook() {
    Bundle bundle = stubBundle( BUNDLE_ID_1, ACTIVE );
    stubBundleSupplier( ClassOfBundle1.class, bundle );
    AtomicReference<Class<?>> expected = new AtomicReference<>();

    tracker.registerUnloadHook( ClassOfBundle1.class, () -> expected.set( ClassOfBundle1.class ) );
    bundleListener.bundleChanged( new BundleEvent( STOPPING, bundle ) );

    assertThat( expected.get() ).isSameAs( ClassOfBundle1.class );
  }

  @Test
  public void registerMultipleUnloadsHooksForTheSameClassOfABundle() {
    Bundle bundle = stubBundle( BUNDLE_ID_1, ACTIVE );
    stubBundleSupplier( ClassOfBundle1.class, bundle );
    Runnable hook1 = mock( Runnable.class );
    Runnable hook2 = mock( Runnable.class );

    tracker.registerUnloadHook( ClassOfBundle1.class, hook1 );
    tracker.registerUnloadHook( ClassOfBundle1.class, hook2 );
    bundleListener.bundleChanged( new BundleEvent( STOPPING, bundle ) );

    verify( hook1 ).run();
    verify( hook2 ).run();
  }

  @Test
  public void registerMultipleUnloadHooksForDifferenctClassesOfABundle() {
    Bundle bundle = stubBundle( BUNDLE_ID_1, ACTIVE );
    stubBundleSupplier( ClassOfBundle1.class, bundle );
    Runnable hook1 = mock( Runnable.class );
    tracker.registerUnloadHook( ClassOfBundle1.class, hook1 );
    stubBundleSupplier( ClassOfBundle2.class, bundle );
    Runnable hook2 = mock( Runnable.class );
    tracker.registerUnloadHook( ClassOfBundle2.class, hook2 );

    bundleListener.bundleChanged( new BundleEvent( STOPPING, bundle ) );

    verify( hook1 ).run();
    verify( hook2 ).run();
  }

  @Test
  public void registerUnloadHooksForClassesOfDifferentBundles() {
    Bundle bundle1 = stubBundle( BUNDLE_ID_1, ACTIVE );
    stubBundleSupplier( ClassOfBundle1.class, bundle1 );
    Runnable hook1 = mock( Runnable.class );
    tracker.registerUnloadHook( ClassOfBundle1.class, hook1 );
    Bundle bundle2 = stubBundle( BUNDLE_ID_2, ACTIVE );
    stubBundleSupplier( ClassOfBundle2.class, bundle2 );
    Runnable hook2 = mock( Runnable.class );
    tracker.registerUnloadHook( ClassOfBundle2.class, hook2 );

    bundleListener.bundleChanged( new BundleEvent( STOPPING, bundle1 ) );

    verify( hook1 ).run();
    verify( hook2, never() ).run();
  }

  @Test
  public void changeBundleStateAgainAfterDeactivation() {
    Bundle bundle = stubBundle( BUNDLE_ID_1, ACTIVE );
    stubBundleSupplier( ClassOfBundle1.class, bundle );
    AtomicReference<Class<?>> expected = new AtomicReference<>();

    tracker.registerUnloadHook( ClassOfBundle1.class, () -> expected.set( ClassOfBundle1.class ) );
    bundleListener.bundleChanged( new BundleEvent( STOPPING, bundle ) );
    expected.set( null );
    bundleListener.bundleChanged( new BundleEvent( STOPPED, bundle ) );
    bundleListener.bundleChanged( new BundleEvent( STARTING, bundle ) );
    bundleListener.bundleChanged( new BundleEvent( STARTED, bundle ) );
    bundleListener.bundleChanged( new BundleEvent( STOPPING, bundle ) );

    assertThat( expected.get() ).isNull();
  }


  @Test
  public void deactivateBundleWithRegisteredHookOfInactiveBundle() {
    Bundle bundle = stubBundle( BUNDLE_ID_1, INSTALLED );
    stubBundleSupplier( ClassOfBundle1.class, bundle );
    Runnable hook = mock( Runnable.class );

    Throwable actual = thrownBy( () -> tracker.registerUnloadHook( ClassOfBundle1.class, hook ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_BUNDLE_IS_NOT_ACTIVE,
                           bundle.getSymbolicName(),
                           bundle.getVersion(),
                           bundle.getBundleId(),
                           ClassOfBundle1.class.getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsContextArgument() {
    new BundleDeactivationTracker( null, mock( Function.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsBundleSupplierArgument() {
    new BundleDeactivationTracker( mock( BundleContext.class ), null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerUnloadHookWithNullAsTypeArgument() {
    tracker.registerUnloadHook( null, mock( Runnable.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerUnloadHookWithNullAsUnloadHookArgument() {
    tracker.registerUnloadHook( ClassOfBundle1.class, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterUnloadHookWithNullAsTypeArgument() {
    tracker.unregisterUnloadHook( null, mock( Runnable.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterUnloadHookWithNullAsUnloadHookArgument() {
    tracker.unregisterUnloadHook( ClassOfBundle1.class, null );
  }

  private static Bundle stubBundle( long bundleId, int state ) {
    Bundle result = mock( Bundle.class );
    when( result.getBundleId() ).thenReturn( bundleId );
    when( result.getSymbolicName() ).thenReturn( "symbolic-name-of-" + bundleId );
    when( result.getVersion() ).thenReturn( new Version( 0, 8, 15, "of-" + bundleId ) );
    when( result.getState() ).thenReturn( state );
    return result;
  }

  private void stubBundleSupplier( Class<?> classOfBundle, Bundle bundle ) {
    when( bundleSupplier.apply( classOfBundle ) ).thenReturn( bundle );
  }

  private static BundleListener captureBundleListener( BundleContext context ) {
    ArgumentCaptor<BundleListener> captor = forClass( BundleListener.class );
    verify( context ).addBundleListener( captor.capture() );
    return captor.getValue();
  }
}