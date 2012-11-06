package org.neo4j.cypher.internal.spi.kernelimpl;

/**
 * Handles taking global locks on database entities.
 */
public interface LockService
{

    void lockNode(long nodeId);
    void lockRelationship(long relationshipId);

    void unlockNode(long nodeId);
    void unlockRelationship(long relationshipId);

    void unlockAll();

}
