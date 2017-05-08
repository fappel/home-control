package com.codeaffine.home.control.admin.ui.preference.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;

public class CollectionValueTest {

  @Test
  public void construct() {
    ObjectInfo objectInfo = mock( ObjectInfo.class );
    AttributeInfo attributeInfo = mock( AttributeInfo.class );
    List<?> value = mock( List.class );

    CollectionValue collectionValue = new CollectionValue( objectInfo, attributeInfo, value  );

    assertThat( collectionValue.getObjectInfo() ).isSameAs( objectInfo );
    assertThat( collectionValue.getAttributeInfo() ).isSameAs( attributeInfo );
    assertThat( collectionValue.getValue() ).isSameAs( value );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsObjectInfoArgument() {
    new CollectionValue( null, mock( AttributeInfo.class ), mock( Set.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeInfoArgument() {
    new CollectionValue( mock( ObjectInfo.class ), null, mock( Set.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsValueArgument() {
    new CollectionValue( mock( ObjectInfo.class ), mock( AttributeInfo.class ), null );
  }
}