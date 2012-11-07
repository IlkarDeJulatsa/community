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
