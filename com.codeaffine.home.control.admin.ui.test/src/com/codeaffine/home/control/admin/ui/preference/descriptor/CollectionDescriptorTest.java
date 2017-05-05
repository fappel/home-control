package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.admin.ui.preference.descriptor.AttributePropertyDescriptorHelper.hasExactlyDelegateInstanceOf;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class CollectionDescriptorTest {

  private CollectionDescriptor descriptor;
  private AttributeInfo attributeInfo;
  private ObjectInfo objectInfo;

  @Before
  public void setUp() {
    objectInfo = mock( ObjectInfo.class );
    attributeInfo = stubAttributeInfo( Set.class );
    descriptor = new CollectionDescriptor( objectInfo, attributeInfo );
  }

  @Test
  public void createPropertyDescriptor() {
    IPropertyDescriptor actual = descriptor.createPropertyDescriptor();

    assertThat( actual )
      .matches( descriptor -> hasExactlyDelegateInstanceOf( descriptor, CollectionPropertyDescriptor.class ) )
      .matches( descriptor -> descriptor.getDisplayName().equals( ATTRIBUTE_NAME ) )
      .matches( descriptor -> descriptor.getId().equals( ATTRIBUTE_NAME ) );
  }

  @Test
  public void convertToRepresentationValue() {
    HashSet<Object> value = new HashSet<>();

    CollectionValue actual = ( CollectionValue )descriptor.convertToRepresentationValue( value );

    assertThat( actual.getObjectInfo() ).isEqualTo( objectInfo );
    assertThat( actual.getAttributeInfo() ).isEqualTo( attributeInfo );
    assertThat( actual.getValue() ).isSameAs( value );
  }

  @Test
  public void convertToValue() {
    HashSet<Object> expected = new HashSet<>();

    Object actual = descriptor.convertToValue( expected );

    assertThat( actual ).isSameAs( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsOjbectInfoArgument() {
    new CollectionDescriptor( null, attributeInfo );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeInfoArgument() {
    new CollectionDescriptor( objectInfo, null );
  }
}