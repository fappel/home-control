package com.codeaffine.home.control.engine.util;

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceCollector {

  private static final Object[] EMPTY_ARRAY = new Object[ 0 ];

  public static <T> List<T> collectServices( Class<T> serviceType, BundleContext context ) {
    List<T> result = new LinkedList<T>();
    ServiceTracker<T, Object> tracker = createTracker( serviceType, context  );
    for( Object service : getTrackedServices( tracker ) ) {
      result.add( serviceType.cast( service ) );
    }
    return result;
  }

  private static <T> ServiceTracker<T, Object> createTracker( Class<T> type, BundleContext context ) {
    return new ServiceTracker<T, Object>( context, type, null );
  }

  private static <T> Object[] getTrackedServices( ServiceTracker<T, Object> tracker ) {
    try {
      tracker.open();
      return getServices( tracker );
    } finally {
      tracker.close();
    }
  }

  private static <T> Object[] getServices( ServiceTracker<T, Object> tracker ) {
    Object[] result = tracker.getServices();
    if( result == null ) {
      result = EMPTY_ARRAY;
    }
    return result;
  }
}