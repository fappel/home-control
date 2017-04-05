package com.codeaffine.home.control.status.internal.section;

import static com.codeaffine.home.control.status.internal.section.EntityRelationHelper.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.test.util.entity.MyEntityProvider.CHILD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.status.internal.section.SectionImpl;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.test.util.entity.MyEntity;
import com.codeaffine.home.control.test.util.entity.MyEntityDefinition;

public class SectionImplTest {

  private EntityRelationProvider entityRelationProvider;
  private SectionImpl section;

  @Before
  public void setUp() {
    entityRelationProvider = mock( EntityRelationProvider.class );
    section = new SectionImpl( BED, entityRelationProvider );
  }

  @Test
  public void getDefinition() {
    SectionDefinition actual = section.getDefinition();

    assertThat( actual ).isSameAs( BED );
  }

  @Test
  public void getChildrenWithChildTypeParameter() {
    MyEntity expected = mock( MyEntity.class );
    stubEntityRelation( entityRelationProvider, BED, CHILD );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, CHILD, expected );

    Collection<MyEntity> actual = section.getChildren( MyEntityDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test
  public void getChildren() {
    MyEntity expected = mock( MyEntity.class );
    stubEntityRelationForAllChildren( entityRelationProvider, BED, CHILD );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, CHILD, expected );

    Collection<Entity<?>> actual = section.getChildren();

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getChildrenWithChildTypeParameterAndNullAsArgument() {
    section.getChildren( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDefinitionArgument() {
    new SectionImpl( null, entityRelationProvider );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEntityRelationProviderArgument() {
    new SectionImpl( BED, null );
  }

  @Test
  public void toStringImplementation() {
    String actual = section.toString();

    assertThat( actual.toString() ).contains( section.getDefinition().toString() );
  }
}