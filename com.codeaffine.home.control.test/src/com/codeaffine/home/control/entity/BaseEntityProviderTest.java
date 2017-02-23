package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.entity.MyEntityProvider.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.internal.entity.ZoneProviderImpl;

public class BaseEntityProviderTest {

  private MyEntityProvider provider;

  @Before
  public void setUp() {
    provider = new MyEntityProvider( mock( ZoneProviderImpl.class ) );
  }

  @Test
  public void findAll() {
    Collection<MyEntity> actual = provider.findAll();

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test
  public void findByDefinition() {
    MyEntity actual = provider.findByDefinition( PARENT );

    assertThat( actual.getDefinition() ).isSameAs( PARENT );
  }

  @Test
  public void dispose() {
    provider.dispose();

    assertThat( provider.findAll() ).isEmpty();
    assertThat( provider.findByDefinition( PARENT ) ).isNull();
  }
}