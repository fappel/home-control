package com.codeaffine.home.control.engine.component.preference;

import static com.codeaffine.home.control.engine.component.preference.Messages.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.BeanInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

public class PreferenceTypeValidatorTest {

  private PreferenceTypeValidator validator;

  @Preference
  interface ValidPreference {
    @DefaultValue( "value" )
    String getStringValue();
    void setStringValue( String value );
    @DefaultValue( "12" )
    int getIntValue();
    void setIntValue( int value );
  }

  @Preference
  interface PreferenceWithMissingReadAccessor {
    void setValue( String value );
  }

  @Preference
  interface PreferenceWithMissingWriteAccessor {
    @DefaultValue( "value" )
    String getValue();
  }

  @Preference
  interface PreferenceWithMissingDefaultValueDefinition {
    void setValue( String value );
    String getValue();
  }

  interface PreferenceWithMissingPreferenceAnnotation {
    @DefaultValue( "value" )
    String getValue();
    void setValue( String value );
  }

  @Preference
  interface PreferenceWithNonAttributeAccessorMethod {
    @DefaultValue( "value" )
    String getValue();
    void setValue( String value );
    void nonAttributeAccessorMethod();
  }

  @Preference
  interface PreferenceWithNonMatchingAttributeTypesOfAccessorMethods {
    @DefaultValue( "value" )
    String getValue();
    void setValue( int value );
  }

  @Preference
  interface PreferenceWithUnsupportedAttributeType {
    @DefaultValue( "value" )
    Object getValue();
    void setValue( Object value );
  }

  @Preference
  interface PreferenceWithSetAttribute {
    @DefaultValue( "{value}" )
    Set<String> getValue();
    void setValue( Set<String> value );
  }

  @Preference
  interface PreferenceWithSetAttributeThatHasUnsupportedGenericParameter {
    @DefaultValue( "{value}" )
    Set<Object> getValue();
    void setValue( Set<Object> value );
  }

  @Preference
  interface PreferenceWithListAttribute {
    @DefaultValue( "{value}" )
    List<String> getValue();
    void setValue( List<String> value );
  }

  @Preference
  interface PreferenceWithListAttributeThatHasUnsupportedGenericParameter {
    @DefaultValue( "{value}" )
    List<Object> getValue();
    void setValue( List<Object> value );
  }

  @Preference
  interface PreferenceWithMapAttribute {
    @DefaultValue( "{key=value}" )
    Map<String, String> getValue();
    void setValue( Map<String, String> value );
  }

  @Preference
  interface PreferenceWithMapAttributeThatHasUnsupportedGenericParameter {
    @DefaultValue( "{key=value}" )
    Map<String, Object> getValue();
    void setValue( Map<String, Object> value );
  }

  @Preference
  interface PreferenceWithAttributeTypeWithNonStaticValueOfMethod {
    @DefaultValue( "value" )
    TypeWithNonStaticValueOfMethod getValue();
    void setValue( TypeWithNonStaticValueOfMethod value );
  }

  static class TypeWithNonStaticValueOfMethod {
    TypeWithNonStaticValueOfMethod valueOf( @SuppressWarnings("unused") String value ) {
      return null;
    }
  }

  @Before
  public void setUp() {
    validator = new PreferenceTypeValidator();
  }

  @Test
  public void validate() {
    BeanInfo actual = validator.validate( ValidPreference.class );

    assertThat( actual.getBeanDescriptor().getBeanClass() ).isSameAs( ValidPreference.class );
  }

  @Test
  public void validateWithMapAttribute() {
    BeanInfo actual = validator.validate( PreferenceWithMapAttribute.class );

    assertThat( actual.getBeanDescriptor().getBeanClass() ).isSameAs( PreferenceWithMapAttribute.class );
  }

  @Test
  public void validateWithSetAttribute() {
    BeanInfo actual = validator.validate( PreferenceWithSetAttribute.class );

    assertThat( actual.getBeanDescriptor().getBeanClass() ).isSameAs( PreferenceWithSetAttribute.class );
  }

  @Test
  public void validateWithListAttribute() {
    BeanInfo actual = validator.validate( PreferenceWithListAttribute.class );

    assertThat( actual.getBeanDescriptor().getBeanClass() ).isSameAs( PreferenceWithListAttribute.class );
  }

  @Test
  public void validateIfReadAccessorIsMissing() {
    Class<PreferenceWithMissingReadAccessor> type = PreferenceWithMissingReadAccessor.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_MISSING_READ_ACCESSOR, type.getName(), "value" ) );
  }

  @Test
  public void validateIfWriteAccessorIsMissing() {
    Class<PreferenceWithMissingWriteAccessor> type = PreferenceWithMissingWriteAccessor.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_MISSING_WRITE_ACCESSOR, type.getName(), "value" ) );
  }

  @Test
  public void validateIfDefaultValueDefinitionIsMissing() {
    Class<PreferenceWithMissingDefaultValueDefinition> type = PreferenceWithMissingDefaultValueDefinition.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_MISSING_DEFAULT_VALUE_DEFINITION, type.getName(), "value" ) );
  }

  @Test
  public void validateIfPreferenceAnnotationIsMissing() {
    Class<PreferenceWithMissingPreferenceAnnotation> type = PreferenceWithMissingPreferenceAnnotation.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_NOT_A_PREFERENCE, type.getName() ) );
  }

  @Test
  public void validateIfPreferenceHasNonAttributeAccessorMethod() {
    Class<PreferenceWithNonAttributeAccessorMethod> type = PreferenceWithNonAttributeAccessorMethod.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_INVALID_BEAN_PROPERTY_ACCESSOR, "nonAttributeAccessorMethod", type.getName() ) );
  }

  @Test
  public void validateIfPreferenceHasNonMatchingAttributeTypesOfAccessorMethods() {
    Class<PreferenceWithNonMatchingAttributeTypesOfAccessorMethods> type
      = PreferenceWithNonMatchingAttributeTypesOfAccessorMethods.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_MISSING_WRITE_ACCESSOR, type.getName(), "value" ) );
  }

  @Test
  public void validateWithUnsupportedAttributeType() {
    Class<PreferenceWithUnsupportedAttributeType> type = PreferenceWithUnsupportedAttributeType.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_ATTRIBUTE_TYPE, type.getName(), "value", Object.class.getName() ) );
  }

  @Test
  public void validateWithAttributeTypeWithNonStaticValueOfMethod() {
    Class<PreferenceWithAttributeTypeWithNonStaticValueOfMethod> type
      = PreferenceWithAttributeTypeWithNonStaticValueOfMethod.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    Class<TypeWithNonStaticValueOfMethod> attributeType = TypeWithNonStaticValueOfMethod.class;
    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_ATTRIBUTE_TYPE, type.getName(), "value", attributeType.getName() ) );
  }

  @Test
  public void validateWithMapAttributeThatHasUnsupportedGenericParameter() {
    Class<PreferenceWithMapAttributeThatHasUnsupportedGenericParameter> type
      = PreferenceWithMapAttributeThatHasUnsupportedGenericParameter.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_ATTRIBUTE_TYPE, type.getName(), "value", Map.class.getName() ) );
  }

  @Test
  public void validateWithSetAttributeThatHasUnsupportedGenericParameter() {
    Class<PreferenceWithSetAttributeThatHasUnsupportedGenericParameter> type
      = PreferenceWithSetAttributeThatHasUnsupportedGenericParameter.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_ATTRIBUTE_TYPE, type.getName(), "value", Set.class.getName() ) );
  }

  @Test
  public void validateWithListAttributeThatHasUnsupportedGenericParameter() {
    Class<PreferenceWithListAttributeThatHasUnsupportedGenericParameter> type
      = PreferenceWithListAttributeThatHasUnsupportedGenericParameter.class;

    Throwable actual = thrownBy( () -> validator.validate( type ) );

    assertThat( actual )
      .isInstanceOf( IllegalArgumentException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_ATTRIBUTE_TYPE, type.getName(), "value", List.class.getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void validateWithNullAsPreferenceTypeArgument() {
    validator.validate( null );
  }
}