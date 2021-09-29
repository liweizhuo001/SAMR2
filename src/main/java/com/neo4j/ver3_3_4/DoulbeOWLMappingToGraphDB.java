package com.neo4j.ver3_3_4;

import static com.njupt.util.GlobalParams.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import com.njupt.util.Tools;



import com.njupt.util.AxiomMapping;
import com.njupt.util.DisjointPair4Mappings;
import com.njupt.util.GlobalAttribute;
import com.njupt.util.MIPS;
import com.njupt.util.MappingInfo;
import com.njupt.util.OWLInfo;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;

/**
 * 将OWLInfo与Mappings转换成图数据库，图中有两类节点，分别是概念节点和角色节点。<br>
 * 概念节点：有三个属性分别是节点名，节点类型，节点来源。<br>
 * 角色节点：属性分别是，名称：便于索引，类型：是从concept的关系还是role的关系
 * @author Weizhuo Li
 */
public class DoulbeOWLMappingToGraphDB {		                                             
	public GraphDatabaseService graphDb;	
	public OWLInfo owlInfo1;
	public OWLInfo owlInfo2; 
	private MappingInfo MappingInformation;
	private Index<Node> nodeIndex;                               //节点索引，方便查找节点	
	private Index<Relationship> relationshipIndex;        //关系索引，方便查找节点
	
	ArrayList<MIPS> MIPPs; 
	HashMap<Relationship, List<MIPS>> relMappingMIPSs;
	
	HashMap<String, HashSet<String>> subAxioms =new HashMap<String, HashSet<String>>();
	HashMap<String, HashSet<String>> supAxioms =new HashMap<String, HashSet<String>>();
	HashMap<String, HashSet<String>> disAxioms= new HashMap<String, HashSet<String>>();
	
	ArrayList<String> Mappings= new ArrayList<String>();
	ArrayList<Relationship> mappingSets= new ArrayList<Relationship>();
	ArrayList<Relationship> wantedRelationships= new ArrayList<Relationship>();
	ArrayList<Relationship> unWantedRelationships= new ArrayList<Relationship>();
	
	HashMap<Relationship, String> relToMap=new HashMap<Relationship, String>();
	HashMap<String, HashSet<Relationship>> mapToRel=new HashMap<String, HashSet<Relationship>>();
	HashMap<Relationship, Double> relToWeight=new HashMap<Relationship, Double>();
	
	public ArrayList<String> removedMappings;
	public ArrayList<String> approvedMappings;
	
	public int approvedNum=0,rejectNum=0;

	
	/**
	 * 构造函数
	 * @param dbPath           		图数据库存储路径
	 * @param owlPath1                                          本体1的路径
	 * @param owlPath2                                          本体2的路径
	 * @param mappingPath    两个本体之间的匹配信息
	 * @param isClear               是否清除已存在的同名图数据库
	 * @throws IOException 
	 */	
	public DoulbeOWLMappingToGraphDB(String dbPath, String owlPath1, String owlPath2,String mappingPath,boolean isClear) throws IOException{		
		//是否清除已经存在的数据库（否则再次写入会出现异常）
		if(isClear){
			Tools4Graph.clearDb(dbPath);
		}		
		//创建一个新的数据库或者打开一个已经存在的数据库，实例可以在多个线程中共享，但不能创建多个实例来指向同一个数据库
		//graphDb = new EmbeddedGraphDatabase(dbPath);		这是1.8版本中的表达方式，已经过期
		//graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new  File(dbPath) );
		Tools4Graph.registerShutdownHook(graphDb);  //挂钩，程序结束自动调用
		try(Transaction tx = graphDb.beginTx()){
			IndexManager indexM = graphDb.index();
			nodeIndex = indexM.forNodes(NODEINDEX);  //节点索引，参数是索引的名字			
			relationshipIndex = indexM.forRelationships(RELATIONSHIPINDEX); //关系索引
		}
		owlInfo1 = new OWLInfo(owlPath1);	
		owlInfo2 = new OWLInfo(owlPath2);	
		
		MappingInformation=new MappingInfo(mappingPath); 
	}
	
	
	public OWLInfo getOwlInfo1() {
		return owlInfo1;
	}
	
	public OWLInfo getOwlInfo2() {
		return owlInfo2;
	}
	
	public MappingInfo getMappingInfo() {
		return MappingInformation;
	}
	
	public GraphDatabaseService getGraphDB() {
		return graphDb;
	}
	
	public Index<Node> getNodeIndex() {
		return nodeIndex;
	}
	
	public Index<Relationship> getRelationShipIndex(){
		return relationshipIndex;
	}
	
	public ArrayList<DisjointPair4Mappings> getDisjointPairs()
	{
		ArrayList<DisjointPair4Mappings> disPairs=new ArrayList<DisjointPair4Mappings>();
		for (OWLDisjointClassesAxiom ax: owlInfo1.getAxiomsOfDisjointClass())
		{
			List<OWLClassExpression> classes = ax.getClassExpressionsAsList();
			String subKey = ((OWLClass)classes.get(0)).getIRI().getFragment();
			String supKey = ((OWLClass)classes.get(1)).getIRI().getFragment();
			//存储原子概念的否定以及它自身即可
			DisjointPair4Mappings dispair= new DisjointPair4Mappings(supKey,Tools.getNegativeToken(supKey),COMEFROMFIRST);
			//System.out.println(supKey+"+"+Tools.getNegativeToken(supKey));
			if(!disPairs.contains(dispair))
				disPairs.add(dispair);	
			
			
			//create disjoint relationship Map
			if(disAxioms.containsKey(subKey))
			{
				disAxioms.get(subKey).add(supKey);
			}
			else
			{
				HashSet<String> tempSet=new HashSet<String>();
				tempSet.add(supKey);
				disAxioms.put(subKey, tempSet);
			}
			
			if(disAxioms.containsKey(supKey))
			{
				disAxioms.get(supKey).add(subKey);
			}
			else
			{
				HashSet<String> tempSet=new HashSet<String>();
				tempSet.add(subKey);
				disAxioms.put(supKey, tempSet);
			}
		}
		
		for (OWLDisjointClassesAxiom ax: owlInfo2.getAxiomsOfDisjointClass())
		{
			List<OWLClassExpression> classes = ax.getClassExpressionsAsList();
			String subKey = ((OWLClass)classes.get(0)).getIRI().getFragment();
			String supKey = ((OWLClass)classes.get(1)).getIRI().getFragment();
			//存储原子角色的否定以及它自身即可
			DisjointPair4Mappings dispair= new DisjointPair4Mappings(supKey,Tools.getNegativeToken(supKey),COMEFROMSECOND);
			if(!disPairs.contains(dispair))
				disPairs.add(dispair);
			
			//create disjoint relationship Map
			if(disAxioms.containsKey(subKey))
			{
				disAxioms.get(subKey).add(supKey);
			}
			else
			{
				HashSet<String> tempSet=new HashSet<String>();
				tempSet.add(supKey);
				disAxioms.put(subKey, tempSet);
			}
			
			if(disAxioms.containsKey(supKey))
			{
				disAxioms.get(supKey).add(subKey);
			}
			else
			{
				HashSet<String> tempSet=new HashSet<String>();
				tempSet.add(subKey);
				disAxioms.put(supKey, tempSet);
			}
		}	
		
		//属性的情况待扩展
		
		return disPairs;
	}
	
	public ArrayList<MIPS> getMIPPs(ArrayList<DisjointPair4Mappings> disPairs)
	{
		MIPPs =new ArrayList<MIPS>();
		System.out.println("disjoint pairs:"+disPairs.size());
		int iCount = 0, n=0;
		long ticStart=System.currentTimeMillis();
		while(iCount < disPairs.size()){
			try(Transaction tx = graphDb.beginTx()){
				DisjointPair4Mappings dispair=disPairs.get(iCount);
				System.out.println("The disjoint pair is "+dispair.getFirst() + " " + dispair.getSecond());
				//System.out.println("The ontology is "+dispair.getSource());
				
				Map<String, Object> params = new HashMap<>();
				params.put( "pos", dispair.getFirst() );
				params.put( "neg", dispair.getSecond() );
			    params.put( "pairsource", dispair.getSource() );	
			    StringBuilder query = new StringBuilder();
			    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
			    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
			    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
			    //query.append(" and uc.Name=$uc and n.Name=$pos and t.Name=$neg ");
			    query.append(" and n.Name=$pos and t.Name=$neg ");
			    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
			    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
			    //query.append(" and uc.ComeFrom=$comefrom and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
			    query.append(" and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
			    query.append("RETURN uc,pp,np");
			    System.out.println(query);
			    //Result result = graphDb.execute(query.toString());
			    Result result = graphDb.execute(query.toString(),params);
			    Node uc=null;
			    Path pPath=null;
			    Path nPath=null;
			    while (result.hasNext()) 
			    {
			    	Map<String, Object> temp = result.next();
			    	uc= (Node) temp.get("uc");
			        pPath = (Path) temp.get("pp");
			        nPath = (Path) temp.get("np");		        
			        if(pPath==null||nPath==null)
				    	continue;
			        n++;
				    MIPS mipp = new MIPS(uc, dispair.getFirst(), dispair.getSecond(), dispair.getSource(), pPath, nPath);	
				    MIPPs.add(mipp);
				    //mipp.printMIPS();
				    //mipp.printMappings();
			    }
			    
			    System.out.println("===============================");
			    iCount++; 
			    tx.success();
			}
			
			}
		
		long tocEnd=System.currentTimeMillis();
	    System.out.println("number of MIPP is "+n);
	    System.out.println("number of MIPP is "+MIPPs.size());
		System.out.println("查询路径消耗的时间为："+ (tocEnd-ticStart)+" ms");
		
		return MIPPs;
	}
	
	public HashMap<Relationship, List<MIPS>> calRMappingM(ArrayList<MIPS> MIPPs)
	{
		relMappingMIPSs =new HashMap<Relationship, List<MIPS>>();
		long ticStart=System.currentTimeMillis();
		try(Transaction tx = graphDb.beginTx()){
		for (MIPS mips: MIPPs)
		{
			Set<Relationship> relMapping= mips.getincoherenceMappings();
			for(Relationship r:relMapping)
			{
				//System.out.println(r.getStartNode().getProperty(NAMEPROPERTY)+"->"+r.getEndNode().getProperty(NAMEPROPERTY));
				if(r.getProperty(TYPEPROPERTY).equals("Role"))			//只单独对纯角色的等价进行了限制
				{
					Relationship stemRelationship=null;
					String comefrom=r.getStartNode().getProperty(COMEFROMPROPERTY).toString();
					String subKey=r.getStartNode().getProperty(NAMEPROPERTY).toString().replace("existence_", "").replace("inverse_", "");
					String supKey=r.getEndNode().getProperty(NAMEPROPERTY).toString().replace("existence_", "").replace("inverse_", "");
					if(subKey.equals(supKey)&&comefrom.equals(COMEFROMFIRST))
					{
						String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
						stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					}
					else if((subKey.equals(supKey)&&comefrom.equals(COMEFROMSECOND)))
					{
						String relationshipName = Tools4Graph.getRelationshipName(subKey+"_2", supKey+"_1");		//***
						stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					}
					else 
					{
						String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
						stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					}
					List<MIPS> current = relMappingMIPSs.get(stemRelationship);  
					if(current == null)
					{
						current = new ArrayList<MIPS>();
						relMappingMIPSs.put(stemRelationship,current);
					}
					if(!(current.contains(mips)))
					{
						current.add(mips);
					}
					
				}
				else 
				{
					List<MIPS> current = relMappingMIPSs.get(r);  
					if(current == null)
					{
						current = new ArrayList<MIPS>();
						relMappingMIPSs.put(r,current);
					}
					if(!(current.contains(mips)))
					{
						current.add(mips);
					}		
				}
			}
		}
		//printRelMappingMIMPP(relMappingMIPSs);
		long tocEnd=System.currentTimeMillis();
		System.out.println("The number of Map-MIPS Set is："+ relMappingMIPSs.keySet().size());
		System.out.println("构建mappings-MIPS的表格的时间为："+ (tocEnd-ticStart)+" ms");
		tx.success();
		}
		return relMappingMIPSs;
	}
	
	public ArrayList<Relationship> goRevisionSimple(Map<Relationship, List<MIPS>> relMappingMIPSs, ArrayList<MIPS> MIPPs)
	{
		System.out.println("Begin simple mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			long ticStart=System.currentTimeMillis();
			ArrayList<Relationship> removedRelationship= new ArrayList<Relationship>();
			Relationship _rel = null;
			int iteration=1;
			while(!MIPPs.isEmpty())
			{
				System.out.println("The time of iteration is "+iteration++);
				List<Relationship> minmumWeightedRelationship=getMinWeight(relMappingMIPSs);
				
				//if(minmumWeightedRelationship.size()==1)				
				_rel=minmumWeightedRelationship.get(0);  //这里就考虑第一个，当然也可以选取人工选取的方式。
				String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY);
				System.out.println("The removed mapping is "+mapping);
				System.out.println("The relation expressed in the graph is " + _rel.toString());
				//重点在于怎么删除整个匹配
				List<MIPS> removedMIPSs=relMappingMIPSs.get(_rel);
				MIPPs.removeAll(removedMIPSs);
				java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
				while(iter.hasNext())
				{
					Relationship r=iter.next().getKey();					
					for(int i=0;i<relMappingMIPSs.get(r).size();i++)
					{
						MIPS mips=relMappingMIPSs.get(r).get(i);
						//mips.printMIPS();
						//迭代的方式进行动态移除
						if(mips.unifyMappings(relationshipIndex).contains(_rel)) //归一的方式
						{
							relMappingMIPSs.get(r).remove(mips);
							i--;
						}
					}
					if(relMappingMIPSs.get(r).isEmpty())
						iter.remove();
				}		
				removedRelationship.add(_rel); //其实在角色的更新时就不需要再做一次关于角色的过滤操作了
			}
			removedMappings=updateGraph(removedRelationship,null, graphDb); //其实最终要不要更新的可选的
//			System.out.println("The number of removed mappings is "+ removedMappings.size());
//			for(String a:removedMappings)
//			{
//				System.out.println(a);
//			}
			long tocEnd=System.currentTimeMillis();
			System.out.println("获得移除Mappings的时间为："+ (tocEnd-ticStart)+" ms");
			return removedRelationship;
		}
	}
	
	public ArrayList<Relationship> goRevisionComplex(Map<Relationship, List<MIPS>> relMappingMIPSs, ArrayList<MIPS> MIPPs)
	{
		System.out.println("Begin complex mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			ArrayList<Relationship> removedRelationship= new ArrayList<Relationship>();
			Relationship _rel = null;
			while(!MIPPs.isEmpty())
			{
				ArrayList<Relationship> selectRelationship=getMaxRel(relMappingMIPSs);			
				if(selectRelationship.size()==1)				
					_rel=selectRelationship.get(0);  //这里就考虑第一个，当然也可以选取人工选取的方式。
				else
					selectRelationship=getMinWeight(selectRelationship);
				_rel=selectRelationship.get(0);
				String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY);
				System.out.println(mapping);
				//重点在于怎么删除整个匹配
				List<MIPS> removedMIPSs=relMappingMIPSs.get(_rel);
				MIPPs.removeAll(removedMIPSs);
				java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
				while(iter.hasNext())
				{
					Relationship r=iter.next().getKey();					
					for(int i=0;i<relMappingMIPSs.get(r).size();i++)
					{
						MIPS mips=relMappingMIPSs.get(r).get(i);
						mips.printMIPS();
						//if(mips.getincoherenceMappings().contains(_rel))
						if(mips.unifyMappings(relationshipIndex).contains(_rel))
						{
							relMappingMIPSs.get(r).remove(mips);
							i--;
						}
					}
					if(relMappingMIPSs.get(r).isEmpty())
						iter.remove();
				}		
				removedRelationship.add(_rel); //其实在角色的更新时就不需要再做一次关于角色的过滤操作了
			}
			removedMappings=updateGraph(removedRelationship,null, graphDb);
//			System.out.println("The number of removed mappings is "+ removedMappings.size()+". The removed mappings are listed as follows: ");
//			for(String a:removedMappings)
//			{
//				System.out.println(a);
//			}
			return removedRelationship;
		}
	}
	
	public ArrayList<Relationship> goRevisionByInteractiveOne2One()
	{
		int interation=0;
		
		System.out.println("Begin interactive mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			while(Mappings.size()!=0)
			{
				System.out.println("The current iteration is "+interation++);
				Relationship _rel = null;
				Scanner sc = new Scanner(System.in);  
				//影响函数可以考虑进一步细化
				//_rel=getMaxRel(relMappingMIPSs).get(0);
				//_rel=getMinWeight(relMappingMIPSs).get(0);
				//_rel=getMinWeight();
				
				//_rel=getCandidateMappingByStructure();  //AAAI08Meilicke等人的方法
				
				//_rel=getCandidateMappingArcByGuaranteed(); //JWS2012 简化版方法
				
				//_rel=getCandidateMappingArcByNormed(); //JWS2012 强化版本
				
				_rel=getCandidateMappingArcByImpactor(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight1(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight2(); //本文提出的方法
				
				if(_rel!=null)
				{
					//String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY)+
					//		","+_rel.getProperty(COMEFROMPROPERTY).toString()+","+_rel.getProperty(WEIGHTEDPROPERTY).toString();
					String mapping=relToMap.get(_rel);
					System.out.println("The current mapping is "+mapping);
					System.out.println("The relation expressed in the graph is " + mapping);
					System.out.println("Please make a judgement for this mapping (Y/N):");  
					if(sc.nextLine().equalsIgnoreCase("Y"))
					{ 	   		        	
						System.out.println("ִExecute the action according to the approved mapping.");  
						//wantedRelationships.add(_rel);	
						//removed mapping by 1 to 1 constraint				
						InferencedGraph1By1(_rel); //1对1的约束
						approvedNum++;
					}
					else
					{
						System.out.println("Execute the action according to the declined mapping.");
						//removed operation is finished in the process of selection.
						unWantedRelationships.add(_rel);
						rejectNum++;
					}		        
				}
				else 
				{
					break;
				}
			}
			System.out.println("The number of approved mappings is "+approvedNum);
			System.out.println("The number of rejected mappings is "+rejectNum);
			//removedMappings=obtainMappingByRel(unWantedRelationships);
			approvedMappings=obtainMappingByRel(wantedRelationships);
			removedMappings=obtainMappingByRel(unWantedRelationships);
		}			
		return unWantedRelationships;	
	}
	
	public ArrayList<Relationship> goRevisionByAutoOne2One(ArrayList<String> referenceMappings)
	{
		int interation=0;
			
		System.out.println("Begin interactive mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			while(Mappings.size()!=0)
			{
				System.out.println("===================================="); 
				System.out.println("The current iteration is "+interation++);
				Relationship _rel = null;
				//影响函数可以考虑进一步细化
				//_rel=getMaxRel(relMappingMIPSs).get(0);
				//_rel=getMinWeight(relMappingMIPSs).get(0);
				_rel=getMinWeight();
				
				//_rel=getCandidateMappingByStructure();  //AAAI08Meilicke等人的方法
				
				//_rel=getCandidateMappingArcByGuaranteed(); //JWS2012 简化版方法
				
				//_rel=getCandidateMappingArcByNormed(); //JWS2012 强化版本
				
				//_rel=getCandidateMappingArcByImpactor(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight1(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight2(); //本文提出的方法
				
				if(_rel!=null)
				{
//					String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY)+
//							","+_rel.getProperty(COMEFROMPROPERTY).toString()+","+_rel.getProperty(WEIGHTEDPROPERTY).toString();
					String mapping=relToMap.get(_rel);
					System.out.println("The current mapping arc is "+mapping);

					String correspondingMapping=relToMap.get(_rel);	
					String parts[]=correspondingMapping.split(",");
					String originalMapping=parts[0]+","+parts[1]+",=,1.0";
					System.out.println("Make a judgement by Oracal");  					
					if(referenceMappings.contains(originalMapping)&&!isUserFailing())
					{ 	   		        	
						System.out.println("ִExecute the action according to the approved mapping.");  
						//wantedRelationships.add(_rel);	
						//removed mapping by 1 to 1 constraint				
						InferencedGraph1By1(_rel); //1对1的约束
						approvedNum++;
					}
					else
					{
						System.out.println("Execute the action according to the declined mapping.");
						//removed operation is finished in the process of selection.
						unWantedRelationships.add(_rel);
						rejectNum++;
					}		        
				}
				else 
				{
					break;
				}
			}
			System.out.println("The number of approved mappings is "+approvedNum);
			System.out.println("The number of rejected mappings is "+rejectNum);
			//removedMappings=obtainMappingByRel(unWantedRelationships);			
			approvedMappings=obtainMappingByRel(wantedRelationships);
			removedMappings=obtainMappingByRel(unWantedRelationships);
		}			
		return unWantedRelationships;	
	}
	
	public ArrayList<Relationship> goRevisionByInteractiveByReasoner()
	{
		int interation=1;
//		ArrayList<Relationship> wantedRelationships= new ArrayList<Relationship>();
//		ArrayList<Relationship> unWantedRelationships= new ArrayList<Relationship>();
		System.out.println("Begin interactive mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			Relationship _rel = null;
			//需要对图所有的匹配弧进行移除			
			//while(!MIPPs.isEmpty())
			while(Mappings.size()!=0)
			{
				System.out.println("The current iteration is "+interation++);
				Scanner sc = new Scanner(System.in);  
				//影响函数可以考虑进一步细化

				//_rel=getMaxRel(relMappingMIPSs).get(0);
				//_rel=getMinWeight(relMappingMIPSs).get(0);
				//_rel=getMinWeight();
				
				//_rel=getCandidateMappingByStructure();  //AAAI08Meilicke等人的方法
				
				//_rel=getCandidateMappingArcByGuaranteed(); //JWS2012 简化版方法
				
				//_rel=getCandidateMappingArcByNormed(); //JWS2012 强化版本
				
				_rel=getCandidateMappingArcByImpactor(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight1(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight2(); //本文提出的方法
				
				
				//String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY);
				String mapping=relToMap.get(_rel);
				System.out.println("The current mapping is "+mapping);
				//System.out.println("The relation expressed in the graph is " + _rel.toString());
		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equals("Y")||sc.nextLine().equals("y"))
		        { 	   		        	
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            wantedRelationships.add(_rel);	
		            //添加就是最初构建图数据库的方式,但是比较保守，会考虑到包含关系
		            simpleApproved(_rel,wantedRelationships,unWantedRelationships);
		            //更新图，
		            approvedNum++;
		        }
		        else
		        {
		        	System.out.println("Execute the action according to the rejected mapping.");
		        	unWantedRelationships.add(_rel);
		        	//移除的方式就是图数据库的删除方式,但是比较粗暴，会考虑到包含关系
		        	simpleReject2(_rel,wantedRelationships,unWantedRelationships);
		        	//entailedReject(_rel,wantedRelationships,unWantedRelationships);
					rejectNum++;
		        }
			}	
			System.out.println("The number of approved operation is "+approvedNum);
			System.out.println("The number of rejected operation is "+rejectNum);
			//removedMappings=obtainMappingByRel(unWantedRelationships);
			//removedMappings=obtainMappingByRel(unWantedRelationships);
			approvedMappings=obtainMappingByRel(wantedRelationships);
			removedMappings=obtainMappingByRel(unWantedRelationships);
			
		return unWantedRelationships;
		}
	}
	
	public ArrayList<Relationship> goRevisionByAutoByReasoner(ArrayList<String> referenceMappings)
	{
		int interation=1;
//		ArrayList<Relationship> wantedRelationships= new ArrayList<Relationship>();
//		ArrayList<Relationship> unWantedRelationships= new ArrayList<Relationship>();
		System.out.println("Begin simulation mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			Relationship _rel = null;
			//需要对图所有的匹配弧进行移除			
			//while(!MIPPs.isEmpty())
			while(Mappings.size()!=0)
			{
				System.out.println("===================================="); 
				System.out.println("The current iteration is "+interation++); 
				//影响函数可以考虑进一步细化
				//_rel=getMaxRel(relMappingMIPSs).get(0);
				//_rel=getMinWeight(relMappingMIPSs).get(0);
				//_rel=getMinWeight();
				
				_rel=getCandidateMappingByStructure();  //AAAI08Meilicke等人的方法
				
				//_rel=getCandidateMappingArcByGuaranteed(); //JWS2012 简化版方法
				
				//_rel=getCandidateMappingArcByNormed(); //JWS2012 强化版本
				
				//_rel=getCandidateMappingArcByImpactor(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight1(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight2(); //本文提出的方法
			
//				String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY)+
//						","+_rel.getProperty(COMEFROMPROPERTY).toString()+","+_rel.getProperty(WEIGHTEDPROPERTY).toString();
				String mapping=relToMap.get(_rel);
				System.out.println("The current mapping arc is "+mapping);

				String correspondingMapping=relToMap.get(_rel);	
				String parts[]=correspondingMapping.split(",");
				String originalMapping=parts[0]+","+parts[1]+",=,1.0";
				System.out.println("Make a judgement by Oracal");  					
				if(referenceMappings.contains(originalMapping)&&!isUserFailing())
		        { 	   		        	
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            wantedRelationships.add(_rel);	
		            //添加就是最初构建图数据库的方式,但是比较保守，会考虑到包含关系
		            simpleApproved(_rel,wantedRelationships,unWantedRelationships);
		            //更新图，
		            approvedNum++;
		        }
		        else
		        {
		        	System.out.println("Execute the action according to the rejected mapping.");
		        	unWantedRelationships.add(_rel);
		        	//移除的方式就是图数据库的删除方式,但是比较粗暴，会考虑到包含关系
		        	simpleReject2(_rel,wantedRelationships,unWantedRelationships);
		        	//entailedReject(_rel,wantedRelationships,unWantedRelationships);
					rejectNum++;
		        }
			}	
			System.out.println("The number of approved operation is "+approvedNum);
			System.out.println("The number of rejected operation is "+rejectNum);
			//removedMappings=obtainMappingByRel(unWantedRelationships);
			approvedMappings=obtainMappingByRel(wantedRelationships);
			removedMappings=obtainMappingByRel(unWantedRelationships);
			
		return unWantedRelationships;
		}
	}
	
	public ArrayList<Relationship> goRevisionByInteractiveByEnhancedReasoner()
	{
		int interation=1;
		//int totalNum=Mappings.size();
		System.out.println("Begin interactive mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			Relationship _rel = null;
			//需要对图所有的匹配弧进行移除
			//ArrayList<Relationship> mappingSet=new ArrayList<Relationship>(relMappingMIPSs.keySet());
			clearMappings4Graph(mappingSets, graphDb); //这里可能还要考虑mappingIndex索引的问题
		
			//while(!MIPPs.isEmpty())
			while(Mappings.size()!=0)	
			{ 
				System.out.println("The current iteration is "+interation++);
				Scanner sc = new Scanner(System.in);  
				//影响函数可以考虑进一步细化
				//_rel=getMaxRel(relMappingMIPSs).get(0);
				//_rel=getMinWeight(relMappingMIPSs).get(0);
				//_rel=getMinWeight();
				
				//_rel=getCandidateMappingByStructure();  //AAAI08Meilicke等人的方法
				
				_rel=getCandidateMappingArcByGuaranteed(); //JWS2012 简化版方法
				
				_rel=getCandidateMappingArcByNormed(); //JWS2012 强化版本
				
				_rel=getCandidateMappingArcByImpactor(); //本文提出的方法
				
				_rel=getCandidateMappingArcByImpactorandWeight1(); //本文提出的方法
				
				_rel=getCandidateMappingArcByImpactorandWeight2(); //本文提出的方法
				
//				System.out.println("The number of relInMap: "+relMappingMIPSs.keySet().size());
//				for(Relationship r:relMappingMIPSs.keySet())
//				{
//					System.out.println(r.getStartNode().getProperty(NAMEPROPERTY)+","+r.getEndNode().getProperty(NAMEPROPERTY));					
//				}				
				//String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY);
				String mapping=relToMap.get(_rel);
				System.out.println("The removed mapping is "+mapping);
				//System.out.println("The relation expressed in the graph is " + _rel.toString());
		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equals("Y")||sc.nextLine().equals("y"))
		        { 	   		        	
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            //wantedRelationships.add(_rel);	
		            //添加就是最初构建图数据库的方式,但是比较保守，会考虑到包含关系
		            //InferencedGraph1By1(_rel);
		            //simpleApproved(_rel,wantedRelationships,unWantedRelationships);
		            entailedApproved(_rel,wantedRelationships,unWantedRelationships);
		            //更新图，
		            approvedNum++;
		        }
		        else
		        {
		        	System.out.println("Execute the action according to the rejected mapping.");
		        	//unWantedRelationships.add(_rel);
		        	//移除的方式就是图数据库的删除方式,但是比较粗暴，会考虑到包含关系
		        	//simpleReject2(_rel,wantedRelationships,unWantedRelationships);
		        	entailedReject(_rel,wantedRelationships,unWantedRelationships);
					rejectNum++;
		        }
			}	
			System.out.println("The number of approved operation is "+approvedNum);
			System.out.println("The number of rejected operation is "+rejectNum);
			//double saveRatio=(approvedNum+rejectNum)*1.0/totalNum;
			
			System.out.println("The proportion of save operation is "+rejectNum);
			//removedMappings=obtainMapping4Rel(unWantedRelationships);
			approvedMappings=obtainMappingByRel(wantedRelationships);
			removedMappings=obtainMappingByRel(unWantedRelationships);
			
		return unWantedRelationships;
		}
	}
	
	public ArrayList<Relationship> goRevisionByAutoByEnhancedReasoner(ArrayList<String> referenceMappings)
	{
		int interation=1;
		System.out.println("Begin interactive mapping revision");
		try(Transaction tx = graphDb.beginTx())
		{
			Relationship _rel = null;
			//需要对图所有的匹配弧进行移除
			//ArrayList<Relationship> mappingSet=new ArrayList<Relationship>(relMappingMIPSs.keySet());
			clearMappings4Graph(mappingSets, graphDb); //这里可能还要考虑mappingIndex索引的问题

			//while(!MIPPs.isEmpty())
			while(Mappings.size()!=0)	
			{
				System.out.println("The current iteration is "+interation++);
				//影响函数可以考虑进一步细化
				//_rel=getMaxRel(relMappingMIPSs).get(0);
				
				//_rel=getMinWeight(relMappingMIPSs).get(0);
				
				//_rel=getMinWeight();
				
				_rel=getCandidateMappingByStructure();  //AAAI08Meilicke等人的方法
				
				//_rel=getCandidateMappingArcByGuaranteed(); //JWS2012 简化版方法
				
				//_rel=getCandidateMappingArcByNormed(); //JWS2012 强化版本
				
				//_rel=getCandidateMappingArcByImpactor(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight1(); //本文提出的方法
				
				//_rel=getCandidateMappingArcByImpactorandWeight2(); //本文提出的方法
				
//				System.out.println("The number of relInMap: "+relMappingMIPSs.keySet().size());
//				for(Relationship r:relMappingMIPSs.keySet())
//				{
//					System.out.println(r.getStartNode().getProperty(NAMEPROPERTY)+","+r.getEndNode().getProperty(NAMEPROPERTY));
//					
//				}				
				//String mapping=_rel.getStartNode().getProperty(NAMEPROPERTY)+","+_rel.getEndNode().getProperty(NAMEPROPERTY);
				
				String mapping=relToMap.get(_rel);
				System.out.println("The current mapping is "+mapping);

				String correspondingMapping=relToMap.get(_rel);	
				String parts[]=correspondingMapping.split(",");
				String originalMapping=parts[0]+","+parts[1]+",=,1.0";
				System.out.println("Make a judgement by Oracal");  					
				if(referenceMappings.contains(originalMapping)&&!isUserFailing())
		        { 	   		        	
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            //wantedRelationships.add(_rel);	
		            //添加就是最初构建图数据库的方式,但是比较保守，会考虑到包含关系
		            //InferencedGraph1By1(wantedRelationships,unWantedRelationships, graphDb);
		            //simpleApproved(_rel,wantedRelationships,unWantedRelationships);
		            entailedApproved(_rel,wantedRelationships,unWantedRelationships);
		            //更新图，
		            approvedNum++;
		        }
		        else
		        {
		        	System.out.println("Execute the action according to the rejected mapping.");
		        	//unWantedRelationships.add(_rel);
		        	//移除的方式就是图数据库的删除方式,但是比较粗暴，会考虑到包含关系
		        	//simpleReject2(_rel,wantedRelationships,unWantedRelationships);
		        	entailedReject(_rel,wantedRelationships,unWantedRelationships);
					rejectNum++;
		        }
			}	
			System.out.println("The number of approved operation is "+approvedNum);
			System.out.println("The number of rejected operation is "+rejectNum);
			
			//removedMappings=obtainMapping4Rel(unWantedRelationships);
			approvedMappings=obtainMappingByRel(wantedRelationships);
			removedMappings=obtainMappingByRel(unWantedRelationships);
		return unWantedRelationships;
		}
	}
	

	
	private boolean isUserFailing(){
		
		Random generator= new Random();
		int error_user=0; //The potential ratio of judgement made by users
		int random_num = generator.nextInt(100);
		if (random_num < error_user){
			return true;
		}
		return false;
		
	}
	
	public void InferencedGraph1By1(Relationship rel)
	{
		HashSet<Relationship> extendRel=unifyMappingsApproved(rel);
		
		//extendRel.retainAll(relMappingMIPSs.keySet()); //跟MIPSs没什么关系
		
		wantedRelationships.addAll(extendRel); //add the approved relationships
		
		mappingSets.removeAll(extendRel);
		
		for(Relationship r:extendRel) //还需要考虑头尾问题
		{
			String start = r.getStartNode().getProperty(NAMEPROPERTY).toString();
			String end = r.getEndNode().getProperty(NAMEPROPERTY).toString();
			String source = r.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String target = r.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			for(Relationship candidate: mappingSets)
			{
				
				String cStart = candidate.getStartNode().getProperty(NAMEPROPERTY).toString();
				String cEnd = candidate.getEndNode().getProperty(NAMEPROPERTY).toString();
				String cSource = candidate.getStartNode().getProperty(COMEFROMPROPERTY).toString();
				String cTarget = candidate.getEndNode().getProperty(COMEFROMPROPERTY).toString();
//				String weight=candidate.getProperty(WEIGHTEDPROPERTY).toString();
//				//">"，"<","="
//				String candaidateMap1="";
//				String candaidateMap2="";
//				String candaidateMap3="";
				if((start.equals(cStart)&&source.equals(cSource))||(end.equals(cEnd)&&target.equals(cTarget)))
				{
					unWantedRelationships.add(candidate);
					String map=relToMap.get(candidate);
					Mappings.remove(map);
//					if(cSource.equals("First"))
//					{
//						candaidateMap1=cStart+","+cEnd+",>,"+weight;
//						candaidateMap2=cStart+","+cEnd+",<,"+weight;
//						candaidateMap3=cStart+","+cEnd+",=,"+weight;
//						Mappings.remove(candaidateMap1);
//						Mappings.remove(candaidateMap2);
//						Mappings.remove(candaidateMap3);
//					}
//					else 
//					{
//						candaidateMap1=cEnd+","+cStart+",>,"+weight;
//						candaidateMap2=cEnd+","+cStart+",<,"+weight;
//						candaidateMap3=cEnd+","+cStart+",=,"+weight;
//						Mappings.remove(candaidateMap1);
//						Mappings.remove(candaidateMap2);
//						Mappings.remove(candaidateMap3);
//					}								
				}
			}			
		}
		
	}
	
	public void simpleApproved(Relationship rel, ArrayList<Relationship> wantedRelationships, ArrayList<Relationship> unWantedRelationships)
	{
		//更新MIPPs relMappingMIPSs
//		HashSet<Relationship> extendRel=unifyMappings(rel);
//		HashSet<Relationship> unWantMapping=new HashSet<Relationship>(); //存储的目的是方便做MIPPs和relMappingMIPSs的更新
//		extendRel.retainAll(relMappingMIPSs.keySet());
		
		String mapping=relToMap.get(rel);
		HashSet<Relationship> extendRel=mapToRel.get(mapping);
		
		//extendRel.retainAll(relMappingMIPSs.keySet()); //移除relMappingMIPSs可能存在问题
		
		HashSet<Relationship> unWantMapping=new HashSet<Relationship>(); //存储的目的是方便做MIPPs和relMappingMIPSs的更新
				
		for(Relationship r:extendRel)
		{
			if(!relMappingMIPSs.keySet().contains(r))
				continue;
			List<MIPS> relatedMIPSs=relMappingMIPSs.get(r);
			for(MIPS mips:relatedMIPSs)
			{
				//Set<Relationship> incoherentMappings=mips.getincoherenceMappings(); //获取了最原始的匹配对
//				Set<Relationship> incoherentMappings=mips.unifyMappings(relationshipIndex); //获取了最原始的匹配对
//				if(existRejectedRelationships(rel,incoherentMappings,wantedRelationships)) //Does MIPP conflict mappings.			
//				{
//					incoherentMappings.remove(rel); 
//					for(Relationship removedMap:incoherentMappings)
//					{
//						HashSet<Relationship> extendRemovedMap=unifyMappings(removedMap);
//						String removedMapping=relToMap.get(removedMap);
//						Mappings.remove(removedMapping);
//						unWantedRelationships.addAll(extendRemovedMap);
//						unWantMapping.addAll(unWantedRelationships);
//					}
//				}
				Set<String> incoherentMappings=mips.unifyMappings(relToMap); //获取了最原始的匹配对
				String map=relToMap.get(rel);
				if(existRejectedMappings(map,incoherentMappings,wantedRelationships)) //通过匹配来快速计算
				{
					incoherentMappings.remove(map); 
					for(String removedMap:incoherentMappings)
					{
						//HashSet<Relationship> extendRemovedMap=unifyMappings(removedMap);
						HashSet<Relationship> extendRemovedRelationships=mapToRel.get(removedMap);
						Mappings.remove(removedMap);
						//unWantedRelationships.addAll(extendRemovedRelationships);
						for(Relationship er: extendRemovedRelationships)
						{
							if(!unWantedRelationships.contains(er))
								unWantedRelationships.add(er);
						}
						unWantMapping.addAll(unWantedRelationships);
					}
				}
				
			}
		}
		
		//wantedRelationships.add(rel); //add the approved mapping
		//更新图			
		for(Relationship unRel:unWantMapping)
		{
			if(!relMappingMIPSs.keySet().contains(unRel))
				continue;
			List<MIPS> removedMIPSs=relMappingMIPSs.get(unRel);
			MIPPs.removeAll(removedMIPSs); 
			java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
			while(iter.hasNext())
			{
				Relationship r=iter.next().getKey();					
				for(int i=0;i<relMappingMIPSs.get(r).size();i++)
				{
					MIPS mips=relMappingMIPSs.get(r).get(i);
					//mips.printMIPS();
					//if(mips.getincoherenceMappings().contains(unRel))
					//if(mips.unifyMappings(relationshipIndex).contains(unRel))  //因为unRels是归一化之后的结果，针对属性而言
					if(mips.unifyRels(relToMap,mapToRel).contains(unRel))  //因为unRels是归一化之后的结果，针对属性而言
					{
						relMappingMIPSs.get(r).remove(mips);
						i--;
					}
					
				}
				if(relMappingMIPSs.get(r).isEmpty())
					iter.remove();
			}				
		}	
		//考虑移除相应的其他同步的边或者属性。
		
	}
	
	public void entailedApproved(Relationship rel, ArrayList<Relationship> wantedRelationships, ArrayList<Relationship> unWantedRelationships)
	{
		//更新MIPPs relMappingMIPSs
		wantedRelationships.add(rel);
		//HashSet<Relationship> extendRel=unifyMappings(rel);
		
		String mapping=relToMap.get(rel);
		HashSet<Relationship> extendRel=mapToRel.get(mapping);
		//extendRel.retainAll(relMappingMIPSs.keySet()); //移除relMappingMIPSs可能存在问题	
			
		HashSet<Relationship> unWantMapping=new HashSet<Relationship>(); //存储的目的是方便做MIPPs和relMappingMIPSs的更新
		for(Relationship r:extendRel)
		{
			if(!relMappingMIPSs.containsKey(r))
				continue;
			List<MIPS> relatedMIPSs=relMappingMIPSs.get(r);
			for(MIPS mips:relatedMIPSs)
			{
				//Set<Relationship> incoherentMappings=mips.getincoherenceMappings(); //获取了最原始的匹配对
				//Set<Relationship> incoherentMappings=mips.unifyMappings(relationshipIndex); //获取了最原始的匹配对
				Set<String> incoherentMappings=mips.unifyMappings(relToMap); //获取了最原始的匹配对
				String map=relToMap.get(rel);
				if(existRejectedMappings(map,incoherentMappings,wantedRelationships)) //Does MIPP conflict mappings.
				{
					incoherentMappings.remove(map); 
					for(String removedMap:incoherentMappings)
					{
						//HashSet<Relationship> extendRemovedMap=unifyMappings(removedMap);
						HashSet<Relationship> extendRemovedRelationships=mapToRel.get(removedMap);
						Mappings.remove(removedMap);
						//unWantedRelationships.addAll(extendRemovedRelationships);
						for(Relationship er: extendRemovedRelationships)
						{
							if(!unWantedRelationships.contains(er))
								unWantedRelationships.add(er);
						}
						unWantMapping.addAll(unWantedRelationships);
					}
				}
			}
		}
		
		for(Relationship unRel:unWantMapping)
		{
			if(!relMappingMIPSs.keySet().contains(unRel))
				continue;
			List<MIPS> removedMIPSs=relMappingMIPSs.get(unRel);
			MIPPs.removeAll(removedMIPSs); 
			java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
			while(iter.hasNext())
			{
				Relationship r=iter.next().getKey();					
				for(int i=0;i<relMappingMIPSs.get(r).size();i++)
				{
					MIPS mips=relMappingMIPSs.get(r).get(i);
					//mips.printMIPS();
					//if(mips.getincoherenceMappings().contains(unRel))
					//if(mips.unifyMappings(relationshipIndex).contains(unRel))  //因为unRels是归一化之后的结果，针对属性而言
					//if(mips.unifyMappings(relToMap,mapToRel).contains(unRel))
					if(mips.unifyRels(relToMap,mapToRel).contains(unRel))
					{
						relMappingMIPSs.get(r).remove(mips);
						i--;
					}
					
				}
				if(relMappingMIPSs.get(r).isEmpty())
					iter.remove();
			}				
		}	
		
//		printStateOfGraph(); 打印状态
		
//		wantedRelationships.add(rel); //add the approved mapping
//		String start=rel.getStartNode().getProperty(NAMEPROPERTY).toString();
//		String end=rel.getEndNode().getProperty(NAMEPROPERTY).toString();
//		System.out.println(start +"->"+end);
		
		//更新图
		for(Relationship re:extendRel)
		{
			//refresh edge
			re.getStartNode().createRelationshipTo(re.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
//			System.out.println(re);
//			re=re.getStartNode().createRelationshipTo(re.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
//			System.out.println(re); //赋值之后边的数量会增加
			//System.out.println(re.getStartNode().getProperty(NAMEPROPERTY) +"->"+re.getEndNode().getProperty(NAMEPROPERTY));
		}

		
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(wantedRelationships);
		tempSet.removeAll(unWantedRelationships);
		
		
		for(Relationship r:tempSet)
		{
			if(r==null)
				continue;
            String startNode=r.getStartNode().getProperty(NAMEPROPERTY).toString();
			String endNode=r.getEndNode().getProperty(NAMEPROPERTY).toString();
			String nodeSource = r.getStartNode().getProperty(COMEFROMPROPERTY).toString();
		    String nodeTarget = r.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			Map<String, Object> params = new HashMap<>();	
		    params.put( "a", startNode );	
		    params.put( "b", endNode );
		    params.put( "c", nodeSource );	
		    params.put( "d", nodeTarget );
		    StringBuilder query = new StringBuilder();	    
		    query.append("MATCH pp=(x)-[r1:INCLUDEDBY*]->(y) ");
		    //query.append("WHERE x.Name=$a and y.Name=$b ");
		    query.append("WHERE x.Name=$a and y.Name=$b and x.ComeFrom=$c and y.ComeFrom=$d ");
		    query.append("RETURN pp");
		    //System.out.println(query);
		    Result result = graphDb.execute(query.toString(),params);		
			if(result.hasNext()==true) //如果有路径
			{
				wantedRelationships.add(r);
				//System.out.println(r.getStartNode().getProperty(NAMEPROPERTY) +"->"+r.getEndNode().getProperty(NAMEPROPERTY) +" needs to be added!");
				//refresh edge
				r.getStartNode().createRelationshipTo(r.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
				String tempMapping=relToMap.get(r);
				if(mapToRel.get(tempMapping).size()==1)
					Mappings.remove(tempMapping);			
			}
			else
			{
				//System.out.println(result.toString());
			}			
		}	
	}
	
	public void simpleReject(Relationship rel, ArrayList<Relationship> wantedRelationships, ArrayList<Relationship> unWantedRelationships)
	{
		//更新MIPPs relMappingMIPSs
		//HashSet<Relationship> extendRel=unifyRels(rel);
		
		String mapping=relToMap.get(rel);
		HashSet<Relationship> extendRel=mapToRel.get(mapping);
		
		extendRel.retainAll(relMappingMIPSs.keySet());
		for(Relationship unRel:extendRel)
		{
//			if(!relMappingMIPSs.keySet().contains(unRel))
//				continue;
			List<MIPS> removedMIPSs=relMappingMIPSs.get(unRel);
			MIPPs.removeAll(removedMIPSs); 
			java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
			while(iter.hasNext())
			{
				Relationship r=iter.next().getKey();					
				for(int i=0;i<relMappingMIPSs.get(r).size();i++)
				{
					MIPS mips=relMappingMIPSs.get(r).get(i);
					mips.printMIPS();
				    //if(mips.getincoherenceMappings().contains(unRel))
					//if(mips.unifyMappings(relationshipIndex).contains(unRel)) //因为unRels是归一化之后的结果，针对属性而言
					if(mips.unifyRels(relToMap,mapToRel).contains(unRel)) //因为unRels是归一化之后的结果，针对属性而言
					{
						relMappingMIPSs.get(r).remove(mips);
						i--;
					}
				}
				if(relMappingMIPSs.get(r).isEmpty())
					iter.remove();
			}	
		}
		
	}
	
	public void simpleReject2(Relationship rel, ArrayList<Relationship> wantedRelationships, ArrayList<Relationship> unWantedRelationships)
	{
		//更新MIPPs relMappingMIPSs
		
		String mapping=relToMap.get(rel);
		HashSet<Relationship> extendRel=mapToRel.get(mapping);
		
		//extendRel.retainAll(relMappingMIPSs.keySet()); //移除relMappingMIPSs可能存在问题	
				
		for(Relationship unRel:extendRel)
		{
			if(!relMappingMIPSs.keySet().contains(unRel))
				continue;
			List<MIPS> removedMIPSs=relMappingMIPSs.get(unRel);
			MIPPs.removeAll(removedMIPSs); 
			java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
			while(iter.hasNext())
			{
				Relationship r=iter.next().getKey();					
				for(int i=0;i<relMappingMIPSs.get(r).size();i++)
				{
					MIPS mips=relMappingMIPSs.get(r).get(i);
					//mips.printMIPS();
				    //if(mips.getincoherenceMappings().contains(unRel))
					//if(mips.unifyMappings(relationshipIndex).contains(unRel)) //因为unRels是归一化之后的结果，针对属性而言
					if(mips.unifyRels(relToMap,mapToRel).contains(unRel)) //因为unRels是归一化之后的结果，针对属性而言
					{
						relMappingMIPSs.get(r).remove(mips);
						i--;
					}
				}
				if(relMappingMIPSs.get(r).isEmpty())
					iter.remove();
			}	
		}	
	}
	
	public void entailedReject(Relationship rel, ArrayList<Relationship> wantedRelationships, ArrayList<Relationship> unWantedRelationships)
	{
		//更新MIPPs relMappingMIPSs
		String mapping=relToMap.get(rel);
		HashSet<Relationship> extendRel=mapToRel.get(mapping);
		
		//extendRel.retainAll(relMappingMIPSs.keySet());
		
		for(Relationship unRel:extendRel)
		{
			if(!relMappingMIPSs.containsKey(unRel))
				continue;
			List<MIPS> removedMIPSs=relMappingMIPSs.get(unRel);
			MIPPs.removeAll(removedMIPSs); 
			java.util.Iterator<Entry<Relationship, List<MIPS>>> iter= relMappingMIPSs.entrySet().iterator();
			while(iter.hasNext())
			{
				Relationship r=iter.next().getKey();					
				for(int i=0;i<relMappingMIPSs.get(r).size();i++)
				{
					MIPS mips=relMappingMIPSs.get(r).get(i);
					//mips.printMIPS();
					//if(mips.unifyMappings(relToMap,mapToRel).contains(unRel)) //因为unRels是归一化之后的结果，针对属性而言
					if(mips.unifyRels(relToMap,mapToRel).contains(unRel)) //因为unRels是归一化之后的结果，针对属性而言	
					{
						relMappingMIPSs.get(r).remove(mips);
						i--;
					}
				}
				if(relMappingMIPSs.get(r).isEmpty())
					iter.remove();
			}	
		}
		unWantedRelationships.addAll(extendRel);
		
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(unWantedRelationships);
		
		for(Relationship unRel:extendRel)
		{
			if(unRel==null)
				continue;
			String startNode= unRel.getStartNode().getProperty(NAMEPROPERTY).toString();
			String endNode=unRel.getEndNode().getProperty(NAMEPROPERTY).toString();
			///一定要严谨，Reviewer->Meta-Reviewer让人蛋疼
			String nodeSource = unRel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String nodeTarget = unRel.getEndNode().getProperty(COMEFROMPROPERTY).toString();

			//System.out.println(startNode+"->"+endNode);	
			for(Relationship re:tempSet)
			{
				//String mappings=relToMap.get(re);	
//				Relationship re0 = re.getStartNode().createRelationshipTo(re.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
//				System.out.println(re0.getStartNode().getProperty(NAMEPROPERTY) +"->"+re0.getEndNode().getProperty(NAMEPROPERTY));
				
				re.getStartNode().createRelationshipTo(re.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
				//System.out.println(re.getStartNode().getProperty(NAMEPROPERTY) +"->"+re.getEndNode().getProperty(NAMEPROPERTY));
				
				Map<String, Object> params = new HashMap<>();
			    params.put( "a", startNode );	
			    params.put( "b", endNode );
			    params.put( "c", nodeSource );	
			    params.put( "d", nodeTarget );
			    StringBuilder query = new StringBuilder();	    
			    query.append("MATCH pp=(x)-[r1:INCLUDEDBY*]->(y) ");
			    //query.append("WHERE x.Name=$a and y.Name=$b");
			    query.append("WHERE x.Name=$a and y.Name=$b and x.ComeFrom=$c and y.ComeFrom=$d ");
			    query.append("RETURN pp");
			    //System.out.println(query);
			    Result result = graphDb.execute(query.toString(),params);
//			    while (result.hasNext()) 
//			    {
//			    	Map<String, Object> temp = result.next();		    	
//			    	Path pPath = (Path) temp.get("pp");
//			    		printAllPaths(pPath);	
//			    	System.out.println("===============================");
//			    } 			
				if(result.hasNext()==true) //如果有路径
				{
					//新加入的关系编码跟之前有差异
					unWantedRelationships.add(re);
					String mappings=relToMap.get(re);
					Mappings.remove(mappings);
					
					//System.out.println(re0.getStartNode().getProperty(NAMEPROPERTY) +"->"+re0.getEndNode().getProperty(NAMEPROPERTY) +" needs to be deleted!");
					//System.out.println(re.getStartNode().getProperty(NAMEPROPERTY) +"->"+re.getEndNode().getProperty(NAMEPROPERTY) +" needs to be deleted!");
				}
				else
				{
					//System.out.println(result.toString());
				}
				//re0.delete();
				
				String start = re.getStartNode().getProperty(NAMEPROPERTY).toString();
				String end = re.getEndNode().getProperty(NAMEPROPERTY).toString();
				String source = re.getStartNode().getProperty(COMEFROMPROPERTY).toString();
				String target = re.getEndNode().getProperty(COMEFROMPROPERTY).toString();

				StringBuilder query1 = new StringBuilder();
				String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
				query1.append("MATCH (n)-[r]->(m) ");
				query1.append(String.format(formatter, start, end, source, target));
				query1.append("DELETE r");
				graphDb.execute(query1.toString());	
							
			}
			
		}
		
		
//		String mapping=relToMap.get(extendRel);
//		HashMap<Relationship, String> relToMap=new HashMap<Relationship, String>();
//		HashMap<String, HashSet<Relationship>> mapToRel=new HashMap<String, HashSet<Relationship>>();
//		
//		
//		基于拒绝关系的进一步推理
//		
//		1. 基于已有的mappings测试两个节点，
//		如果新的边加入导致，这个两个点的关系被推导出来，
//		那么该节点也需要被拒绝
		
	}
	
	
	
		
	public HashSet<Relationship> unifyRels(Relationship r)
	{
		HashSet<Relationship> unifiedMapping=new HashSet<Relationship>();
		if(r.getProperty(TYPEPROPERTY).equals("Role"))			//只单独对纯角色的等价进行了限制
		{
				Relationship stemRelationship=null;
				String comefrom=r.getStartNode().getProperty(COMEFROMPROPERTY).toString();
				String subKey=r.getStartNode().getProperty(NAMEPROPERTY).toString().replace("existence_", "").replace("inverse_", "");	
				String supKey=r.getEndNode().getProperty(NAMEPROPERTY).toString().replace("existence_", "").replace("inverse_", "");
				if(subKey.equals(supKey)&&comefrom.equals(COMEFROMFIRST))
				{
					String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
					stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
				else if((subKey.equals(supKey)&&comefrom.equals(COMEFROMSECOND)))
				{
					String relationshipName = Tools4Graph.getRelationshipName(subKey+"_2", supKey+"_1");		//***
					stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
				else 
				{
					String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
					stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
				unifiedMapping.add(stemRelationship);		
		}
		else
		{
			unifiedMapping.add(r);
		}
	    return unifiedMapping;
	} 
	
	
	
	public HashSet<Relationship> unifyMappingsApproved(Relationship r)
	{
		//1.改良扩充，包括拒绝方式也可以这样操作。
		String map=relToMap.get(r);
		HashSet<Relationship> rels=mapToRel.get(map);
		return rels;
	} 
	
	public boolean existRejectedRelationships(Relationship rel, Set<Relationship> mappings,ArrayList<Relationship> wantedRelationships)
	{
		if(!mappings.contains(rel))
			return false;
		//mappings.retainAll(wantedRelationships);
		mappings.removeAll(wantedRelationships);
		if(mappings.size()==1) //rel is approved mappings, then other one is wrong
			return true;
		else
			return false;
			
	}
	
	public boolean existRejectedMappings(String rel, Set<String> mappings,ArrayList<Relationship> wantedRelationships)
	{
		if(!mappings.contains(rel))
			return false;
		//mappings.retainAll(wantedRelationships);
		ArrayList<String> tempSet=new ArrayList<String>();
		for(Relationship re: wantedRelationships)
		{
			tempSet.add(relToMap.get(re));
		}
		mappings.removeAll(tempSet);
		if(mappings.size()==1) //rel is approved mappings, then other one is wrong
			return true;
		else
			return false;
			
	}
	
	public boolean existRejectedMappings2(String rel, Set<String> mappings,ArrayList<Relationship> wantedRelationships)
	{
		if(!mappings.contains(rel))
			return false;
		//mappings.retainAll(wantedRelationships);
		ArrayList<String> tempSet=new ArrayList<String>();
		for(Relationship re: wantedRelationships)
		{
			tempSet.add(relToMap.get(re));
		}
		mappings.removeAll(tempSet);
		mappings.remove(rel); //this function needs to assume this mapping is removed
		if(mappings.size()==1) //rel is approved mappings, then other one is wrong
			return true;
		else
			return false;
			
	}
	
	
	
	public ArrayList<Relationship> getMaxRel(Map<Relationship, List<MIPS>> relMappingMIPSs) //计算每个mapping最小权重
	{
		int max = 0;
		//Relationship rel = null;
		ArrayList<Relationship> maximumRelationship= new ArrayList<Relationship>();		
		//第一个循序找到关联最大的值是多少
		for(Relationship r : relMappingMIPSs.keySet())
		{				
			if(relMappingMIPSs.get(r).size()>max)
			{
				max = relMappingMIPSs.get(r).size();
				//rel = r;
			}			
		}
		//第二轮循环才能找到对应的Relationship的集合
		for(Relationship r : relMappingMIPSs.keySet())
		{				
			if(relMappingMIPSs.get(r).size()==max)
			{
				maximumRelationship.add(r);
			}			
		}
		return maximumRelationship;	
	}
	
	
	public ArrayList<Relationship> getMinWeight(ArrayList<Relationship> candidateMappings) //计算每个mapping最小权重
	{
		double minWeight = 1;
		//Relationship rel = null;
		ArrayList<Relationship> minimumRelationship= new ArrayList<>();	
		HashMap<Double,ArrayList<Relationship>> map=new HashMap<Double,ArrayList<Relationship>>();	
		//第一个循序找到关联最大的值是多少
		for(Relationship r : candidateMappings)
		{	
			//String mapping=r.getStartNode().getProperty(NAMEPROPERTY)+","+r.getEndNode().getProperty(NAMEPROPERTY);
			double weight=Double.parseDouble(r.getProperty(WEIGHTEDPROPERTY).toString());
			ArrayList<Relationship> _rRelationship= new ArrayList<>();	
			if(minWeight>weight)
				minWeight=weight;
			if(map.get(weight)==null)  //选取权重最小的值进行移除
			{
				_rRelationship.add(r);
				map.put(weight, _rRelationship);
			}
			else  //闭包个数为num不在存在mapping中
			{
				map.get(weight).add(r);			
			}
		}
		minimumRelationship=map.get(minWeight);
		return minimumRelationship;	
	}
	
	public List<Relationship> getMinWeight(Map<Relationship, List<MIPS>> relMappingMIPSs) //计算每个mapping最小权重
	{
		double minWeight = 1;
		//Relationship rel = null;
		List<Relationship> minimumRelationship= new ArrayList<>();	
		HashMap<Double,List<Relationship>> map=new HashMap<Double,List<Relationship>>();	
		//第一个循序找到关联最大的值是多少
		for(Relationship r : relMappingMIPSs.keySet())
		{	
			//String mapping=r.getStartNode().getProperty(NAMEPROPERTY)+","+r.getEndNode().getProperty(NAMEPROPERTY);
			//double weight=Double.parseDouble(r.getProperty(WEIGHTEDPROPERTY).toString());
			double weight=relToWeight.get(r);
			List<Relationship> _rRelationship= new ArrayList<>();	
			if(minWeight>weight)
				minWeight=weight;
			if(map.get(weight)==null)  //选取权重最小的值进行移除
			{
				_rRelationship.add(r);
				map.put(weight, _rRelationship);
			}
			else  //闭包个数为num不在存在mapping中
			{
				map.get(weight).add(r);			
			}
		}
		minimumRelationship=map.get(minWeight);
		return minimumRelationship;	
	}
	
	public Relationship getMinWeight() //计算每个mapping最小权重
	{
		double minWeight = 1;
//		String s1="Meta-Review***Review";
//		//relationshipIndex.get(NAMEPROPERTY, s1).getSingle();
		
		//List<Relationship> minimumRels= new ArrayList<Relationship>();	
		HashMap<Double,List<String>> Weightmap=new HashMap<Double,List<String>>();	
		//第一个循序找到关联最大的值是多少
		for(String map : Mappings)
		{	
			String parts[]=map.split(",");
			double weight=Double.parseDouble(parts[3]);
			//double weight=relToWeight.get(r);
			List<String> _mapping= new ArrayList<>();	
			if(minWeight>weight)
				minWeight=weight;
			if(Weightmap.get(weight)==null)  //选取权重最小的值进行移除
			{
				_mapping.add(map);
				Weightmap.put(weight, _mapping);
			}
			else  //闭包个数为num不在存在mapping中
			{
				Weightmap.get(weight).add(map);			
			}
		}
		
		Relationship rel = null;
		String s="";
		
		//if(Weightmap.get(minWeight).size()!=0)
		if(!Weightmap.keySet().isEmpty()&&Weightmap.get(minWeight).size()!=0)
		{
			s=Weightmap.get(minWeight).get(0);		
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
					
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				//System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}
		
		return rel;	
	}
	
	public Relationship getCandidateMappingByStructure() //AAAI08Meilicke等人的方法
	{
		int maxImpact=0;
	
		HashMap<Integer,ArrayList<String>> ImpactorMapping=new HashMap<Integer,ArrayList<String>>();
		for(String map : Mappings)
		{
			int impactor=0;
			String parts[]=map.split(",");
			String c1=parts[0];
			String c2=parts[1];
			if(parts[2].equals("<")) //parts[1]包含于part[0]
			{
				int a=0,b=0,c=0;
				Set<String> subClass1 = subAxioms.get(c1);
				if(subClass1!=null)
				{
					a=subClass1.size();
				}
				Set<String> superClass2 = supAxioms.get(c2);
				if(superClass2!=null)
				{
					b=superClass2.size();
				}			
				Set<String> disClass2 = disAxioms.get(c2);	
				if(disClass2!=null)
				{
					c=disClass2.size();
				}					
				//impactor=subClass1.size()*(superClass2.size()+disClass2.size());
				impactor=a*(b+c);
				
			}
			else if(parts[2].equals(">")) //parts[0]包含于part[1]
			{
				int a=0,b=0,c=0;
				Set<String> superClass1 = supAxioms.get(c1);
				if(superClass1!=null)
				{
					a=superClass1.size();
				}
				Set<String> subClass2 = subAxioms.get(c2);
				if(subClass2!=null)
				{
					b=subClass2.size();
				}
				Set<String> disClass2 = disAxioms.get(c2);		
				if(disClass2!=null)
				{
					c=disClass2.size();
				}
				//impactor=superClass1.size()*(subClass2.size()+disClass2.size());
				impactor=a*(b+c);
			}	
			else
			{
				int aa=0,bb=0,cc=0;
				Set<String> subClass1 = subAxioms.get(c1);
				if(subClass1!=null)
				{
					aa=subClass1.size();
				}
				Set<String> superClass2 = supAxioms.get(c2);
				if(superClass2!=null)
				{
					bb=superClass2.size();
				}
				Set<String> disClass21 = disAxioms.get(c2);	
				if(disClass21!=null)
				{
					cc=disClass21.size();
				}
				//int impactor1=subClass1.size()*(superClass2.size()+disClass21.size());
				int impactor1=aa*(bb+cc);
				
				int aaa=0,bbb=0,ccc=0;
				Set<String> superClass1 = supAxioms.get(c1);
				if(superClass1!=null)
				{
					aaa=superClass1.size();
				}
				Set<String> subClass2 = subAxioms.get(c2);
				if(subClass2!=null)
				{
					bbb=subClass2.size();
				}
				Set<String> disClass22 = disAxioms.get(c2);	
				if(disClass22!=null)
				{
					ccc=disClass22.size();
				}
				//int impactor2 =superClass1.size()*(subClass2.size()+disClass22.size());		
				int impactor2 =aaa*(bbb+ccc);	
				impactor=impactor1+impactor2;			
			}	
			if(impactor>maxImpact)
				maxImpact=impactor;
			
			if(ImpactorMapping.containsKey(impactor))
			{
				ImpactorMapping.get(impactor).add(map);
			}
			else
			{
				ArrayList<String> tempSet= new ArrayList<String>();
				tempSet.add(map);
				ImpactorMapping.put(impactor, tempSet);
			}
			
		}
		
		Relationship rel = null;
		String s="";
		//if(ImpactorMapping.get(maxImpact).size()!=0)
		if(!ImpactorMapping.keySet().isEmpty()&&ImpactorMapping.get(maxImpact).size()!=0)	
		{
			s=ImpactorMapping.get(maxImpact).get(0);		
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
					
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}
		
		return rel;	 
	}
	
	public Relationship getCandidateMappingArcByGuaranteed() //JWS2012简化版方法
	{
		int minGuaranteed=100;
		int impactPlus=0,impactConflict=0,impactMinor=0; 
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(wantedRelationships);
		tempSet.removeAll(unWantedRelationships);
		HashMap<Integer,ArrayList<String>> impactMappings=new HashMap<Integer,ArrayList<String>>();	
		for(String map : Mappings)
		{
			HashSet<Relationship> relSet=mapToRel.get(map);			
			impactPlus=obtainImpactPlus(relSet,tempSet)-1; //JWS2012中不能包含自己
			impactConflict=	obtainImpactConflict(relSet);
			impactMinor=obtainImpactMinor(relSet,tempSet)-1; //JWS2012中不能包含自己
			int guaranteed=Math.min(impactPlus+impactConflict, impactMinor); //简化版本的计算模式
			if(minGuaranteed>guaranteed)
				minGuaranteed=guaranteed;
			if(impactMappings.keySet().contains(guaranteed))
				impactMappings.get(guaranteed).add(map);
			else
			{
				ArrayList<String> tempMappingSet=new ArrayList<String>();
				tempMappingSet.add(map);
				impactMappings.put(guaranteed, tempMappingSet);
			}		
		}
		
		Relationship rel = null;
		String s="";
		//if(impactMappings.get(minGuaranteed).size()!=0)
		if(!impactMappings.keySet().isEmpty()&&impactMappings.get(minGuaranteed).size()!=0)	
		{
			s=impactMappings.get(minGuaranteed).get(0);		
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}		
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				//System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}		
		return rel;	 
	}
	
	public Relationship getCandidateMappingArcByNormed() //JWS2012简化版方法
	{
		double validityRatio=0.75; //通常可以根据MIPS来计算
		double maxImpact=-100.0;
		int impactPlus=0,impactConflict=0,impactMinor=0; 
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(wantedRelationships);
		tempSet.removeAll(unWantedRelationships);
		HashMap<Double,ArrayList<String>> impactMappings=new HashMap<Double,ArrayList<String>>();	
		for(String map : Mappings)
		{
			HashSet<Relationship> relSet=mapToRel.get(map);			
			impactPlus=obtainImpactPlus(relSet,tempSet);
			impactConflict=	obtainImpactConflict(relSet);
			impactMinor=obtainImpactMinor(relSet,tempSet);
			//int impact=Math.min(impactPlus+impactConflict, impactMinor);
			
			double impactNormPlus=(impactPlus)*1.0/Mappings.size();
			double impactNormConflict=(impactConflict)*1.0/Mappings.size();
			double impactNormMinor=(impactMinor)*1.0/Mappings.size();
			
			double NormPlus=-Math.abs(validityRatio-impactNormPlus);
			double NormConflict=-Math.abs(1-validityRatio-impactNormConflict);
			double NormMinor=-Math.abs(1-validityRatio-impactNormMinor);
			
			double impact=Math.max(Math.max(NormPlus,NormConflict),NormMinor);
			
			
			if(maxImpact<impact)
				maxImpact=impact;
			if(impactMappings.keySet().contains(impact))
				impactMappings.get(impact).add(map);
			else
			{
				ArrayList<String> tempMappingSet=new ArrayList<String>();
				tempMappingSet.add(map);
				impactMappings.put(impact, tempMappingSet);
			}		
		}
		
		Relationship rel = null;
		String s="";
		//if(impactMappings.get(maxImpact).size()!=0)
		if(!impactMappings.keySet().isEmpty()&&impactMappings.get(maxImpact).size()!=0)	
		{
			s=impactMappings.get(maxImpact).get(0);		
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
					
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				//System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}		
		return rel;	 
	}
	
	public Relationship getCandidateMappingArcByImpactor()
	{
		int maxImpact=0;
		int impactPlus=0,impactConflict=0,impactMinor=0; 
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(wantedRelationships);
		tempSet.removeAll(unWantedRelationships);
		HashMap<Integer,ArrayList<String>> impactMappings=new HashMap<Integer,ArrayList<String>>();
		System.out.println("==========The process of evaluation by impact factor==========");
		for(String map : Mappings)
		{
			
			//System.out.println("The mapping is "+ map);
			HashSet<Relationship> relSet=mapToRel.get(map);			
			impactPlus=obtainImpactPlus(relSet,tempSet);
			//System.out.println("The ImpactPlus of "+map +" is finished");
			
			impactConflict=	obtainImpactConflict(relSet);
			//System.out.println("The ImpactConflict of "+map +" is finished");
			
			impactMinor=obtainImpactMinor(relSet,tempSet);
			//System.out.println("The ImpactMinor of "+map +" is finished");
			
			int impact=Math.max(impactPlus+impactConflict, impactMinor);
			if(maxImpact<impact)
				maxImpact=impact;
			if(impactMappings.keySet().contains(impact))
				impactMappings.get(impact).add(map);
			else
			{
				ArrayList<String> tempMappingSet=new ArrayList<String>();
				tempMappingSet.add(map);
				impactMappings.put(impact, tempMappingSet);
			}		
		}
		
		Relationship rel = null;
		String s="";
		//if(!impactMappings.keySet().isEmpty())
		if(!impactMappings.keySet().isEmpty()&&impactMappings.get(maxImpact).size()!=0)
		{
			s=impactMappings.get(maxImpact).get(0);		
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
					
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}
		
		return rel;	 
	}
	
	public Relationship getCandidateMappingArcByImpactorandWeight1()
	{
		int maxImpact=0;
		int impactPlus=0,impactConflict=0,impactMinor=0; 
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(wantedRelationships);
		tempSet.removeAll(unWantedRelationships);
		HashMap<Integer,ArrayList<String>> impactMappings=new HashMap<Integer,ArrayList<String>>();	
		for(String map : Mappings)
		{
			HashSet<Relationship> relSet=mapToRel.get(map);			
			impactPlus=obtainImpactPlus(relSet,tempSet);
			impactConflict=	obtainImpactConflict(relSet);
			impactMinor=obtainImpactMinor(relSet,tempSet);
			int impact=Math.max(impactPlus+impactConflict, impactMinor);
			if(maxImpact<impact)
				maxImpact=impact;
			if(impactMappings.keySet().contains(impact))
				impactMappings.get(impact).add(map);
			else
			{
				ArrayList<String> tempMappingSet=new ArrayList<String>();
				tempMappingSet.add(map);
				impactMappings.put(impact, tempMappingSet);
			}		
		}
		
		Relationship rel = null;
		String s="";
		//if(impactMappings.get(maxImpact).size()!=0)
		if(!impactMappings.keySet().isEmpty()&&impactMappings.get(maxImpact).size()!=0)
		{
			double minWeight=1.1;
			HashMap<Double,ArrayList<String>> weightedMappings=new HashMap<Double,ArrayList<String>>();	
			for(String map : impactMappings.get(maxImpact))
			{	
				String parts[]=map.split(",");
				double weight=Double.parseDouble(parts[3]);
				//double weight=relToWeight.get(r);
				ArrayList<String> _mapping= new ArrayList<>();	
				if(minWeight>weight)
					minWeight=weight;
				if(weightedMappings.get(weight)==null)  //选取权重最小的值进行移除
				{
					_mapping.add(map);
					weightedMappings.put(weight, _mapping);
				}
				else  //闭包个数为num不在存在mapping中
				{
					weightedMappings.get(weight).add(map);			
				}
			}			
			s=weightedMappings.get(minWeight).get(0);			
			
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
					
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				//System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}
		
		return rel;	 
	}
	
	public Relationship getCandidateMappingArcByImpactorandWeight2()
	{
		double maxImpact=0;
		int impactPlus=0,impactConflict=0,impactMinor=0; 
		HashSet<Relationship> tempSet=new HashSet<Relationship>();
		tempSet.addAll(mappingSets);
		tempSet.removeAll(wantedRelationships);
		tempSet.removeAll(unWantedRelationships);
		HashMap<Double,ArrayList<String>> impactMappings=new HashMap<Double,ArrayList<String>>();	
		for(String map : Mappings)
		{
			String parts[]=map.split(",");
			double weight=Double.parseDouble(parts[3]);
			
			HashSet<Relationship> relSet=mapToRel.get(map);			
			impactPlus=obtainImpactPlus(relSet,tempSet);
			impactConflict=	obtainImpactConflict(relSet);
			impactMinor=obtainImpactMinor(relSet,tempSet);
			double impact=weight*Math.max(impactPlus+impactConflict, impactMinor);//考虑匹配的权重来作为可信度。
			
			//double impact=Math.max(impactPlus+impactConflict, impactMinor);//考虑匹配的权重来作为可信度。
			
			if(maxImpact<impact)
				maxImpact=impact;
			if(impactMappings.keySet().contains(impact))
				impactMappings.get(impact).add(map);
			else
			{
				ArrayList<String> tempMappingSet=new ArrayList<String>();
				tempMappingSet.add(map);
				impactMappings.put(impact, tempMappingSet);
			}		
		}
		
		Relationship rel = null;
		String s="";
		//if(impactMappings.get(maxImpact).size()!=0)
		if(!impactMappings.keySet().isEmpty()&&impactMappings.get(maxImpact).size()!=0)	
		{
			s=impactMappings.get(maxImpact).get(0);		
			Mappings.remove(s); //选中的匹配进行移除
			
			String parts[]=s.split(",");
			String subKey=parts[0];
			String supKey=parts[1];
			if(subKey.equals(supKey))
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
				rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey+"_2",subKey+"_1");		//***
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
					
			}	
			else 
			{
				String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
				System.out.println(relationshipName);
				//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
					rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				else
				{
					ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
					while(x.hasNext())
					{
						Relationship n=x.next();
						String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
						String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
						if(!comfrom1.equals(comfrom2))
						{
							rel=n;
							break;
						}
					}			
				}
				if(rel==null)
				{
					relationshipName = Tools4Graph.getRelationshipName(supKey,subKey);		//***		
					//rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();				
					if(relationshipIndex.get(NAMEPROPERTY, relationshipName).size()==1)
						rel=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
					else
					{
						ResourceIterator<Relationship> x=relationshipIndex.get(NAMEPROPERTY, relationshipName).iterator();
						while(x.hasNext())
						{
							Relationship n=x.next();
							String comfrom1=(String) n.getStartNode().getProperty(COMEFROMPROPERTY);
							String comfrom2=(String) n.getEndNode().getProperty(COMEFROMPROPERTY);
							if(!comfrom1.equals(comfrom2))
							{
								rel=n;
								break;
							}
						}			
					}
				}
				
			}	
		}
		
		return rel;	 
	}
	
	public int obtainImpactPlus(HashSet<Relationship> relSet, HashSet<Relationship> tempSet)
	{		
		HashSet<String> potentialMapping=new HashSet<String>(); //avoid repetition of calculation
		
		for(Relationship rel:relSet)
		{	
			if(rel==null)
				continue;
			//System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY) +"->"+rel.getEndNode().getProperty(NAMEPROPERTY) +" is assumed to be added!");
			//Relationship r0=rel.getStartNode().createRelationshipTo(rel.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);		
			rel.getStartNode().createRelationshipTo(rel.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);		
			for(Relationship r:tempSet)
			{	
				String startNode=r.getStartNode().getProperty(NAMEPROPERTY).toString();
				String endNode=r.getEndNode().getProperty(NAMEPROPERTY).toString();
				String nodeSource = r.getStartNode().getProperty(COMEFROMPROPERTY).toString();
				String nodeTarget = r.getEndNode().getProperty(COMEFROMPROPERTY).toString();
				Map<String, Object> params = new HashMap<>();	
				params.put( "a", startNode );	
				params.put( "b", endNode );
				params.put( "c", nodeSource );	
			    params.put( "d", nodeTarget );
				StringBuilder query = new StringBuilder();	    
				query.append("MATCH pp=(x)-[r1:INCLUDEDBY*]->(y) ");
				//query.append("WHERE x.Name=$a and y.Name=$b ");
				query.append("WHERE x.Name=$a and y.Name=$b and x.ComeFrom=$c and y.ComeFrom=$d ");
				query.append("RETURN pp");
				//System.out.println(query);
				Result result = graphDb.execute(query.toString(),params);		
				if(result.hasNext()==true) //如果有路径
				{				
					//System.out.println(r.getStartNode().getProperty(NAMEPROPERTY) +"->"+r.getEndNode().getProperty(NAMEPROPERTY) +" will be inferenced!");
					String mappings=relToMap.get(r);
//					System.out.println(mappings +" will be inferenced by "+rel.getStartNode().getProperty(NAMEPROPERTY) +"->"+rel.getEndNode().getProperty(NAMEPROPERTY));
//					System.out.println(mappings +" will be inferenced by "
//							+rel.getStartNode().getProperty(NAMEPROPERTY) +"->"+rel.getEndNode().getProperty(NAMEPROPERTY));
					potentialMapping.add(mappings);
				}			
			}
		
//			String start = r0.getStartNode().getProperty(NAMEPROPERTY).toString();
//			String end = r0.getEndNode().getProperty(NAMEPROPERTY).toString();
//			String source = r0.getStartNode().getProperty(COMEFROMPROPERTY).toString();
//			String target = r0.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			
			String start = rel.getStartNode().getProperty(NAMEPROPERTY).toString();
			String end = rel.getEndNode().getProperty(NAMEPROPERTY).toString();
			String source = rel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String target = rel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			
			StringBuilder query = new StringBuilder();
			String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
			query.append("MATCH (n)-[r]->(m) ");
			query.append(String.format(formatter, start, end, source, target));
			query.append("DELETE r");
			graphDb.execute(query.toString());	
			
			//rel.delete();
			
//			r0.removeProperty(NAMEPROPERTY);
//			relationshipIndex.remove(r0);
//			r0.delete();					
			//rel.delete();		
		}	

		return potentialMapping.size();
	}
	
	public int obtainImpactConflict(HashSet<Relationship> relSet)
	{		
		HashSet<String> potentialMapping=new HashSet<String>(); //avoid repetition of calculation		
		for(Relationship r:relSet)
		{
			if(!relMappingMIPSs.containsKey(r))
				continue;
			List<MIPS> relatedMIPSs=relMappingMIPSs.get(r);
			for(MIPS mips:relatedMIPSs)
			{
				Set<String> incoherentMappings=mips.unifyMappings(relToMap); //获取了最原始的匹配对
				String map=relToMap.get(r);
				if(existRejectedMappings2(map,incoherentMappings,wantedRelationships)) //Does MIPP conflict mappings.
				{
					incoherentMappings.remove(map); 
					for(String removedMap:incoherentMappings)
					{
						//System.out.println(removedMap +" will be conflicted with "+ map);
						potentialMapping.add(removedMap);
					}
				}
			}
		}
		return potentialMapping.size();
	}
	
	public int obtainImpactMinor(HashSet<Relationship> relSet, HashSet<Relationship> tempSet)
	{
		HashSet<String> potentialMapping=new HashSet<String>();  //avoid repetition of calculation
		for(Relationship unRel:relSet)
		{
			if(unRel==null)
				continue;
			String startNode= unRel.getStartNode().getProperty(NAMEPROPERTY).toString();
			String endNode=unRel.getEndNode().getProperty(NAMEPROPERTY).toString();
			String nodeSource = unRel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String nodeTarget = unRel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			//System.out.println(startNode+"->"+endNode);	
			for(Relationship re:tempSet)
			{
				//String mappings=relToMap.get(re);	
				
				//Relationship re0 = re.getStartNode().createRelationshipTo(re.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
				//System.out.println(re0.getStartNode().getProperty(NAMEPROPERTY) +"->"+re0.getEndNode().getProperty(NAMEPROPERTY));
				
				re.getStartNode().createRelationshipTo(re.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
				//System.out.println(re.getStartNode().getProperty(NAMEPROPERTY) +"->"+re.getEndNode().getProperty(NAMEPROPERTY));
				
				Map<String, Object> params = new HashMap<>();
			    params.put( "a", startNode );	
			    params.put( "b", endNode );
			    params.put( "c", nodeSource );	
			    params.put( "d", nodeTarget );
			    StringBuilder query = new StringBuilder();	    
			    query.append("MATCH pp=(x)-[r1:INCLUDEDBY*]->(y) ");
			    //query.append("WHERE x.Name=$a and y.Name=$b ");
			    query.append("WHERE x.Name=$a and y.Name=$b and x.ComeFrom=$c and y.ComeFrom=$d ");
			    query.append("RETURN pp");
//			    System.out.println(query);
			    Result result = graphDb.execute(query.toString(),params);
//			    while (result.hasNext()) 
//			    {
//			    	Map<String, Object> temp = result.next();		    	
//			    	Path pPath = (Path) temp.get("pp");
//			    		printAllPaths(pPath);	
//			    	System.out.println("===============================");
//			    } 			
				if(result.hasNext()==true) //如果有路径
				{
//					System.out.println(re0.getStartNode().getProperty(NAMEPROPERTY) +"->"+re0.getEndNode().getProperty(NAMEPROPERTY) +" "
//							+ "needs to be deleted!");
					String mappings=relToMap.get(re);
//					System.out.println(mappings + " will be deleted if "+(startNode+"->"+endNode)+" is rejected");
					potentialMapping.add(mappings);
				}
				
//				String start = re0.getStartNode().getProperty(NAMEPROPERTY).toString();
//				String end = re0.getEndNode().getProperty(NAMEPROPERTY).toString();
//				String source = re0.getStartNode().getProperty(COMEFROMPROPERTY).toString();
//				String target = re0.getEndNode().getProperty(COMEFROMPROPERTY).toString();
				
				String start = re.getStartNode().getProperty(NAMEPROPERTY).toString();
				String end = re.getEndNode().getProperty(NAMEPROPERTY).toString();
				String source = re.getStartNode().getProperty(COMEFROMPROPERTY).toString();
				String target = re.getEndNode().getProperty(COMEFROMPROPERTY).toString();
				
				StringBuilder query1 = new StringBuilder();
				String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
				query1.append("MATCH (n)-[r]->(m) ");
				query1.append(String.format(formatter, start, end, source, target));
				query1.append("DELETE r");
				graphDb.execute(query1.toString());	
				
				
//				graphDb.execute(query.toString());	
//				re0.removeProperty(NAMEPROPERTY);			
//				relationshipIndex.remove(re0);
//				re0.delete();
				//re.delete();
					
			}
			
		}
		return potentialMapping.size();
	}
	
	
	
	
	/**
	 * 更新图
	 * */
	public ArrayList<String>  updateGraph(List<Relationship> removedRelationship,List<Relationship> addedRelationship,GraphDatabaseService graphDB) 
	{
		ArrayList<String> removedMappings=new ArrayList<String>();
		// 删除移除的边
		for(Relationship rel:removedRelationship)
		{
			String start = rel.getStartNode().getProperty(NAMEPROPERTY).toString();
			String end = rel.getEndNode().getProperty(NAMEPROPERTY).toString();
			String source = rel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String target = rel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			double weight=Double.parseDouble(rel.getProperty(WEIGHTEDPROPERTY).toString());
			if(rel.getProperty(TYPEPROPERTY).toString().equals("Role"))  //角色匹配的删除方式
			{
//			    Map<String, Object> params = new HashMap<>();
//			    params.put( "start", start );
//			    params.put( "end", end );
//			    params.put( "source", source );
//			    params.put( "target", target );
//				StringBuilder query = new StringBuilder();
//				query.append("MATCH (t) - [r] -> (p)"); 
//				query.append("WHERE t.Name=$start and p.Name=$end ");
//				query.append( "and t.ComeFrom=$source and p.ComeFrom=$target "); 
//				query.append("DELETE r"); 
//				System.out.println(graphDb.execute(query.toString(),params));
				
				StringBuilder query = new StringBuilder();			
				String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
				query.append("MATCH (n)-[r]->(m) ");
				query.append(String.format(formatter, start, end, source, target));
				query.append(" DELETE r");
				//System.out.println(graphDb.execute(query.toString()));
				graphDb.execute(query.toString());
							
				Tools.clear(query);
				query.append("MATCH (n)-[r]->(m) ");
				query.append(String.format(formatter, "existence_"+start, "existence_"+end, source, target));
				query.append(" DELETE r");
				//System.out.println(graphDb.execute(query.toString()));
				graphDb.execute(query.toString());
				
				Tools.clear(query);
				query.append("MATCH (n)-[r]->(m) ");
				query.append(String.format(formatter, "inverse_"+start, "inverse_"+end, source, target));
				query.append(" DELETE r");
				//System.out.println(graphDb.execute(query.toString()));
				graphDb.execute(query.toString());
				
				Tools.clear(query);
				query.append("MATCH (n)-[r]->(m) ");
				query.append(String.format(formatter, "existence_inverse_"+start, "existence_inverse_"+end, source, target));
				query.append(" DELETE r");
				//System.out.println(graphDb.execute(query.toString()));
				graphDb.execute(query.toString());
			}			
			else		//概念匹配的删除方式
			{
				StringBuilder query = new StringBuilder();
				String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
				query.append("MATCH (n)-[r]->(m) ");
				query.append(String.format(formatter, start, end, source, target));
				query.append("DELETE r");
				//System.out.println(graphDb.execute(query.toString()));
				graphDb.execute(query.toString());
			}			
			//考虑可能是属性的情况
			start=start.replace("existence_", "").replace("inverse_", "");
			end=end.replace("existence_", "").replace("inverse_", "");			
			//removedMappings.add(start.replace("_1", "").replace("_2", "")+","+end.replace("_1", "").replace("_2", "")+","+weight+" "+source+"->"+target);
			String removedmap="";
			if(source.equals("Second")&&target.equals("First"))
				removedmap=end.replace("_1", "")+","+start.replace("_2", "")+",="+","+weight;
			else
				removedmap=start.replace("_1", "")+","+end.replace("_2", "")+",="+","+weight;
			if(!removedMappings.contains(removedmap))
				removedMappings.add(removedmap);
			
		}
		return removedMappings;	
		//this.graphDB=graphDB;
	}
	
	public void clearOneMapping4Graph(Relationship rel,GraphDatabaseService graphDB) 
	{
		System.out.println("clear the mapping arcs of graph");

			String start = rel.getStartNode().getProperty(NAMEPROPERTY).toString();
			String end = rel.getEndNode().getProperty(NAMEPROPERTY).toString();
			String source = rel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String target = rel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			
			String relationshipName=rel.getProperty(NAMEPROPERTY).toString();
			String comefrom=rel.getProperty(COMEFROMPROPERTY).toString();
			String weight=rel.getProperty(WEIGHTEDPROPERTY).toString();
			String relType=rel.getProperty(TYPEPROPERTY).toString();
			

//			StringBuilder query = new StringBuilder();
//			String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
//			query.append("MATCH (n)-[r]->(m) ");
//			query.append(String.format(formatter, start, end, source, target));
//			query.append("DELETE r");
//				//System.out.println(graphDb.execute(query.toString()));
//			graphDb.execute(query.toString());	
			
			rel.delete();
			

		printStateOfGraph();
	}
	
	public void clearMappings4Graph(ArrayList<Relationship> removedRelationship,GraphDatabaseService graphDB) 
	{
		System.out.println("clear the mapping arcs of graph");
		for(Relationship rel:removedRelationship)
		{
			String start = rel.getStartNode().getProperty(NAMEPROPERTY).toString();
			String end = rel.getEndNode().getProperty(NAMEPROPERTY).toString();
			String source = rel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
			String target = rel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
			
//			String relationshipName=rel.getProperty(NAMEPROPERTY).toString();
//			String comefrom=rel.getProperty(COMEFROMPROPERTY).toString();
//			String weight=rel.getProperty(WEIGHTEDPROPERTY).toString();
//			String relType=rel.getProperty(TYPEPROPERTY).toString();
			

			StringBuilder query = new StringBuilder();
			String formatter = "WHERE n.Name='%s' and m.Name='%s' and n.ComeFrom='%s' and m.ComeFrom='%s'";
			query.append("MATCH (n)-[r]->(m) ");
			query.append(String.format(formatter, start, end, source, target));
			query.append("DELETE r");
				//System.out.println(graphDb.execute(query.toString()));
			graphDb.execute(query.toString());	
			
			//rel.delete();
			
			//rel = rel.getStartNode().createRelationshipTo(rel.getEndNode(), GlobalAttribute.RelTypes.INCLUDEDBY);
//			rel.setProperty(COMEFROMPROPERTY, comefrom);
//			rel.setProperty(NAMEPROPERTY, relationshipName);
//			rel.setProperty(TYPEPROPERTY, relType); //是role的关系还是concept的关系
//			rel.setProperty(WEIGHTEDPROPERTY, weight); //权重
			
			
		}
		 
		printStateOfGraph();
	}
	
	public ArrayList<String> obtainMappingByRel(List<Relationship> removedRelationship)
	{
		ArrayList<String> removedMappings=new ArrayList<String>();
		for(Relationship rel:removedRelationship)
		{
//			String start = rel.getStartNode().getProperty(NAMEPROPERTY).toString();
//			String end = rel.getEndNode().getProperty(NAMEPROPERTY).toString();
//			String source = rel.getStartNode().getProperty(COMEFROMPROPERTY).toString();
//			String target = rel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
//			double weight=Double.parseDouble(rel.getProperty(WEIGHTEDPROPERTY).toString());
//			start=start.replace("existence_", "").replace("inverse_", "");
//			end=end.replace("existence_", "").replace("inverse_", "");			
//			//removedMappings.add(start.replace("_1", "").replace("_2", "")+","+end.replace("_1", "").replace("_2", "")+","+weight+" "+source+"->"+target);
//			String removedmap="";
//			if(source.equals("Second")&&target.equals("First"))
//				removedmap=end.replace("_1", "")+","+start.replace("_2", "")+",="+","+weight;
//			else
//				removedmap=start.replace("_1", "")+","+end.replace("_2", "")+",="+","+weight;
//			if(!removedMappings.contains(removedmap))
//				removedMappings.add(removedmap);
			String mapping=relToMap.get(rel);
			if(!removedMappings.contains(mapping))
				removedMappings.add(mapping);
		}
		return removedMappings;
	}
	
	public ArrayList<String> obtainMapping4Rel(List<Relationship> removedRelationship)
	{
		ArrayList<String> removedMappings=new ArrayList<String>();
		for(Relationship rel:removedRelationship)
		{
			String mappings=relToMap.get(rel);
			if(!removedMappings.contains(mappings))
				removedMappings.add(mappings);
		}
		return removedMappings;
	}
	
	
	public void createDbFromOwl(){		
		System.out.println("preprocessing owlInfo for graph ... ...");
		PreOWL4Graph.handle(getOwlInfo1());  //将角色进行扩充，增加其他三种形式 inverse_,existence_,existence_inverse_
		PreOWL4Graph.handle(getOwlInfo2());	 //将角色进行扩充，增加其他三种形式 inverse_,existence_,existence_inverse_
		System.out.println("Create graph database from owlInfo ... ...");
		createNodes(owlInfo1,owlInfo2);		
		createRelationshipsFromOwlInfo(owlInfo1,owlInfo2,MappingInformation);	
		
		//this.shutdown();     //无须显式调用，已经设置了挂钩函数，程序结束会调用图的shutdown
	}	
	
	/**
	 * 从本体信息(OWL Info)中创建节点，并记录节点的来源
	 * @param owl1               已经解析的本体1信息
	 * @param owl2               已经解析的本体2信息
	 */		
	private void createNodes(OWLInfo owl1, OWLInfo owl2){
		System.out.println(String.format("    #Creating class nodes from %s...", owl1.getOwlFileName()," "+owl2.getOwlFileName()));
		//取出所有的谓词，主要是概念和对象属性，这时已经做了图转换前的预处理
		Set<String> concepts1 = owl1.getConceptTokens();
		Set<String> objectproperties1 = owl1.getObjPropertyTokens();	
		Set<String> dataproperties1 = owl1.getDataPropertyTokens();
		Set<String> concepts2 = owl2.getConceptTokens();
		Set<String> objectproperties2 = owl2.getObjPropertyTokens();	
		Set<String> dataproperties2 = owl2.getDataPropertyTokens();	
		Transaction tx = graphDb.beginTx();   //开始事务		
		try{
			//创建概念节点
			for(String concept : concepts1){				
				createNode(concept,CONCEPTLABEL,COMEFROMFIRST);				
			}	
			//创建 属性/角色 节点
			for(String role : objectproperties1){				
			    createNode(role,ROLETYPE,COMEFROMFIRST);
			}
			for(String role : dataproperties1){				
			    createNode(role,ROLETYPE,COMEFROMFIRST);
			}
			for(String concept : concepts2){				
				createNode(concept,CONCEPTLABEL,COMEFROMSECOND);				
			}	
			//创建 属性/角色 节点
			for(String role : objectproperties2){				
			    createNode(role,ROLETYPE,COMEFROMSECOND);
			}
			for(String role : dataproperties2){				
			    createNode(role,ROLETYPE,COMEFROMSECOND);
			}
			tx.success(); //提交事务，节点添加完成
		}finally{
			tx.close();
		}
	}	
	
	
	/**
	 * 创建图数据库单个节点并添加到index中
	 * @param name            节点的名称
	 * @param type              节点的类别
	 * @param comefrom    节点来源本体(first or second)
	 * @return 已创建或者存在的节点
	 */
	private Node createNode(String name, String type, String comefrom){		
		//查询图中是否存在指定的概念节点，query的参数(节点属性名，节点属性值),索引的时候是对某个节点的某个属性来索引
		Node node = nodeIndex.query(NAMEPROPERTY, name).getSingle();
//		Node node1 = nodeIndex.query(NAMEPROPERTY, "A1_1").getSingle(); 测试A1_1是否为空
//		System.out.println(node1==null); 
		//创建一个节点，有三个属性(节点名，节点类型，节点来源) //判断为空才创建一个新的节点
		if(node == null){			
			node = graphDb.createNode();
			node.setProperty(NAMEPROPERTY, name);
			node.setProperty(TYPEPROPERTY, type);			
			node.setProperty(COMEFROMPROPERTY, comefrom);	
			if(comefrom.equals(COMEFROMFIRST))  //同名的两个概念可以用不同的本体来源来进行区分
				nodeIndex.add(node, NAMEPROPERTY,name+"_1");
			else
				nodeIndex.add(node, NAMEPROPERTY,name+"_2");
		}				
		return node;
	}	
	
	/**
	 * 从本体中构建关系
	 * @param owlInfo1 本体.
	 * @param owlInfo2 本体.
	 * @param MappingInformation 匹配信息
	 */	
	private void createRelationshipsFromOwlInfo(OWLInfo owlInfo1, OWLInfo owlInfo2,MappingInfo MappingInformation){
		System.out.println("    #Create relationship on graph of ontology "+owlInfo1.getOwlFileName()+" "+owlInfo2.getOwlFileName());
		//Set<OWLAxiom> Axioms = owlInfo.getAxiomsInTBox();
		List<AxiomMapping> aMappings1 = owlInfo1.getTBoxAxiomMappings();
		List<AxiomMapping> aMappings2 = owlInfo2.getTBoxAxiomMappings();
		Mappings = MappingInformation.getMappings();
		
		Transaction tx = graphDb.beginTx(); 
		try{
			for(AxiomMapping aMapping : aMappings1){
				//处理概念包含
				String subKey = aMapping.getSubKey();
				String supKey = aMapping.getSupKey();
				String relType = aMapping.getAxiomType();
				createRelationship(subKey,supKey,COMEFROMFIRST,relType);
				if(subKey.contains("negative")||supKey.contains("negative"))
					continue;
				if(relType.equals("Concept")||relType.equals("Role"))
				{
					//create subclassof relationship Map
					if(subAxioms.containsKey(supKey))
					{
						subAxioms.get(supKey).add(subKey);
					}
					else
					{
						HashSet<String> tempSet=new HashSet<String>();
						tempSet.add(subKey);
						subAxioms.put(supKey, tempSet);						
					}
					//create supclassof relationship Map
					if(supAxioms.containsKey(subKey))
					{
						//supAxioms.get(supKey).add(subKey);
						supAxioms.get(subKey).add(supKey);
					}
					else
					{
						HashSet<String> tempSet=new HashSet<String>();
						tempSet.add(supKey);
						supAxioms.put(subKey, tempSet);
					}
					//if(subMap)
				}
			}
			for(AxiomMapping aMapping : aMappings2){
				//处理概念包含
				String subKey = aMapping.getSubKey();
				String supKey = aMapping.getSupKey();
				String relType = aMapping.getAxiomType();  //Concept
				createRelationship(subKey,supKey,COMEFROMSECOND,relType);	
				if(relType.equals("Concept")||relType.equals("Role"))
				{
					//create subclassof relationship Map
					if(subAxioms.containsKey(supKey))
					{
						subAxioms.get(supKey).add(subKey);
					}
					else
					{
						HashSet<String> tempSet=new HashSet<String>();
						tempSet.add(subKey);
						subAxioms.put(supKey, tempSet);
						
					}
					//create supclassof relationship Map
					if(supAxioms.containsKey(subKey))
					{
						//supAxioms.get(supKey).add(subKey);
						supAxioms.get(subKey).add(supKey);
					}
					else
					{
						HashSet<String> tempSet=new HashSet<String>();
						tempSet.add(supKey);
						supAxioms.put(subKey, tempSet);
					}
					//if(subMap)
				}
				
				
				
			}
			for(String mapping : Mappings){
				//处理概念包含
				String parts[]=mapping.split(",");
				//if(parts[2].equals("="))
				//判断mappings是以概念出现还是角色出现(角色的情况要创建的关系会更多)
				if(owlInfo1.getObjPropertyTokens().contains(parts[0])||owlInfo2.getObjPropertyTokens().contains(parts[0]))
				{
					Relationship relationship1 = null;
					Relationship relationship2 = null;
					Relationship relationship3 = null;
					Relationship relationship4 = null;
					Relationship relationship5 = null;
					Relationship relationship6 = null;
					Relationship relationship7 = null;
					Relationship relationship8 = null;
					
					if(parts[2].equals("|"))
					{
						relationship1=createRelationshipForMappings(parts[0],parts[1],"positive",CONJUNCTION,ROLETYPE,parts[3]);
						relationship2=createRelationshipForMappings(Tools.getInverseToken(parts[0]),Tools.getInverseToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);
						relationship3=createRelationshipForMappings(Tools.getExistenceToken(parts[0]),Tools.getExistenceToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);
						relationship4=createRelationshipForMappings(Tools.getExistenceInverseToken(parts[0]),Tools.getExistenceInverseToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);
						
						relToMap.put(relationship1, mapping);
						relToMap.put(relationship2, mapping);
						relToMap.put(relationship3, mapping);
						relToMap.put(relationship4, mapping);
						
						if(mapToRel.containsKey(mapping))	
						{
							mapToRel.get(mapping).add(relationship1);
							mapToRel.get(mapping).add(relationship2);
							mapToRel.get(mapping).add(relationship3);
							mapToRel.get(mapping).add(relationship4);
						}
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship1);
							list.add(relationship2);
							list.add(relationship3);
							list.add(relationship4);
							mapToRel.put(mapping,list);
						}									
					}
					else 
					{
						relationship1=createRelationshipForMappings(parts[0],parts[1],"positive",CONJUNCTION,ROLETYPE,parts[3]);	
						relationship2=createRelationshipForMappings(parts[1],parts[0],"negative",CONJUNCTION,ROLETYPE,parts[3]);
						relationship3=createRelationshipForMappings(Tools.getInverseToken(parts[0]),Tools.getInverseToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);	
						relationship4=createRelationshipForMappings(Tools.getInverseToken(parts[1]),Tools.getInverseToken(parts[0]),"negative",CONJUNCTION,ROLETYPE,parts[3]);
						relationship5=createRelationshipForMappings(Tools.getExistenceToken(parts[0]),Tools.getExistenceToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);	
						relationship6=createRelationshipForMappings(Tools.getExistenceToken(parts[1]),Tools.getExistenceToken(parts[0]),"negative",CONJUNCTION,ROLETYPE,parts[3]);
						relationship7=createRelationshipForMappings(Tools.getExistenceInverseToken(parts[0]),Tools.getExistenceInverseToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);	
						relationship8=createRelationshipForMappings(Tools.getExistenceInverseToken(parts[1]),Tools.getExistenceInverseToken(parts[0]),"negative",CONJUNCTION,ROLETYPE,parts[3]);
											
						relToMap.put(relationship1, mapping);
						relToMap.put(relationship2, mapping);
						relToMap.put(relationship3, mapping);
						relToMap.put(relationship4, mapping);
						relToMap.put(relationship5, mapping);
						relToMap.put(relationship6, mapping);
						relToMap.put(relationship7, mapping);
						relToMap.put(relationship8, mapping);
						
						if(mapToRel.containsKey(mapping))	
						{
							mapToRel.get(mapping).add(relationship1);
							mapToRel.get(mapping).add(relationship2);
							mapToRel.get(mapping).add(relationship3);
							mapToRel.get(mapping).add(relationship4);
							mapToRel.get(mapping).add(relationship5);
							mapToRel.get(mapping).add(relationship6);
							mapToRel.get(mapping).add(relationship7);
							mapToRel.get(mapping).add(relationship8);
						}
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship1);
							list.add(relationship2);
							list.add(relationship3);
							list.add(relationship4);
							list.add(relationship5);
							list.add(relationship6);
							list.add(relationship7);
							list.add(relationship8);
							mapToRel.put(mapping,list);
						}				
					}
				}
				else if(owlInfo1.getDataPropertyTokens().contains(parts[0])||owlInfo2.getDataPropertyTokens().contains(parts[0]))
				{
					Relationship relationship1 = null;
					Relationship relationship2 = null;
					Relationship relationship3 = null;
					Relationship relationship4 = null;
					if(parts[2].equals("|"))
					{
						relationship1=createRelationshipForMappings(parts[0],parts[1],"positive",CONJUNCTION,ROLETYPE,parts[3]);
						relationship2=createRelationshipForMappings(Tools.getInverseToken(parts[0]),Tools.getInverseToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);
						relToMap.put(relationship1, mapping);
						relToMap.put(relationship2, mapping);
						
						if(mapToRel.containsKey(mapping))	
						{
							mapToRel.get(mapping).add(relationship1);
							mapToRel.get(mapping).add(relationship2);
						}
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship1);
							list.add(relationship2);
							mapToRel.put(mapping,list);
						}					
					}
					else 
					{
						relationship1=createRelationshipForMappings(parts[0],parts[1],"positive",CONJUNCTION,ROLETYPE,parts[3]);	
						relationship2=createRelationshipForMappings(parts[1],parts[0],"negative",CONJUNCTION,ROLETYPE,parts[3]);
						relationship3=createRelationshipForMappings(Tools.getExistenceInverseToken(parts[0]),Tools.getExistenceInverseToken(parts[1]),"positive",CONJUNCTION,ROLETYPE,parts[3]);	
						relationship4=createRelationshipForMappings(Tools.getExistenceInverseToken(parts[1]),Tools.getExistenceInverseToken(parts[0]),"negative",CONJUNCTION,ROLETYPE,parts[3]);
						relToMap.put(relationship1, mapping);
						relToMap.put(relationship2, mapping);
						relToMap.put(relationship3, mapping);
						relToMap.put(relationship4, mapping);
						
						if(mapToRel.containsKey(mapping))	
						{
							mapToRel.get(mapping).add(relationship1);
							mapToRel.get(mapping).add(relationship2);
							mapToRel.get(mapping).add(relationship3);
							mapToRel.get(mapping).add(relationship4);
						}
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship1);
							list.add(relationship2);
							list.add(relationship3);
							list.add(relationship4);
							mapToRel.put(mapping,list);
						}		
					}
				}
				//判断mappings是以概念出现还是角色出现
				else
				{
					Relationship relationship = null;
					if(parts[2].equals(">")) //parts[1]包含于part[0]
					{
						relationship=createRelationshipForMappings(parts[0],parts[1],"positive",CONJUNCTION,CONCEPTTYPE,parts[3]);
						relToMap.put(relationship, mapping);
						
						if(mapToRel.containsKey(mapping))						
							mapToRel.get(mapping).add(relationship);
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship);
							mapToRel.put(mapping,list);
						}				
					}
					else if(parts[2].equals("<")) //parts[1]包含于part[0]
					{
						relationship=createRelationshipForMappings(parts[1],parts[0],"negative",CONJUNCTION,CONCEPTTYPE,parts[3]);
						relToMap.put(relationship, mapping);
						
						if(mapToRel.containsKey(mapping))						
							mapToRel.get(mapping).add(relationship);
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship);
							mapToRel.put(mapping,list);
						}
					}
					else
					{
						relationship=createRelationshipForMappings(parts[0],parts[1],"positive",CONJUNCTION,CONCEPTTYPE,parts[3]);
						relToMap.put(relationship, mapping);
						
						if(mapToRel.containsKey(mapping))						
							mapToRel.get(mapping).add(relationship);
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship);
							mapToRel.put(mapping,list);
						}
						
						relationship=createRelationshipForMappings(parts[1],parts[0],"negative",CONJUNCTION,CONCEPTTYPE,parts[3]);
						relToMap.put(relationship, mapping);
						
						if(mapToRel.containsKey(mapping))						
							mapToRel.get(mapping).add(relationship);
						else
						{
							HashSet<Relationship> list=new HashSet<Relationship>();
							list.add(relationship);
							mapToRel.put(mapping,list);
						}
					}
					
				}
				//else if(parts[2].equals("|"))
				//createRelationship(parts[0],parts[1],COMEFROMFIRST,relType);
			}
			tx.success();
		}
		finally{
			tx.close();
		}
	}	
	
	
	/**
	 * 创建一个图上的关系来表示包含公理，子类指向父类<br>
	 * 一个关系有三个属性：关系来源(first|second)，关系的名称(sub***sup)，关系的类型(INCLUDEDBY|MEMBEROF)
	 * @param subKey           子类标识符 
	 * @param supKey           父类表示符
	 * @param comefrom       关系来源本体
	 * @param relType           关系的类型，是role的关系还是concept的关系
	 */
	private void createRelationship(String subKey, String supKey, String comefrom, String relType){
		Relationship relationship = null;				
		Node subNode=null, supNode=null;		
		//找出节点来，图中是先把概念节点和role节点添加进去了
		try{
			if(comefrom.equals(COMEFROMFIRST))
			{
				//Node node1 = nodeIndex.query(NAMEPROPERTY, "A1_1").getSingle();			
			    subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_1").getSingle();		
			    supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_1").getSingle();
			}
			else 
			{
				subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_2").getSingle();		
				supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_2").getSingle();
			}
		}catch(Exception e){
			System.out.println("Signle ontology Exception: "+subKey + " * " + supKey);
		}			
		if(subNode!=null && supNode!=null){
			String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***	
			//IndexHits<Relationship> hits = relationshipIndex.query(NAMEPROPERTY,relationshipName); //查找关系是否存在
			//如果关系不存在，添加关系，并将新的关系添加到关系索引中(在本体匹配中可能不需要)
			//if(hits.size()==0){
				relationship = subNode.createRelationshipTo(supNode,	GlobalAttribute.RelTypes.INCLUDEDBY);
				relationship.setProperty(COMEFROMPROPERTY, comefrom);
				relationship.setProperty(NAMEPROPERTY, relationshipName);
				relationship.setProperty(TYPEPROPERTY, relType);
				//relationship.setProperty(WEIGHTEDPROPERTY, "1.0");
				relationshipIndex.add(relationship, NAMEPROPERTY, relationshipName);
			//}
		}
	}
	
	/**
	 * 创建一个图上的关系来表示mappings
	 * 一个关系有三个属性：关系来源(first|second)，关系的名称(sub***sup)，关系的类型(INCLUDEDBY|MEMBEROF)
	 * @param subKey           子类标识符 
	 * @param supKey           父类表示符
	 * @param direction        方向
	 * @param comefrom         关系来源本体
	 * @param relType          关系的类型，是role的关系还是concept的关系
	 * @param weight           mapping的权重
	 */
	private Relationship createRelationshipForMappings(String subKey, String supKey,String direction, String comefrom, String relType, String weight){
		Relationship relationship = null;				
		Node subNode=null, supNode=null;		
		//找出节点来，图中是先把概念节点和role节点添加进去了
		try{
			if(direction.equals("positive"))//即从本体1指向本体2
			{
			  subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_1").getSingle();		
			  supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_2").getSingle();	
			}
			else 
			{
			  subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_2").getSingle();		
			  supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_1").getSingle();	
			}
		}catch(Exception e){
			System.out.println("Mappings Exception: "+subKey + " * " + supKey);
		}			
		if(subNode!=null && supNode!=null){
			String relationshipName ="";
			if(subKey.equals(supKey)&&direction.equals("positive"))//即从本体1指向本体2
			{
				relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");	
			}
			else if(subKey.equals(supKey)&&direction.equals("negative"))//即从本体1指向本体2
			{
				relationshipName = Tools4Graph.getRelationshipName(subKey+"_2", supKey+"_1");
			}
			else 
			{
				relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);
			}		
			//String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
			relationship = subNode.createRelationshipTo(supNode, GlobalAttribute.RelTypes.INCLUDEDBY);
			relationship.setProperty(COMEFROMPROPERTY, comefrom);
			relationship.setProperty(NAMEPROPERTY, relationshipName);
			relationship.setProperty(TYPEPROPERTY, relType); //是role的关系还是concept的关系
			relationship.setProperty(WEIGHTEDPROPERTY, weight); //权重
			
			mappingSets.add(relationship);
			relationshipIndex.add(relationship, NAMEPROPERTY, relationshipName);
			relToWeight.put(relationship, Double.parseDouble(weight));
		}
		return relationship;
	}
	
	
	public static void printAllPaths(Path p) {     	
		for(Relationship rel : p.relationships()){
			Node start = rel.getStartNode();
			Node end = rel.getEndNode();
			System.out.print(start.getProperty(NAMEPROPERTY)+"->"+end.getProperty(NAMEPROPERTY)+" ");			
		}
		System.out.println();
	}
	
	public void printRelMappingMIMPP(HashMap<Relationship, List<MIPS>> relMappingMIPSs)
	{
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("Print the minimal incoherent mappings:");
		for(Relationship r:relMappingMIPSs.keySet())
		{
			System.out.println("======================================");
			System.out.println("The incoherent mapping is "+ r.getStartNode().getProperty(NAMEPROPERTY)+"->" + r.getEndNode().getProperty(NAMEPROPERTY));
			List<MIPS> current =relMappingMIPSs.get(r);
			System.out.println("The number minimal conflict set is: " + current.size());
			for(MIPS a:current)
			{
				System.out.println("The unsatisfiable node is "+a.getUnsatNode().getProperty(NAMEPROPERTY)+" "+a.getUnsatNode().getProperty(COMEFROMPROPERTY));
				System.out.println("The number minimal conflict set is: ");
				a.printMIPS();
			}
		}	
		
	}
	
	public void printStateOfGraph()  //打印当前匹配弧的状态
	{
		StringBuilder query1 = new StringBuilder(); 
		//query1.append("MATCH ()-[rel:Conj]->() RETURN rel;");
		query1.append("MATCH ()-[rel]->() RETURN rel;");
		System.out.println(query1);
		Result result = graphDb.execute(query1.toString());
		while (result.hasNext()) 
		{
			Map<String, Object> temp = result.next();
			for ( String key : result.columns() )  //对应的按列来进行读取
			{
				System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
//           	if((Relationship)temp.get(key))
				Relationship rel=(Relationship) temp.get( key );
				System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY)+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
//				System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString()+"  "+rel.getType());
				System.out.println("ID is "+rel.getId());
			}
			System.out.println("===============================");
		}
		System.out.println();
	}
	
	

	
	/**
	 * 正常关闭图数据库
	 */
	public void shutdown(){		
		this.graphDb.shutdown();
		System.out.println("Shut down graph database.");
	}
}
	

	

