package com.codeaffine.home.control.application.internal.section;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;

public class SectionFactory implements EntityFactory<Section, SectionDefinition> {

  private final EntityRelationProvider entityRelationProvider;

  public SectionFactory( EntityRelationProvider entityRelationProvider ) {
    verifyNotNull( entityRelationProvider, "entityRelationProvider" );

    this.entityRelationProvider = entityRelationProvider;
  }

  @Override
  public Section create( SectionDefinition definition ) {
    verifyNotNull( definition, "definition" );

    return new SectionImpl( definition, entityRelationProvider );
  }
}