package org.neo4j.cypher.internal.spi.kernelimpl;

import org.neo4j.kernel.impl.persistence.PersistenceManager;

public class PropertyRepository
{
    public PropertyRepository( PersistenceManager persistenceManager )
    {

    }

    public int getOrCreatePropertyKeyId( String propertyKey )
    {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    public int getPropertyKeyId( String propertyKey )
    {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setNodeProperty( long nodeId, int propertyKeyId, Object value )
    {

    }

    public Object getNodeProperty( long nodeId, int propertyKeyId )
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setRelationshipProperty( long relationshipId, int propertyKeyId, Object value )
    {
        //To change body of created methods use File | Settings | File Templates.
    }

    public Object getRelationshipProperty( long relationshipId, int propertyKeyId )
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
