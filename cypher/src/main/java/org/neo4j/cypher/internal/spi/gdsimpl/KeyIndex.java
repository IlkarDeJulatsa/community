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
