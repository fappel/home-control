package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.entity.MyEntityProvider.MY_ENTITY_DEFINITIONS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class BaseEntityProviderTest {

  private MyEntityProvider provider;

  @Before
  public void setUp() {
    provider = new MyEntityProvider();
  }

  @Test
  public void findAll() {
    Collection<MyEntity> actual = provider.findAll();

    assertThat( actual ).hasSize( MY_ENTITY_DEFINITIONS.size() );
  }

  @Test
  public void findByDefinition() {
    MyEntity actual = provider.findByDefinition( MY_ENTITY_DEFINITIONS.iterator().next() );

    assertThat( actual.getDefinition() ).isSameAs( MY_ENTITY_DEFINITIONS.iterator().next() );
  }

  @Test
  public void dispose() {
    provider.dispose();

    assertThat( provider.findAll() ).isEmpty();
    assertThat( provider.findByDefinition( MY_ENTITY_DEFINITIONS.iterator().next() ) ).isNull();
  }
}