package com.codeaffine.home.control.application.internal.section;

import static com.codeaffine.home.control.application.internal.section.EntityRelationHelper.*;
import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.BedRoomCeiling;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.internal.section.SectionImpl;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityRelationProvider;

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
    Lamp expected = mock( Lamp.class );
    stubEntityRelation( entityRelationProvider, BED, BedRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, BedRoomCeiling, expected );

    Collection<Lamp> actual = section.getChildren( LampDefinition.class );

    assertThat( actual )
      .hasSize( 1 )
      .contains( expected );
  }

  @Test
  public void getChildren() {
    Lamp expected = mock( Lamp.class );
    stubEntityRelationForAllChildren( entityRelationProvider, BED, BedRoomCeiling );
    stubRegistryWithEntityInstanceForDefinition( entityRelationProvider, BedRoomCeiling, expected );

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