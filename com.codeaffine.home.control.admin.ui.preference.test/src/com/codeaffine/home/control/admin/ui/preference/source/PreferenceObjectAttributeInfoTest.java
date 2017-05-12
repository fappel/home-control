package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.ADD;
import static com.codeaffine.home.control.admin.ui.test.ObjectInfoHelper.*;
import static com.codeaffine.home.control.admin.ui.test.PreferenceInfoHelper.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.descriptor.ActionPresentation;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeAction;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

public class PreferenceObjectAttributeInfoTest {

  private ModifyAdapter modifyAdapter;

  @Before
  public void setUp() {
    modifyAdapter = mock( ModifyAdapter.class );
  }

  @Test
  public void getName() {
    AttributeInfo attributeInfo = createAttributeInfoOfSimpleType( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

    String actual = attributeInfo.getName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_NAME );
  }

  @Test
  public void getDisplayName() {
    AttributeInfo attributeInfo = createAttributeInfoOfSimpleType( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

    String actual = attributeInfo.getDisplayName();

    assertThat( actual ).isEqualTo( ATTRIBUTE_NAME );
  }

  @Test
  public void getAttributeType() {
    AttributeInfo attributeInfo = createAttributeInfoOfSimpleType( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

    Class<?> actual = attributeInfo.getAttributeType();

    assertThat( actual ).isSameAs( ATTRIBUTE_VALUE.getClass() );
  }

  @Test
  public void getGenericTypeParametersOfAttributeType() {
    List<Class<?>> expected = Arrays.asList( String.class );
    AttributeInfo attributeInfo
      = createAttributeInfoOfCollectionType( ATTRIBUTE_NAME, asList( "element" ), expected );


    List<Class<?>> actual = attributeInfo.getGenericTypeParametersOfAttributeType();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getActions() {
    AttributeInfo attributeInfo
      = createAttributeInfoOfCollectionType( ATTRIBUTE_NAME, asList( "element" ), asList( String.class ) );

    List<AttributeAction> actual = attributeInfo.getActions();

    assertThat( actual )
      .hasSize( 1 )
      .allMatch( action -> action.getPresentation( ActionPresentation.class ) == ADD );
  }

  @Test
  public void getActionsOfNonCollectionType() {
    AttributeInfo attributeInfo = createAttributeInfoOfSimpleType( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );


    List<AttributeAction> actual = attributeInfo.getActions();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void invokeActionReturnedBygetActions() {
    List<String> value = asList( "list-element" );
    AttributeInfo attributeInfo
      = createAttributeInfoOfCollectionType( ATTRIBUTE_NAME, value, asList( String.class ) );

    attributeInfo.getActions().forEach( action -> action.run() );

    ArgumentCaptor<CollectionValue> captor = forClass( CollectionValue.class );
    verify( modifyAdapter ).handleAddition( captor.capture() );
    assertThat( captor.getValue().getValue() ).isEqualTo( value );
    assertThat( captor.getValue().getObjectInfo().getAttributeValue( ATTRIBUTE_NAME ) ).isEqualTo( value );
    assertThat( captor.getValue().getAttributeInfo() ).isEqualTo( attributeInfo );
  }

  private AttributeInfo createAttributeInfoOfSimpleType( String attributeName, Object attributeValue ) {
    PreferenceAttributeDescriptor descriptor = stubDescriptor( attributeName, attributeValue.getClass() );
    PreferenceInfo preferenceInfo = stubPreferenceInfo( attributeValue, descriptor );
    PreferenceObjectInfo objectInfo = new PreferenceObjectInfo( preferenceInfo, modifyAdapter );
    return objectInfo.getAttributeInfo( attributeName );
  }

  private AttributeInfo createAttributeInfoOfCollectionType(
    String attributeName, List<String> value, List<Class<?>> genericTypeParametersOfValue )
  {
    PreferenceAttributeDescriptor descriptor = stubDescriptor( attributeName, List.class );
    stubDescriptorWithGenericTypeParametersOfAttributeType( descriptor, genericTypeParametersOfValue );
    PreferenceInfo preferenceInfo = stubPreferenceInfo( value, descriptor );
    PreferenceObjectInfo objectInfo = new PreferenceObjectInfo( preferenceInfo, modifyAdapter );
    return objectInfo.getAttributeInfo( attributeName );
  }
}