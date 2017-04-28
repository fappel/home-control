package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.home.control.admin.ui.preference.attribute.ComboBoxPropertyDescriptorHelper.getSelectionValues;
import static com.codeaffine.home.control.admin.ui.preference.attribute.EnumAdapterTest.TestEnum.TWO;
import static com.codeaffine.home.control.admin.ui.test.AttributeDescriptorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

public class EnumAdapterTest {

  private EnumAdapter adapter;

  enum TestEnum { ONE, TWO };

  @Before
  public void setUp() {
    adapter = new EnumAdapter( stubAttributeDescriptor( TestEnum.class ) );
  }


  @Test
  public void createPropertyDescriptor() {
    PropertyDescriptor actual = adapter.createPropertyDescriptor();

    assertThat( actual.getDisplayName() ).isEqualTo( ATTRIBUTE_NAME );
    assertThat( actual.getId() ).isEqualTo( ATTRIBUTE_NAME );
    assertThat( getSelectionValues( actual ) ).containsExactly( getExpectedSelectionValues() );
  }

  @Test
  public void convertToLabel() {
    Object actual = adapter.convertToLabel( TWO );

    assertThat( actual ).isEqualTo( TWO.ordinal() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToLabelWithNullAsAttributeValueArgument() {
    adapter.convertToLabel( null );
  }

  @Test
  public void convertToAttributeValue() {
    Object actual = adapter.convertToAttributeValue( TWO.ordinal() );

    assertThat( actual ).isEqualTo( TWO );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeWithNullAsAttributeValueLabelArgument() {
    adapter.convertToAttributeValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new EnumAdapter( null );
  }

  private static String[] getExpectedSelectionValues() {
    return Stream.of( TestEnum.values() ).map( value -> value.toString() ).toArray( String[]::new );
  }
}