package com.codeaffine.home.control.util.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PrimitiveToBoxedTypeTest {

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfIntType() {
    Class<?> intType = int[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( intType );

    assertThat( actual ).isSameAs( Integer.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfLongType() {
    Class<?> longType = long[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( longType );

    assertThat( actual ).isSameAs( Long.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfDoubleType() {
    Class<?> doubleType = double[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( doubleType );

    assertThat( actual ).isSameAs( Double.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfFloatType() {
    Class<?> floatType = float[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( floatType );

    assertThat( actual ).isSameAs( Float.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfBooleanType() {
    Class<?> booleanType = boolean[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( booleanType );

    assertThat( actual ).isSameAs( Boolean.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfByteType() {
    Class<?> byteType = byte[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( byteType );

    assertThat( actual ).isSameAs( Byte.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeIfShortType() {
    Class<?> shortType = short[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( shortType );

    assertThat( actual ).isSameAs( Short.class );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeWithUnsupportedPrimitive() {
    Class<?> charType = char[].class.getComponentType();

    Class<?> actual = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( charType );

    assertThat( actual ).isSameAs( charType );
  }

  @Test
  public void replacePrimitiveTypeByBoxedTypeWithNonPrimitiveArgument() {
    Class<?> nonPrimitve = PrimitiveToBoxedType.replacePrimitiveTypeByBoxedType( String.class );

    assertThat( nonPrimitve ).isSameAs( String.class );
  }
}