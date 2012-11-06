package org.neo4j.cypher.internal.spi.kernelimpl;

import java.util.Iterator;

import org.neo4j.cypher.internal.spi.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.impl.core.RelationshipTypeHolder;
import org.neo4j.kernel.impl.persistence.EntityIdGenerator;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

public class RelationshipRepository
{
    private final EntityIdGenerator idGenerator;
    private final PersistenceManager persistenceManager;
    private final RelationshipTypeHolder relationshipTypes;

    public RelationshipRepository( EntityIdGenerator idGenerator,
                                   PersistenceManager persistenceManager,
                                   RelationshipTypeHolder relationshipTypes )
    {
        this.idGenerator = idGenerator;
        this.persistenceManager = persistenceManager;
        this.relationshipTypes = relationshipTypes;
    }

    public int getRelationshipType( String relationshipTypeKey )
    {
        Integer id = relationshipTypes.getIdFor( relationshipTypeKey );
        if(id == null)
        {
            throw new RuntimeException( "No such relationship type, '" + relationshipTypeKey + "'" );
        }
        return id;
    }

    public int getOrCreateRelationshipType( String relationshipTypeKey )
    {
        RelationshipType type = relationshipTypes.addValidRelationshipType(
                relationshipTypeKey,
                /* createIfNeeded= */true );
        return relationshipTypes.getIdFor( relationshipTypeKey );
    }

    public long createRelationship( long startNodeId, long endNodeId, int relationshipTypeId )
    {
        long id = idGenerator.nextId( Relationship.class );
        persistenceManager.relationshipCreate( id, relationshipTypeId, startNodeId, endNodeId );

        // TODO: Figure out the cow stuff in kernel, we need to add this relationship to both nodes as well

        return id;
    }

    public void deleteRelationship( long relationshipId )
    {
        persistenceManager.relDelete( relationshipId );

        // TODO: Figure out the cow stuff in kernel, we need to remove this relationship from both nodes as well
    }

    public Iterator<Long> getRelationshipsFor( long nodeId, Direction dir, int ... types )
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
