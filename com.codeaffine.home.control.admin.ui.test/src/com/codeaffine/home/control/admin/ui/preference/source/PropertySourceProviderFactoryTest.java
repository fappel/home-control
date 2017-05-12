package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.home.control.admin.ui.preference.source.Messages.ERROR_UNSUPPORTED_COLLECTION_VALUE_TYPE;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySourceProvider;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class PropertySourceProviderFactoryTest {

  private PropertySourceProviderFactory factory;
  private ModifyAdapter modifyAdapter;

  static class PropertySourceData {

    final Class<? extends IPropertySource> expectedSourceType;
    final Object editableValue;
    final Object object;

    PropertySourceData( Object object, Class<? extends IPropertySource> type, Object editableValue ) {
      this.object = object;
      this.expectedSourceType = type;
      this.editableValue = editableValue;
    }
  }

  public static Object[] provideData() {
    HashSet<Object> set = new HashSet<>();
    ArrayList<Object> list = new ArrayList<>();
    HashMap<Object, Object> map = new HashMap<>();
    Object value = new Object();
    return new Object[] {
      new PropertySourceData( mock( IPropertySource.class ), IPropertySource.class, null ),
      new PropertySourceData( mock( PreferenceInfo.class ), AttributePropertySource.class, null ),
      new PropertySourceData( newCollectionValue( set ), AttributePropertySource.class, set ),
      new PropertySourceData( newCollectionValue( list ), AttributePropertySource.class, list ),
      new PropertySourceData( newCollectionValue( map ), AttributePropertySource.class, map ),
      new PropertySourceData( value, ValuePropertySource.class, value ),
      new PropertySourceData( null, ValuePropertySource.class, null ),
    };
  }

  @Before
  public void setUp() {
    factory = new PropertySourceProviderFactory();
  }

  @Test
  public void create() {
    modifyAdapter = mock( ModifyAdapter.class );

    IPropertySourceProvider actual = factory.create( modifyAdapter );

    assertThat( actual ).isNotNull();
  }

  @Test
  @Parameters( source = PropertySourceProviderFactoryTest.class )
  public void getPropertySourceFromCreatedProvider( PropertySourceData data ) {
    IPropertySourceProvider provider = factory.create( mock( ModifyAdapter.class ) );

    IPropertySource actual = provider.getPropertySource( data.object );

    assertThat( actual )
      .isInstanceOf( data.expectedSourceType )
      .matches( source ->    source.getEditableValue() == data.editableValue
                          || source.getEditableValue().equals( data.editableValue ) );
  }

  @Test
  public void getPropertySourceFromCreatedProviderForUnsupportedCollectionType() {
    IPropertySourceProvider provider = factory.create( mock( ModifyAdapter.class ) );
    Object value = mock( Queue.class );

    Throwable actual = thrownBy( () -> provider.getPropertySource( newCollectionValue( value ) ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasMessage( format( ERROR_UNSUPPORTED_COLLECTION_VALUE_TYPE, value.getClass().getName() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsModifyAdapterArgument() {
    factory.create( null );
  }

  private static CollectionValue newCollectionValue( Object collection ) {
    return new CollectionValue( mock( ObjectInfo.class ), mock( AttributeInfo.class ), collection );
  }
}