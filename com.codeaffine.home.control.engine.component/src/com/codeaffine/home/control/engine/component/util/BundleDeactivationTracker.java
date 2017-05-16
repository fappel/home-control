package com.codeaffine.home.control.engine.component.util;

import static com.codeaffine.home.control.engine.component.util.Messages.ERROR_BUNDLE_IS_NOT_ACTIVE;
import static com.codeaffine.util.ArgumentVerification.*;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.BundleEvent.STARTED;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BundleDeactivationTracker {

  private final Map<Long, Map<Class<?>, Set<Consumer>>> listeners;
  private final Function<Class<?>, Bundle> bundleSupplier;

  public BundleDeactivationTracker( BundleContext context ) {
    this( context, classOfBundle -> FrameworkUtil.getBundle( classOfBundle ) );
  }

  BundleDeactivationTracker( BundleContext context, Function<Class<?>, Bundle> bundleSupplier ) {
    verifyNotNull( bundleSupplier, "bundleSupplier" );
    verifyNotNull( context, "context" );

    this.bundleSupplier = bundleSupplier;
    this.listeners = new HashMap<>();
    context.addBundleListener( evt -> handleBundleEvent( evt ) );
  }

  public <T> void registerDeactivationHook( Class<T> classOfBundle, Consumer<Class<T>> deactivationHook ) {
    verifyNotNull( deactivationHook, "deactivationHook" );
    verifyNotNull( classOfBundle, "classOfBundle" );
    verifyBundleOfClassIsActive( classOfBundle, getBundle( classOfBundle ) );

    ensureIdToClassMapStructure( classOfBundle );
    Map<Class<?>, Set<Consumer>> map = listeners.get( getBundleId( getBundle( classOfBundle ) ) );
    ensureClassToHookSetStructure( classOfBundle, map );
    map.get( classOfBundle ).add( deactivationHook );
  }

  private void handleBundleEvent( BundleEvent evt ) {
    if( bundleIsNotActive( evt ) && hasDeactivationHookForBundle( evt ) ) {
      executeAndRemoveDeactivationHooks( evt );
    }
  }

  private static boolean bundleIsNotActive( BundleEvent evt ) {
    return evt.getType() != STARTED;
  }

  private boolean hasDeactivationHookForBundle( BundleEvent evt ) {
    return listeners.containsKey( getBundleId( evt.getBundle() ) );
  }

  private void executeAndRemoveDeactivationHooks( BundleEvent evt ) {
    Long key = getBundleId( evt.getBundle() );
    Map<Class<?>, Set<Consumer>> handlerMap = listeners.get( key );
    handlerMap.keySet().forEach( clazz -> handlerMap.get( clazz ).forEach( hook -> hook.accept( clazz ) ) );
    listeners.remove( key );
  }

  private <T> Bundle getBundle( Class<T> classOfBundle ) {
    return bundleSupplier.apply( classOfBundle );
  }

  private static <T> void verifyBundleOfClassIsActive( Class<T> classOfBundle, Bundle bundle ) {
    verifyCondition( bundle.getState() == ACTIVE, ERROR_BUNDLE_IS_NOT_ACTIVE, getArguments( bundle, classOfBundle ) );
  }

  private static <T> Object[] getArguments( Bundle bundle, Class<T> classOfBundle ) {
    return new Object[] {
      bundle.getSymbolicName(), bundle.getVersion(), getBundleId( bundle ), classOfBundle.getName()
    };
  }

  private <T> void ensureIdToClassMapStructure( Class<T> classOfBundle ) {
    if( !listeners.containsKey( getBundleId( getBundle( classOfBundle ) ) ) ) {
      Map<Class<?>, Set<Consumer>> map = new HashMap<>();
      listeners.put( getBundleId( getBundle( classOfBundle ) ), map );
    }
  }

  private static <T> void ensureClassToHookSetStructure( Class<T> classOfBundle, Map<Class<?>, Set<Consumer>> map ) {
    if( !map.containsKey( classOfBundle ) ) {
      map.put( classOfBundle, new HashSet<>() );
    }
  }

  private static Long getBundleId( Bundle bundle ) {
    return Long.valueOf( bundle.getBundleId() );
  }
}