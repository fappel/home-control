package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertySourceProvider;
import com.codeaffine.home.control.admin.ui.internal.property.PropertySheetEntry;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.source.PropertySourceProviderFactory;

public class RootEntryFactoryTest {

  @Test
  public void create() {
    Object bean = new Object();
    IPropertySource propertySource = mock( IPropertySource.class );
    ModifyAdapter modifyAdapter = mock( ModifyAdapter.class );
    IPropertySourceProvider propertySourceProvider = stubPropertySourceProvider( bean, propertySource );
    PropertySourceProviderFactory providerSourceProviderFactory
      = stubPropertySourceProviderFactory( modifyAdapter, propertySourceProvider );
    RootEntryFactory factory = new RootEntryFactory( providerSourceProviderFactory );

    PropertySheetEntry entry = factory.create( modifyAdapter );
    IPropertySource actual = entry.getPropertySource( bean );

    assertThat( actual ).isSameAs( propertySource );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsPropertySourceProviderFactoryArgument() {
    new RootEntryFactory( null );
  }

  @Test
  public void createWithNullAsModifyAdapterArgument() {
    RootEntryFactory factory = new RootEntryFactory( mock( PropertySourceProviderFactory.class ) );

    Throwable actual = thrownBy( () -> factory.create( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  private static IPropertySourceProvider stubPropertySourceProvider( Object bean, IPropertySource propertySource ) {
    IPropertySourceProvider result = mock( IPropertySourceProvider.class );
    when( result.getPropertySource( bean ) ).thenReturn( propertySource );
    return result;
  }

  private static PropertySourceProviderFactory stubPropertySourceProviderFactory(
    ModifyAdapter modifyAdapter, IPropertySourceProvider propertySourceProvider )
  {
    PropertySourceProviderFactory result = mock( PropertySourceProviderFactory.class );
    when( result.create( modifyAdapter ) ).thenReturn( propertySourceProvider );
    return result;
  }
}