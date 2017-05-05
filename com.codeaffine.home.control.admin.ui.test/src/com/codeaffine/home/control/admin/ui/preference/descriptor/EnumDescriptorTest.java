package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.ComboBoxPropertyDescriptorHelper.getSelectionValues;
import static com.codeaffine.home.control.admin.ui.preference.descriptor.EnumDescriptorTest.TestEnum.TWO;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;

public class EnumDescriptorTest {

  private EnumDescriptor descriptor;

  enum TestEnum { ONE, TWO };

  @Before
  public void setUp() {
    descriptor = new EnumDescriptor( stubAttributeInfo( TestEnum.class ) );
  }


  @Test
  public void createPropertyDescriptor() {
    IPropertyDescriptor actual = descriptor.createPropertyDescriptor();

    assertThat( actual.getDisplayName() ).isEqualTo( ATTRIBUTE_NAME );
    assertThat( actual.getId() ).isEqualTo( ATTRIBUTE_NAME );
    assertThat( getSelectionValues( actual ) ).containsExactly( getExpectedSelectionValues() );
  }

  @Test
  public void convertToRepresentationValue() {
    Object actual = descriptor.convertToRepresentationValue( TWO );

    assertThat( actual ).isEqualTo( TWO.ordinal() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToRepresentationValueWithNullAsAttributeValueArgument() {
    descriptor.convertToRepresentationValue( null );
  }

  @Test
  public void convertToValue() {
    Object actual = descriptor.convertToValue( TWO.ordinal() );

    assertThat( actual ).isEqualTo( TWO );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToAttributeWithNullAsAttributeValueLabelArgument() {
    descriptor.convertToValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeDescriptorArgument() {
    new EnumDescriptor( null );
  }

  private static String[] getExpectedSelectionValues() {
    return Stream.of( TestEnum.values() ).map( value -> value.toString() ).toArray( String[]::new );
  }
}