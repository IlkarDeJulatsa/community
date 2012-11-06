package org.neo4j.cypher.internal.spi.gdsimpl;

import java.util.Iterator;

import org.neo4j.cypher.internal.spi.Direction;
import org.neo4j.cypher.internal.spi.QueryContext;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.IteratorWrapper;

/**
 * An implementation of a query context on top of the existing GraphDatabaseService.
 *
 * It provides repeatable read isolation level.
 */
public class GDSBackedQueryContext implements QueryContext
{
    private final GraphDatabaseService db;

    /**
     * We temporarily use a query-local index for property keys, this is to
     * be replaced by the real property key index used by the database.
     */
    private final KeyIndex propertyIndex = new KeyIndex( );

    /**
     * We temporarily use a query-local index for relationship types, this is to
     * be replaced by the real relationship type index used by the database.
     */
    private final KeyIndex relationshipTypeIndex = new KeyIndex( );

    public GDSBackedQueryContext( GraphDatabaseService db )
    {
        this.db = db;
    }

    @Override
    public int getOrCreatePropertyKeyId( String propertyKey )
    {
        return propertyIndex.getOrCreate( propertyKey );
    }

    @Override
    public int getPropertyKeyId( String propertyKey )
    {
        return propertyIndex.getIndex( propertyKey );
    }

    @Override
    public long createNode()
    {
        return db.createNode().getId();
    }

    @Override
    public void deleteNode( long nodeId )
    {
        db.getNodeById( nodeId ).delete();
    }

    @Override
    public void setNodeProperty( long nodeId, int propertyKeyId, Object value )
    {
        db.getNodeById( nodeId )
                .setProperty( propertyIndex.getKey( propertyKeyId ), value );
    }

    @Override
    public Object getNodeProperty( long nodeId, int propertyKeyId )
    {
        return db.getNodeById( nodeId ).getProperty( propertyIndex.getKey( propertyKeyId ) );
    }

    @Override
    public int getRelationshipTypeId( String relationshipTypeKey )
    {
        return relationshipTypeIndex.getIndex( relationshipTypeKey );
    }

    @Override
    public int getOrCreateRelationshipTypeId( String relationshipTypeKey )
    {
        return relationshipTypeIndex.getOrCreate( relationshipTypeKey );
    }

    @Override
    public long createRelationship( long startNodeId, long endNodeId, int relationshipTypeId )
    {
        return db.getNodeById( startNodeId )
                .createRelationshipTo( db.getNodeById( endNodeId ),
                        DynamicRelationshipType.withName( relationshipTypeIndex.getKey( relationshipTypeId ) ) )
                .getId();
    }

    @Override
    public void deleteRelationship( long relationshipId )
    {
        db.getRelationshipById( relationshipId ).delete();
    }

    @Override
    public void setRelationshipProperty( long relationshipId, int propertyKeyId, Object value )
    {
        db.getRelationshipById( relationshipId ).setProperty( propertyIndex.getKey( propertyKeyId ), value );
    }

    @Override
    public Object getRelationshipProperty( long relationshipId, int propertyKeyId )
    {
        return db.getRelationshipById( relationshipId ).getProperty( propertyIndex.getKey( propertyKeyId ) );
    }

    @Override
    public Iterator<Long> getRelationshipsFor( long nodeId, Direction dir, int ... types )
    {
        if(types.length == 0)
        {
            return toLongIterator( db.getNodeById( nodeId ).getRelationships( toGDSDirection( dir ) ) );
        } else
        {
            return toLongIterator( db.getNodeById( nodeId ).getRelationships(
                    toGDSDirection( dir ),
                    toGDSRelationshipTypes(types) ) );
        }
    }

    private RelationshipType [] toGDSRelationshipTypes( int [] types )
    {
        RelationshipType [] relTypes = new RelationshipType[types.length];
        for(int i=0; i < types.length; i++)
        {
            relTypes[i] = DynamicRelationshipType.withName( relationshipTypeIndex.getKey( types[i]) );
        }

        return relTypes;
    }

    private Iterator<Long> toLongIterator( Iterable<Relationship> relationships )
    {
        final Iterator<Relationship> iter = relationships.iterator();
        return new IteratorWrapper<Long, Relationship>(iter)
        {
            @Override
            protected Long underlyingObjectToObject( Relationship rel )
            {
                return rel.getId();
            }
        };
    }

    private org.neo4j.graphdb.Direction toGDSDirection( Direction dir )
    {
        switch(dir)
        {
            case INCOMING:
                return org.neo4j.graphdb.Direction.INCOMING;
            case OUTGOING:
                return org.neo4j.graphdb.Direction.OUTGOING;
            default:
                return org.neo4j.graphdb.Direction.BOTH;
        }
    }

    @Override
    public void close()
    {
    }
}
