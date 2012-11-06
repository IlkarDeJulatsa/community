package org.neo4j.cypher.internal.spi.gdsimpl;

import java.util.ArrayList;
import java.util.List;

public class KeyIndex
{
    private final List<String> index = new ArrayList<String>();

    public int getOrCreate( String key )
    {
        try
        {
            return getIndex( key );
        } catch(IndexOutOfBoundsException e)
        {
            // Not indexed, add and return
            index.add( key );
            return index.size() - 1;
        }
    }

    public String getKey( int keyId )
    {
        if(index.size() > keyId && keyId >= 0)
        {
            return index.get( (int) keyId );
        }

        throw new IndexOutOfBoundsException( "No key with index " + keyId + "." );
    }

    public int getIndex(String key)
    {
        for ( int i=0;i< index.size(); i++ )
        {
            String indexedKey = index.get(i);
            if(indexedKey.equals( key ))
            {
                return i;
            }
        }

        throw new IndexOutOfBoundsException( "No key named " + key + "." );
    }

}
