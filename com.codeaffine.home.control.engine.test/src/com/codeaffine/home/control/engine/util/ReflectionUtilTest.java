package com.codeaffine.home.control.engine.util;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.junit.Test;

import com.codeaffine.home.control.event.Subscribe;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReflectionUtilTest {

  private static class MyCommand {
    @Subscribe
    private void execute() {}
  }

  @Test
  public void invoke() throws Exception {
    Object expected = new Object();
    Object argument = new Object();
    Object object = stubFunctionObject( expected, argument );
    Method method = getMethod( object, "apply", Object.class );

    Object actual = ReflectionUtil.invoke( method, object, argument );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void invokeRestrictedType() throws Exception {
    MyCommand object = new MyCommand();
    Method method = getMethod( object, "execute" );

    Object actual = ReflectionUtil.invoke( method, object );

    assertThat( actual ).isNull();
  }

  @Test
  public void invokeWithRuntimeException() throws Exception {
    RuntimeException expected = new RuntimeException();
    Object argument = new Object();
    Function object = stubFunctionObject( null, argument );
    when( object.apply( argument ) ).thenThrow( expected );
    Method method = getMethod( object, "apply", Object.class );

    Throwable actual = thrownBy( () -> ReflectionUtil.invoke( method, object, argument ) );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void invokeWithCheckedException() throws Exception {
    Exception expected = new Exception();
    Callable object = mock( Callable.class );
    when( object.call() ).thenThrow( expected );
    Method method = getMethod( object, "call" );

    Throwable actual = thrownBy( () -> ReflectionUtil.invoke( method, object ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasCause( expected );
  }

  @Test
  public void getAnnotatedMethod() {
    Collection<Method> actual = ReflectionUtil.getAnnotatedMethods( new MyCommand(), Subscribe.class );

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( method -> "execute".equals( method.getName() ) );
  }

  @Test
  public void execute() throws Exception {
    Object expected = new Object();
    Callable callable = stubCallableWithResult( expected );

    Object actual = ReflectionUtil.execute( callable );

    assertThat( actual ).isSameAs( expected );
  }

  @Test( expected = NullPointerException.class )
  public void executeIfReflectiveCallArgumentIsNull() {
    ReflectionUtil.execute( null );
  }

  @Test
  public void executeIfReflectiveCallThrowsRuntimeException() throws Exception {
    RuntimeException expected = new RuntimeException();
    Callable callable = stubCallableWithProblem( expected );

    Throwable actual = thrownBy( () -> ReflectionUtil.execute( callable ) );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void executeIfReflectiveCallThrowsCheckedException() throws Exception {
    Exception cause = new Exception();
    Callable callable = stubCallableWithProblem( cause );

    Throwable actual = thrownBy( () -> ReflectionUtil.execute( callable ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasCause( cause );
  }

  @Test
  public void executeIfReflectiveCallThrowsInvocationTargetExceptionWithRuntimeCause() throws Exception {
    RuntimeException expected = new RuntimeException();
    Callable callable = stubCallableWithProblem( new InvocationTargetException( expected ) );

    Throwable actual = thrownBy( () -> ReflectionUtil.execute( callable ) );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void executeIfReflectiveCallThrowsInvocationTargetExceptionWithCheckedCause() throws Exception {
    Exception cause = new Exception();
    Callable callable = stubCallableWithProblem( new InvocationTargetException( cause ) );

    Throwable actual = thrownBy( () -> ReflectionUtil.execute( callable ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasCause( cause );
  }

  private static Function stubFunctionObject( Object returnValue, Object argument ) {
    Function result = mock( Function.class );
    when( result.apply( argument ) ).thenReturn( returnValue );
    return result;
  }

  private static Method getMethod( Object object, String methodName, Class ... arguments ) throws Exception {
    return object.getClass().getDeclaredMethod( methodName, arguments );
  }

  private static Callable stubCallableWithResult( Object expected ) throws Exception {
    Callable result = mock( Callable.class );
    when( result.call() ).thenReturn( expected );
    return result;
  }

  private static Callable stubCallableWithProblem( Throwable problem ) throws Exception {
    Callable result = mock( Callable.class );
    when( result.call() ).thenThrow( problem );
    return result;
  }
}