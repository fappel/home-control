package com.codeaffine.home.control.admin.ui.internal.console;

import static com.codeaffine.home.control.util.reflection.ReflectionUtil.*;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codeaffine.home.control.util.reflection.ReflectionUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
class SessionFactory {

  static class Session {

    private final Object delegate;

    Session( Object delegate ) {
      this.delegate = delegate;
    }

    void put( String name, Object value ) {
      Method method = ReflectionUtil.execute( () -> delegate.getClass().getMethod( "put", String.class, Object.class ) );
      invoke( method, delegate, name, value );
    }

    void execute( CharSequence commandLine ) {
      Method method = ReflectionUtil.execute( () -> delegate.getClass().getMethod( "execute", CharSequence.class ) );
      invoke( method, delegate, commandLine );
    }

    void close() {
      Method method = ReflectionUtil.execute( () -> delegate.getClass().getMethod( "close" ) );
      invoke( method, delegate );
    }
  }

  Session create( InputStream in, PrintStream out, PrintStream err ) {
    BundleContext context = FrameworkUtil.getBundle( getClass() ).getBundleContext();
    ServiceReference serviceReference = context.getServiceReference( "org.apache.karaf.shell.api.console.SessionFactory" );
    if( serviceReference == null ) {
      serviceReference = context.getServiceReference( "org.apache.felix.service.command.CommandProcessor" );
    }
    if( serviceReference == null ) {
      throw new IllegalStateException( "No osgi console session implementation found." );
    }
    Object sessionFactory  = context.getService( serviceReference );

    Method method;
    try {
      method = execute( () -> getSessionFactoryMethod( sessionFactory, "create" ) );
    } catch( RuntimeException rt ) {
      method = execute( () -> getSessionFactoryMethod( sessionFactory, "createSession" ) );
    }
    return new Session( invoke( method, sessionFactory, in, out, err ) );
  }

  private static Method getSessionFactoryMethod( Object sessionFactory, String name ) throws NoSuchMethodException {
    return sessionFactory.getClass().getMethod( name, InputStream.class, PrintStream.class, PrintStream.class );
  }
}