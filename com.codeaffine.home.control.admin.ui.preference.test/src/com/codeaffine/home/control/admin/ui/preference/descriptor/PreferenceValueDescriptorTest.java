package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.ComboBoxPropertyDescriptorHelper.getSelectionValues;
import static com.codeaffine.home.control.admin.ui.preference.descriptor.TestPreferenceValue.TWO;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

public class PreferenceValueDescriptorTest {

  private PreferenceValueDescriptor descriptor;

  @Before
  public void setUp() {
    descriptor = new PreferenceValueDescriptor( stubAttributeInfo( TestPreferenceValue.class ) );
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

    assertThat( actual ).isEqualTo( indexOf( TWO ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToRepresentationValueWithNullAsAttributeValueArgument() {
    descriptor.convertToRepresentationValue( null );
  }

  @Test
  public void convertToValue() {
    Object actual = descriptor.convertToValue( indexOf( TWO ) );

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

  @Test
  public void createPropertyDescriptorIfTypeDoesNotProvideValuesAccessMethod() {
    PreferenceValueDescriptor invalidDescriptor = new PreferenceValueDescriptor( stubAttributeInfo( Runnable.class ) );

    Throwable actual = thrownBy( () -> invalidDescriptor.createPropertyDescriptor() );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessageContaining( Runnable.class.getName() + "." + PreferenceValueDescriptor.VALUES_ACCESS_METHOD );
  }

  private static String[] getExpectedSelectionValues() {
    return Stream.of( TestPreferenceValue.values() ).map( value -> value.toString() ).toArray( String[]::new );
  }

  private static int indexOf( TestPreferenceValue value ) {
    return asList( TestPreferenceValue.values() ).indexOf( value );
  }
}