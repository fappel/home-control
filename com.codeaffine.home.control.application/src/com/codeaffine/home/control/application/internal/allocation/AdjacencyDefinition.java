package com.codeaffine.home.control.application.internal.allocation;

import static com.codeaffine.home.control.application.internal.allocation.Messages.ERROR_ENTITY_DEFINITION_NO_ELEMENT_ADJACENCY_DEFINITION;
import static com.codeaffine.util.ArgumentVerification.*;
import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class AdjacencyDefinition {

  private final Map<EntityDefinition<?>, Set<EntityDefinition<?>>> edges;

  public AdjacencyDefinition( Set<? extends EntityDefinition<?>> definitions ) {
    verifyNotNull( definitions, "definitions" );

    edges = definitions.stream().collect( toMap( definition -> definition, definition -> new HashSet<>() ) );
  }

  public AdjacencyDefinition link( EntityDefinition<?> definition1, EntityDefinition<?> definition2 ) {
    verifyNotNull( definition1, "definition1" );
    verifyCondition( isElement( definition1 ), ERROR_ENTITY_DEFINITION_NO_ELEMENT_ADJACENCY_DEFINITION, definition1 );
    verifyNotNull( definition2, "defintion2" );
    verifyCondition( isElement( definition2 ), ERROR_ENTITY_DEFINITION_NO_ELEMENT_ADJACENCY_DEFINITION, definition2 );

    edges.get( definition1 ).add( definition2 );
    edges.get( definition2 ).add( definition1 );
    return this;
  }

  public boolean isAdjacent( EntityDefinition<?> definition1, EntityDefinition<?> definition2 ) {
    verifyNotNull( definition1, "definition1" );
    verifyCondition( isElement( definition1 ), ERROR_ENTITY_DEFINITION_NO_ELEMENT_ADJACENCY_DEFINITION, definition1 );
    verifyNotNull( definition2, "definition2" );
    verifyCondition( isElement( definition2 ), ERROR_ENTITY_DEFINITION_NO_ELEMENT_ADJACENCY_DEFINITION, definition2 );

    return edges.get( definition1 ).contains( definition2 );
  }

  private boolean isElement( EntityDefinition<?> definition ) {
    return edges.containsKey( definition );
  }
}