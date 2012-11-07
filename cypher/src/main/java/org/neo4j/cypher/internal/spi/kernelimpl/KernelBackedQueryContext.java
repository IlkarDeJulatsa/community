/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.spi.kernelimpl;

import java.util.Iterator;

import org.neo4j.cypher.internal.spi.Direction;
import org.neo4j.cypher.internal.spi.QueryContext;
import org.neo4j.graphdb.DependencyResolver;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.impl.core.RelationshipTypeHolder;
import org.neo4j.kernel.impl.persistence.EntityIdGenerator;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

/**
 * An attempt at implementing QueryContext on top of kernel components, rather than the user-level API.
 *
 * {@link KernelBackedQueryContext}s are responsible for coordinating the following sub-components:
 *
 * Repositories
 * ------------
 *
 * The query context is responsible for coordinating reading and writing data to Repositories, which in
 * turn are responsible for updating and reading transaction state and reading global database state.
 *
 * Traversals
 * ----------
 *
 * Not yet implemented.
 *
 * Concurrency
 * -----------
 * {@link QueryContext} are responsible for reading and writing to Repositories in a thread-safe manner across query contexts.
 * {@link QueryContext}s themselves are (externally) single-threaded, and no more than one query context can operate
 * on the same transaction at the same time. That said, a {@link QueryContext} could potentially use multiple threads
 * internally for more complex tasks, such as traversing.
 *
 * How they handle cross-{@link QueryContext} concurrency is up to the query context implementation (locks,
 * optimistic locks, or something different altogether), is up to the QueryContext implementation. However, it follows
 * from the task that all query contexts created by a given database need to have a joint understanding of how to
 * handle concurrency.
 *
 * Caching
 * -------
 *
 * {@link QueryContext}s can, if they want, include caching things. The cache layer must be transparent, and not
 * change the semantics of the isolation level that the query context fulfills.
 */
public class KernelBackedQueryContext implements QueryContext
{

    private final NodeRepository nodeRepo;
    private final RelationshipRepository relationshipRepo;
    private final PropertyRepository propertRepo;
    private final LockService transactionLocks;
    private final LockService queryLocks;

    /**
     * Temporary help factory, to create context from a GDB.
     * @param db
     * @return
     */
    public static KernelBackedQueryContext temporaryHelpCreateFrom(GraphDatabaseAPI db, LockService transactionLocks)
    {
        DependencyResolver resolver = db.getDependencyResolver();
        PersistenceManager persistenceManager = resolver.resolveDependency( PersistenceManager.class );
        EntityIdGenerator idGenerator         = resolver.resolveDependency( EntityIdGenerator.class );
        RelationshipTypeHolder relTypes       = resolver.resolveDependency( RelationshipTypeHolder.class );

        return new KernelBackedQueryContext(
                new NodeRepository(         idGenerator, persistenceManager ),
                new RelationshipRepository( idGenerator, persistenceManager, relTypes ),
                new PropertyRepository(     persistenceManager ),
                transactionLocks,
                new KernelLockService());
    }

    public KernelBackedQueryContext( NodeRepository nodeRepository,
                                     RelationshipRepository relationshipRepo,
                                     PropertyRepository propertyRepo,
                                     LockService transactionLocks,
                                     LockService queryLocks )
    {
        this.nodeRepo = nodeRepository;
        this.relationshipRepo = relationshipRepo;
        this.propertRepo = propertyRepo;
        this.transactionLocks = transactionLocks;
        this.queryLocks = queryLocks;
    }

    @Override
    public int getOrCreatePropertyKeyId( String propertyKey )
    {
        return propertRepo.getOrCreatePropertyKeyId( propertyKey );
    }

    @Override
    public int getPropertyKeyId( String propertyKey )
    {
        return propertRepo.getPropertyKeyId( propertyKey );
    }

    //
    // Nodes
    //

    @Override
    public long createNode()
    {
        return nodeRepo.createNode();
    }

    @Override
    public void deleteNode( long nodeId )
    {
        nodeRepo.deleteNode( nodeId );
    }

    @Override
    public void setNodeProperty( long nodeId, int propertyKeyId, Object value )
    {
        propertRepo.setNodeProperty( nodeId, propertyKeyId, value );
    }

    @Override
    public Object getNodeProperty( long nodeId, int propertyKeyId )
    {
        return propertRepo.getNodeProperty( nodeId, propertyKeyId );
    }

    //
    // Relationships
    //

    @Override
    public int getRelationshipTypeId( String relationshipTypeKey )
    {
        return relationshipRepo.getRelationshipType( relationshipTypeKey );
    }

    @Override
    public int getOrCreateRelationshipTypeId( String relationshipTypeKey )
    {
        return relationshipRepo.getOrCreateRelationshipType( relationshipTypeKey );
    }

    @Override
    public long createRelationship( long startNodeId, long endNodeId, int relationshipTypeId )
    {
        return relationshipRepo.createRelationship( startNodeId, endNodeId, relationshipTypeId );
    }

    @Override
    public void deleteRelationship( long relationshipId )
    {
        relationshipRepo.deleteRelationship(relationshipId);
    }

    @Override
    public void setRelationshipProperty( long relationshipId, int propertyKeyId, Object value )
    {
        propertRepo.setRelationshipProperty( relationshipId, propertyKeyId, value );
    }

    @Override
    public Object getRelationshipProperty( long relationshipId, int propertyKeyId )
    {
        return propertRepo.getRelationshipProperty( relationshipId, propertyKeyId );
    }

    //
    // Traversal
    //

    @Override
    public Iterator<Long> getRelationshipsFor( long nodeId, Direction dir, int ... types )
    {
        return relationshipRepo.getRelationshipsFor( nodeId, dir, types );
    }

    @Override
    public void close()
    {
    }
}
