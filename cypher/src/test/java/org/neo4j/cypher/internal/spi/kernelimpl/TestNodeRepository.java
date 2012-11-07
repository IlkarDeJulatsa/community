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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.impl.persistence.EntityIdGenerator;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

public class TestNodeRepository
{
    @Test
    public void testCreateNode() throws Exception
    {
        // Given
        EntityIdGenerator idGenerator = mock(EntityIdGenerator.class);
        PersistenceManager persistenceManager = mock(PersistenceManager.class);

        when( idGenerator.nextId( Node.class ) ).thenReturn( 1337l );

        NodeRepository nodeRepo = new NodeRepository( idGenerator, persistenceManager );

        // When
        long out = nodeRepo.createNode();

        // Then
        assertThat(out, is( 1337l ));
        verify( persistenceManager ).nodeCreate( 1337l );
    }

    @Test
    public void testDeleteNode() throws Exception
    {
        // Given
        EntityIdGenerator idGenerator = mock(EntityIdGenerator.class);
        PersistenceManager persistenceManager = mock(PersistenceManager.class);
        NodeRepository nodeRepo = new NodeRepository( idGenerator, persistenceManager );

        // When
        nodeRepo.deleteNode(1337l);

        // Then
        verify( persistenceManager ).nodeDelete( 1337l );
    }
}
