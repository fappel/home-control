package com.codeaffine.home.control.application.internal.section;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Collection;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.entity.EntityRelationProvider;
import com.codeaffine.home.control.entity.EntityRelationResolver;

public class SectionImpl implements Section {

  private final EntityRelationResolver<SectionDefinition> entityRelationResolver;

  SectionImpl( SectionDefinition definition, EntityRelationProvider relationProvider ) {
    verifyNotNull( relationProvider, "relationProvider" );
    verifyNotNull( definition, "definition" );

    this.entityRelationResolver = new EntityRelationResolver<>( definition, relationProvider );
  }

  @Override
  public <R extends Entity<C>, C extends EntityDefinition<R>> Collection<R> getChildren( Class<C> childType ) {
    return entityRelationResolver.getChildren( childType );
  }

  @Override
  public <R extends Entity<C>, C extends EntityDefinition<R>> Collection<Entity<?>> getChildren() {
    return entityRelationResolver.getChildren();
  }

  @Override
  public SectionDefinition getDefinition() {
    return entityRelationResolver.getDefinition();
  }

  @Override
  public String toString() {
    return "Section [definition=" + getDefinition() + "]";
  }
}
