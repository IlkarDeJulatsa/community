package org.neo4j.cypher.internal.spi.kernelimpl;

public class KernelLockService implements LockService
{
    @Override
    public void lockNode( long nodeId )
    {
    }

    @Override
    public void lockRelationship( long relationshipId )
    {
    }

    @Override
    public void unlockNode( long nodeId )
    {
    }

    @Override
    public void unlockRelationship( long relationshipId )
    {
    }

    @Override
    public void unlockAll()
    {
    }
}
