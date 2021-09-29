package com.njupt.demo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.internal.javacompat.ExecutionEngine;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.*;
public class Test1 {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("Data/Test"));
        System.out.println("Database Load!");
        //开启事务
        try (Transaction tx = graphDb.beginTx()) {
            // Perform DB operations
            Node steve = graphDb.createNode(Labels.USER);
            steve.setProperty("name", "Steve");
            Node linda = graphDb.createNode(Labels.USER);
            linda.setProperty("name", "Linda");
            steve.createRelationshipTo( linda, RelationshipTypes.IS_FRIEND_OF );
            System.out.println("created node name is" + steve.getProperty("name"));
            ArrayList<Node> nodes1=new ArrayList<Node>();
            ArrayList<Node> nodes2=new ArrayList<Node>();
            nodes1.add(steve);
            nodes1.add(linda);
            nodes2.add(steve);
            nodes2.add(linda);
            System.out.println("------------");
            System.out.println(nodes1.equals(nodes2));
            List<ArrayList<Node>> Nodes1=new  ArrayList<ArrayList<Node>>();
            List<ArrayList<Node>> Nodes2=new  ArrayList<ArrayList<Node>>();
            Nodes1.add(nodes1);
            Nodes1.add(nodes2);
            Nodes2.add(nodes1);
            Nodes2.add(nodes2);
            System.out.println("------------");
            System.out.println(Nodes1.equals(Nodes2));
            System.out.println(Nodes1.contains(nodes1));
            System.out.println(Nodes2.contains(nodes2));
            
            
            tx.success();
        }
        
        //查询数据库
        String query ="match (n:USER) return n.name as name";
        Map<String, Object >parameters = new HashMap<String, Object>();
         try ( Result result = graphDb.execute( query, parameters ) )
         {
             while ( result.hasNext() )
             {
                 Map<String, Object> row = result.next();
                 for ( String key : result.columns() )
                 {
                     System.out.printf( "%s = %s%n", key, row.get( key ) );
                 }
             }
         }

         registerShutdownHook(graphDb);
         System.out.println("Database Shutdown!");

    }
    //设置标签，但是必须继承于接口label
    public enum Labels implements Label {
        USER,
        MOVIE;
    }

    public enum RelationshipTypes implements RelationshipType {
        IS_FRIEND_OF,
        HAS_SEEN;
    }


    private static void registerShutdownHook(final GraphDatabaseService graphDb){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                graphDb.shutdown();
            }
        });
    }

}