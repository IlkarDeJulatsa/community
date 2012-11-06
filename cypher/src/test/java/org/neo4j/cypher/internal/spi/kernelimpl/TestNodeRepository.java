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
