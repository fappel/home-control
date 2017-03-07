package com.codeaffine.home.control.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.type.StringType;
import com.codeaffine.test.util.lang.EqualsTester;

public class StringTypeTest {

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsArgument() {
    new StringType( null );
  }

  @Test
  public void toStringCall() {
    String expected = "expected";

    StringType instance = new StringType( expected );
    String actual = instance.toString();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<StringType> tester = EqualsTester.newInstance( StringType.EMPTY );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new StringType( "x" ), new StringType( new String( "x" ) ) );
    tester.assertNotEqual( new StringType( "x" ), new StringType( "y" ) );
  }
}
