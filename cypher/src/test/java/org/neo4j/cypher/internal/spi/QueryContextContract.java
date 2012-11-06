package org.neo4j.cypher.internal.spi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.neo4j.cypher.internal.spi.Direction.INCOMING;
import static org.neo4j.cypher.internal.spi.Direction.OUTGOING;

import java.util.Iterator;

import org.junit.Test;

public abstract class QueryContextContract
{

    public abstract QueryContext ctx();

    @Test
    public void shouldCreateNode() throws Exception
    {
        // When
        Long nodeId = ctx().createNode();

        // Then
        assertThat(nodeId, not(nullValue()));
    }

    @Test
    public void shouldSetAndGetNodeProperties() throws Exception
    {
        // Given
        long nodeId = ctx().createNode();
        int propertyKeyId = ctx().getOrCreatePropertyKeyId( "name" );

        // When
        ctx().setNodeProperty( nodeId, propertyKeyId, "BOB!!" );

        // Then
        assertThat( (String) ctx().getNodeProperty( nodeId, propertyKeyId ), is( "BOB!!" ) );
    }

    @Test
    public void shouldCreateRelationship() throws Exception
    {
        // Given
        long node1 = ctx().createNode();
        long node2 = ctx().createNode();

        int type = ctx().getOrCreateRelationshipTypeId( "KNOWS" );

        // When
        long relId = ctx().createRelationship( node1, node2, type );

        // Then
        Iterator<Long> rels1 = ctx().getRelationshipsFor( node1, OUTGOING, type );
        assertThat( rels1.hasNext(), is( true ) );
        assertThat( rels1.next(), is( relId ) );

        Iterator<Long> rels2 = ctx().getRelationshipsFor( node2, INCOMING, type );
        assertThat( rels2.hasNext(), is( true ) );
        assertThat( rels2.next(), is( relId ) );
    }

    @Test
    public void shouldSetAndGetRelationshipProperties() throws Exception
    {
        // Given
        long relId = ctx().createRelationship(
                ctx().createNode(),
                ctx().createNode(),
                ctx().getOrCreateRelationshipTypeId( "A" ) );

        int propertyKeyId = ctx().getOrCreatePropertyKeyId( "name" );

        // When
        ctx().setRelationshipProperty( relId, propertyKeyId, "BOB!!" );

        // Then
        assertThat( (String) ctx().getRelationshipProperty( relId, propertyKeyId ), is( "BOB!!" ) );
    }




}
