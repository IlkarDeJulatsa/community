package org.neo4j.cypher.internal.spi.gdsimpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.cypher.internal.spi.QueryContext;
import org.neo4j.cypher.internal.spi.QueryContextContract;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.ImpermanentGraphDatabase;

public class GDSQueryContextContractIT extends QueryContextContract
{

    private static ImpermanentGraphDatabase gdb;
    private QueryContext ctx;
    private Transaction tx;

    @BeforeClass
    public static void createDb()
    {
        gdb = new ImpermanentGraphDatabase( );
    }


    @Before
    public void createQueryContext()
    {
        tx = gdb.beginTx();
        ctx = new GDSBackedQueryContext( gdb );
    }

    @After
    public void clean()
    {
        ctx.close();
        tx.finish();
    }

    @AfterClass
    public static void destroy()
    {
        gdb.shutdown();
    }


    @Override
    public QueryContext ctx()
    {
        return ctx;
    }


}
