/*
 * Copyright (C) 2012 Neo Technology
 * All rights reserved
 */
package org.neo4j.cypher.internal.spi;

import java.util.Iterator;

/*
 * Developer note: This is an attempt at an internal graph database API, which defines a clean cut between
 * two layers, the query engine layer and, for lack of a better name, the core database layer.
 *
 * Building the query engine layer on top of an explictly internal layer means we can move much faster, not
 * having to worry about deprecations and so on. It is also acceptable if this layer is a bit clunkier, in this
 * case we are, for instance, not exposing any node or relationship objects, but provide direct methods for manipulating
 * them by ids instead.
 *
 * The driver for this was clarifying who is responsible for ensuring query isolation. By exposing a query concept in
 * the core layer, we can move that responsibility outside of the scope of cypher.
 */

/**
 * A query context exposes a set of read and/or write operations of core database primitives.
 * It operates within a transaction, which decides the isolation semantics of the query context.
 *
 * For instance, for a READ_COMMITTED isolated transaction, the query context will guarantee
 * REPEATBLE_READ isolation *within* the context of each individual query.
 */
public interface QueryContext
{

    int getOrCreatePropertyKeyId( String propertyKey );
    int getPropertyKeyId( String propertyKey );

    // Node operations

    long createNode( );
    void deleteNode( long nodeId );

    void setNodeProperty( long nodeId, int propertyKeyId, Object value );
    Object getNodeProperty( long nodeId, int propertyKeyId );

    // Relationship operations

    int getRelationshipTypeId( String relationshipTypeKey );
    int getOrCreateRelationshipTypeId( String relationshipTypeKey );

    long createRelationship( long startNodeId, long endNodeId, int relationshipTypeId );
    void deleteRelationship( long relationshipId );

    void setRelationshipProperty( long relationshipId, int propertyKeyId, Object value );
    Object getRelationshipProperty( long relationshipId, int propertyKeyId);

    // TODO: Expose a high-tech traversal framework here in some way, and remove the getRelationshipsFor method

    Iterator<Long> getRelationshipsFor( long nodeId, Direction dir, int ... types);

    /**
     * Release all resources held by this context.
     */
    void close();

}
