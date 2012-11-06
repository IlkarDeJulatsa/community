package org.neo4j.cypher.internal.spi.kernelimpl;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.impl.persistence.EntityIdGenerator;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

/**
 * This is part of a refactoring of NodeManager, which attempts to split responsibility into smaller components.
 */
public class NodeRepository
{

    private final EntityIdGenerator idGenerator;
    private final PersistenceManager persistenceManager;

    public NodeRepository( EntityIdGenerator idGenerator,  PersistenceManager persistenceManager )
    {
        this.idGenerator = idGenerator;
        this.persistenceManager = persistenceManager;
    }

    public long createNode()
    {
        long id = idGenerator.nextId(Node.class);
        persistenceManager.nodeCreate( id );
        return id;
    }

    public void deleteNode( long nodeId )
    {
        persistenceManager.nodeDelete( nodeId );
    }
}
