/******************************************************************************
* Copyright 2013-2016 LASIGE                                                  *
*                                                                             *
* Licensed under the Apache License, Version 2.0 (the "License"); you may     *
* not use this file except in compliance with the License. You may obtain a   *
* copy of the License at http://www.apache.org/licenses/LICENSE-2.0           *
*                                                                             *
* Unless required by applicable law or agreed to in writing, software         *
* distributed under the License is distributed on an "AS IS" BASIS,           *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    *
* See the License for the specific language governing permissions and         *
* limitations under the License.                                              *
*                                                                             *
*******************************************************************************
* A filtering algorithm based on logical coherence.                           *
*                                                                             *
* @author Daniel Faria & Emanuel Santos                                       *
******************************************************************************/
package aml.filter;

import java.math.BigDecimal;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.Map.Entry;

import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout.ElectricalForce;

import java.util.Set;
import java.util.TreeMap;

import com.hp.hpl.jena.util.iterator.Filter;
import com.sun.javafx.collections.SetAdapterChange;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.webkit.graphics.Ref;

import aml.AML;
import aml.match.Mapping;
import aml.ontology.DataProperty;
import aml.ontology.ObjectProperty;
import aml.settings.MappingRelation;
import aml.settings.MappingStatus;
import aml.util.InteractionManager;
import aml.util.MIPP;
import aml.util.Pair;

public class RepairerGraph implements Flagger
{
	
//Attributes
	
	private AML aml;
	//private RepairMap rMap;
	//private RepairMapRefine rMap;
	private RepairMapGraph rMap;
	private InteractionManager im;
	private Vector<Mapping> maps;
	private ArrayList<Mapping> reliableMappings;
	private ArrayList<Mapping> wantMappings;
	private ArrayList<Mapping> unwantMappings;
	private Random generator;
	private int error_user;
	
	public int approveNum,rejectNum,iteration;
	public int orcalePositiveNum=0,orcaleNegativeNum=0;
	
//Constructors
	
	/**
	 * Constructs a Repairer for automatic repair
	 */
	public RepairerGraph()
	{
		aml = AML.getInstance();
		rMap = aml.getRepairGraph(); 
		if(rMap == null)
			rMap = aml.buildRepairMapGraph();
		im = aml.getInteractionManager();
		maps=aml.getAlignment().getMappingSet();
		reliableMappings =new ArrayList<Mapping>();
		wantMappings =new ArrayList<Mapping>();
		unwantMappings=new ArrayList<Mapping>();
		//generator = new Random(Calendar.getInstance().getTimeInMillis());
		generator = new Random();
		error_user = 0;
	}
	
	private boolean isUserFailing(){
		
		int random_num = generator.nextInt(100);
		
		if (random_num < error_user){
			return true;
		}
		return false;
		
	}
	
	public void obtainReliableMappings()
	{
		//find the incoherent set ID
		HashSet<Integer> indexSet=new HashSet<Integer>();
		indexSet.addAll(rMap.MapMinimalConflictSet.keySet());
		Vector<Mapping> filterMaps=filterSet(maps,indexSet);
		//Vector<Mapping> reliableMappings =new Vector<Mapping>();;
		Vector<Mapping> tempReliableMappings =rMap.structureRefine(filterMaps,indexSet);
		//根据返回的结果进行标记
		for(Mapping m: tempReliableMappings)
		{
			
			int sourceId=m.getSourceId();
			int targetId=m.getTargetId();
			double sim=m.getSimilarity();
			if(sim>=0.0) // threshold constraint
			{		
				Mapping realMapping = aml.getAlignment().getBidirectional(sourceId, targetId);
				m.setStatus(MappingStatus.CORRECT);
				realMapping.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);			
				//rMap.updateConflictSet(m,reliableMappings,unwantMappings);
				rMap.updateGraph(m);
				reliableMappings.add(m);
			}
			//aml.getAlignment().get(sourceId, targetId).setStatus(MappingStatus.CORRECT);
		}	
		
//		System.out.println("The size of MinimalConflictSet is "+ rMap.MinimalConflictSet.size());
//		System.out.println("The size of MapMinimalConflictSet is "+ rMap.MapMinimalConflictSet.size());
	}
	public Vector<Mapping> filterSet(Vector<Mapping> mappings, HashSet<Integer> indexs)
	{
		Vector<Mapping> set=new Vector<Mapping>();
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		tempMappings.addAll(maps);
		for(int i:indexs)
		{
			Mapping map=tempMappings.get(i);
			set.add(map);
		}
		Vector<Mapping> filterset=new Vector<Mapping>();
		filterset.addAll(mappings);
		filterset.removeAll(set);
		return filterset;
	}
	
	public void One2OneRevisedMappings()
	{
		System.out.println("Repairing Alignment");
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));
			int mappingIndex = randomSelect(); //random select
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is\n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	 
		        	//the decision in the reference alignment
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;		        	
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            rMap.one2oneRestriction(mappingIndex,wantMappings,unwantMappings);	
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping.");
					rMap.simpleReject(mappingIndex,wantMappings,unwantMappings);	  //�����˾�ע������ع鵽Christian�� Tool�ķ��������ܾ���Ӱ��
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);	
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The Saving Ratio is : "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
				
		aml.removeIncorrect();		
		maps=tempMappings;
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}
	
	public void ChristianToolRevisedMappings()
	{
		System.out.println("Repairing Alignment");
		approveNum=0;
		rejectNum=0;
		iteration=0;	
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));
			int mappingIndex = getCandidateMappingByStructure(); //Christian Bridge rule function
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is\n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				
		        System.out.println("Please make a judgement for this mapping  (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	 
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian Tool reasoning
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
			        		orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping.");
					rMap.simpleReject(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.print("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print(" Saving: "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}

	public void GraphRevisedMappingsCompleteByStrutureFunction()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));		
			int mappingIndex = getCandidateMappingByStructure(); //Christian Toolsʼ function
			
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
		        System.out.println("Please make a judgement for this mapping  (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	 
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;
		            System.out.println("ִExecute the action according to the approved mapping.");  
		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping.");
					rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);			
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.print("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print(" Saving: "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}
	
	public void ArcByGuaranteedRevisedMappings()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("The iteration is "+(iteration++));
			//int mappingIndex = getCandidateMapping2(); //��Ȩ�����ĳ�ȡ����	
			//int mappingIndex = randomSelect(); //��δ��ǵ������������ȡ	
			//int mappingIndex = getCandidateMappingByImpactor(); //����impactor factor�Ķ��������г�ȡ
			
			int mappingIndex = getCandidateMappingArcByGuaranteed(); //JWS Guaranteed function
			
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;
		            System.out.println("Execute the action according to the approved mapping.");  
		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping."); 
		        	System.out.println("The mapping needs to be removed.");
					rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);			
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.print("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print(" Saving: "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}
	
	public void ArcByNormedRevisedMappings()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("The iteration is "+(iteration++));
			//int mappingIndex = getCandidateMapping2(); //��Ȩ�����ĳ�ȡ����	
			//int mappingIndex = randomSelect(); //��δ��ǵ������������ȡ	
			//int mappingIndex = getCandidateMappingByImpactor(); //����impactor factor�Ķ��������г�ȡ
			
			int mappingIndex = getCandidateMappingArcByNormed(); //JWS normed function
			
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	 
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;
		            System.out.println("Execute the action according to the approved mapping.");  
		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping."); 
		        	System.out.println("The mapping needs to be removed.");
					rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);			
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.print("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print(" Saving: "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}
	
	public void GraphRevisedMappingsCompleteByOptimalFunction()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("The iteration is "+(iteration++));
			//int mappingIndex = getCandidateMapping2(); //��Ȩ�����ĳ�ȡ����	
			//int mappingIndex = randomSelect(); //��δ��ǵ������������ȡ	
			int mappingIndex = getCandidateMappingByImpactor(); // our defined impactor factor
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();

		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	 
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;
		            System.out.println("Execute the action according to the approved mapping.");  
		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
			        	orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping."); 
		        	System.out.println("The mapping needs to be removed.");
					rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);			
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.print("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print(" Saving: "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}
	
	public void GraphRevisedMappingsCompleteByWeightPlus()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("The iteration is "+(iteration++));
			//int mappingIndex = getCandidateMapping2(); //��Ȩ�����ĳ�ȡ����	
			//int mappingIndex = randomSelect(); //��δ��ǵ������������ȡ	
			//int mappingIndex = getCandidateMappingByImpactor(); //����impactor factor�Ķ��������г�ȡ
			int mappingIndex = getCandidateMappingArcByImpactorandWeight(); //weight impactor factor
		
			if(mappingIndex != -1)
			{			
				Scanner sc = new Scanner(System.in);  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				printDetailInformation(maps.get(mappingIndex));
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
		        System.out.println("Please make a judgement for this mapping (Y/N):");  
		        if(sc.nextLine().equalsIgnoreCase("Y"))
		        { 	   	
		        	if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcalePositiveNum++;
		            System.out.println("Execute the action according to the approved mapping.");  
		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
		            approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        		orcaleNegativeNum++;
		        	System.out.println("Execute the action according to the declined mapping."); 
		        	System.out.println("The mapping needs to be removed.");
					rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);			
					rejectNum++;
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.print("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print(" Saving: "+ String.format("%1$.1f", saving)+"%\n");
		
		double orcalePostivePrecision=orcalePositiveNum*1.0/approveNum*100;
		double orcalenegativePrecision=orcaleNegativeNum*1.0/rejectNum*100;	
		System.out.println("The orcale positive number: "+orcalePositiveNum +"  The orcale negative number: " +orcaleNegativeNum);
		System.out.print("Oracle Positive Precision is "+ String.format("%1$.2f", orcalePostivePrecision)+"%  ");
		System.out.print("Oracle Negative Precision is "+ String.format("%1$.2f", orcalenegativePrecision)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
	}

	
	public void AutoBasedReferenceAlignmentOne2OneRevised()
	{
		System.out.println("Repairing Alignment");
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));
			int mappingIndex = randomSelect(); //random select
			if(mappingIndex != -1)
			{			
				System.out.println("The current mapping is\n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
				//if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
		        if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        { 	   		
		        	if(!isUserFailing())
		        	{
		        		System.out.println("ִExecute the action according to the approved mapping.");  
		        		rMap.one2oneRestriction(mappingIndex,wantMappings,unwantMappings);	
		        		approveNum++;
		        	}
		        	else
		        	{
		        		System.out.println("ִExecute the action according to the declined mapping."); 
						rMap.simpleReject(mappingIndex,wantMappings,unwantMappings);	  //�����˾�ע������ع鵽Christian�� Tool�ķ��������ܾ���Ӱ��
						rejectNum++;
		        	}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		System.out.println("ִExecute the action according to the declined mapping."); 
		        		rMap.simpleReject(mappingIndex,wantMappings,unwantMappings);	  //�����˾�ע������ع鵽Christian�� Tool�ķ��������ܾ���Ӱ��
		        		rejectNum++;
		        	}
		        	else
		        	{
		        		System.out.println("ִExecute the action according to the approved mapping.");  
		        		rMap.one2oneRestriction(mappingIndex,wantMappings,unwantMappings);	
		        		approveNum++;
		        	}
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
	}
	
	public void AutoBasedReferenceAlignmentChristianTool()
	{
		System.out.println("Repairing Alignment");
		approveNum=0;
		rejectNum=0;
		iteration=0;	
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));
			int mappingIndex = getCandidateMappingByStructure(); //Christian Bridge rule function
			if(mappingIndex != -1)
			{			
				
				System.out.println("The current mapping is\n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
		        //if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
				
		        if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))	
		        { 	
		        	if(!isUserFailing())
		        	{
		        		System.out.println("ִExecute the action according to the approved mapping.");  
		        		rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian Tool reasoning
		        		approveNum++;
		        	}
		        	else
		        	{
		        		System.out.println("Execute the action according to the declined mapping.");
						rMap.simpleReject(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
						rejectNum++;
		        	}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		//System.out.println("ִ��ר�Ҿܾ�����"); 
		        		System.out.println("Execute the action according to the declined mapping.");
		        		rMap.simpleReject(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
		        		rejectNum++;
		        	}
		        	else
		        	{
		        		System.out.println("ִExecute the action according to the approved mapping.");  
		        		rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian Tool reasoning
		        		approveNum++;
		        	}
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
//		aml.getAlignment().getMappingSet().retainAll(tempMappings);
//		aml.getAlignment().getMappingSet().addAll(reliableMappings);
		
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		System.out.println("The number of decided mappings is "+ aml.getAlignment().getMappingSet().size());
		aml.removeIncorrect();	
		System.out.println("The number of left correct mappings is "+ aml.getAlignment().getMappingSet().size());
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
		System.out.println("The number of mappings (added reliableMappings) is "+ aml.getAlignment().getMappingSet().size());
	}
	
	public void AutoBasedReferenceAlignmentAutoArcByGuaranteed()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));		
			//int mappingIndex = getCandidateMappingArcByGuaranteed(); //JWS Guaranteed function
			int mappingIndex = getCandidateMappingArcByGuaranteed2(); //JWS Guaranteed function
			if(mappingIndex != -1)
			{			
				
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
				//if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
				if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        { 	  
					if(!isUserFailing())
		        	{
						System.out.println("ִExecute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						//rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
						approveNum++;
		        	}
					else
					{
						System.out.println("Execute the action according to the declined mapping.");
						rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
						//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
						rejectNum++;
					}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		System.out.println("Execute the action according to the declined mapping.");
		        		rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
		        		//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
		        		rejectNum++;
		        	}
		        	else
		        	{
		        		System.out.println("ִExecute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						//rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
						approveNum++;
		        	}
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
		

	}
	
	public void AutoBasedReferenceAlignmentArcByNormed()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));		
			int mappingIndex = getCandidateMappingArcByNormed(); //JWS normed function
			if(mappingIndex != -1)
			{			
				
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
				//if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
				if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        { 	
					if(!isUserFailing())
		        	{
						System.out.println("ִExecute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						//rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
						approveNum++;
		        	}
					else
					{
						System.out.println("Execute the action according to the declined mapping.");
						rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
						//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
						rejectNum++;
					}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{	
		        		System.out.println("Execute the action according to the declined mapping.");
		        		rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
		        		//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
		        		rejectNum++;
		        	}
		        	else
		        	{
						System.out.println("ִExecute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						//rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
						approveNum++;
		        	}
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
	}

	public void AutoBasedRefAlignByGraphRevisedMappingStrutureFunction()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));		
			int mappingIndex = getCandidateMappingByStructure(); //Christian Bridge rule function
			if(mappingIndex != -1)
			{			
				
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
				//if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
				if(aml.getReferenceAlignment().containsMapping(sourceId, targetId)&&!isUserFailing())
		        { 	   
					if(!isUserFailing())
		        	{
						System.out.println("ִExecute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						//rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
						approveNum++;
		        	}
					else
					{
						System.out.println("Execute the action according to the declined mapping.");
						rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
						//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
						rejectNum++;
					}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		System.out.println("Execute the action according to the declined mapping.");
		        		rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
		        		//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
		        		rejectNum++;	
		        	}
		        	else
		        	{
						System.out.println("ִExecute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						//rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
						approveNum++;
		        	}
		        } 				
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
	}
	
	public void AutoBasedRefAlignByGraphRevisedMappingOptimalFunction()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("The iteration is "+(iteration++));
			//int mappingIndex = getCandidateMapping2(); //��Ȩ�����ĳ�ȡ����	
			//int mappingIndex = randomSelect(); //��δ��ǵ������������ȡ	
			int mappingIndex = getCandidateMappingByImpactor(); // our defined impactor factor
			if(mappingIndex != -1)
			{			
				  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
		        //if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
				if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        { 
					if(!isUserFailing())
		        	{
						System.out.println("Execute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						approveNum++;
		        	}
					else
					{
						System.out.println("Execute the action according to the declined mapping."); 
			        	//System.out.println("The mapping needs to be removed.");
						rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
						//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings);
						rejectNum++;
					}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		System.out.println("Execute the action according to the declined mapping."); 
		        		//System.out.println("The mapping needs to be removed.");
		        		rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
		        		//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings);
		        		rejectNum++;
		        	}
		        	else
		        	{
		        		System.out.println("Execute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						approveNum++;
		        	}
		        } 
				
//		        if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
//		        { 	   		        			        	
//		            System.out.println("Execute the action according to the approved mapping.");  
//		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
//		            approveNum++;
//		        }
//		        else //��������չ��unknown�����
//		        {  
//		        	System.out.println("Execute the action according to the rejected mapping."); 
//		        	System.out.println("The mapping needs to be removed.");
//					//rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
//					rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings);
//					rejectNum++;
//		        } 				
			}
			else //�Ѿ�������mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
	}
	
	public void AutoBasedRefAlignByGraphRevisedMappingWeightPlus()
	{
		System.out.println("Repairing Alignment");		
		approveNum=0;
		rejectNum=0;
		iteration=0;		
		while(true)
		{
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("The iteration is "+(iteration++));	
			int mappingIndex = getCandidateMappingArcByImpactorandWeight(); //weight impactor factor
			if(mappingIndex != -1)
			{			
				  
				System.out.println("The current mapping is \n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
		        //if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
				if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
		        { 
					if(!isUserFailing())
		        	{
						System.out.println("Execute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						approveNum++;
		        	}
					else
					{
						System.out.println("Execute the action according to the declined mapping."); 
			        	//System.out.println("The mapping needs to be removed.");
						rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
						//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings);
						rejectNum++;
					}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		System.out.println("Execute the action according to the declined mapping."); 
		        		//System.out.println("The mapping needs to be removed.");
		        		rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
		        		//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings);
		        		rejectNum++;
		        	}
		        	else
		        	{
		        		System.out.println("Execute the action according to the approved mapping.");  
						rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
						approveNum++;
		        	}
		        } 
				
//		        if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))
//		        { 	   		        			        	
//		            System.out.println("Execute the action according to the approved mapping.");  
//		            rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
//		            approveNum++;
//		        }
//		        else //��������չ��unknown�����
//		        {  
//		        	System.out.println("Execute the action according to the rejected mapping."); 
//		        	System.out.println("The mapping needs to be removed.");
//					//rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings);
//					rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings);
//					rejectNum++;
//		        } 				
			}
			else //�Ѿ�������mapping��
			{
				break;
			}
			//
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The automate mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		aml.removeIncorrect();		
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
	}
	
	public void AutoBasedReferenceAlignmentSimpleAutoGraphRepair()
	{
		System.out.println("Repairing Alignment");
		approveNum=0;
		rejectNum=0;
		iteration=0;	
			
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));
					
			int mappingIndex = getCandidateMappingArcByGuaranteed2(); //JWS Guaranteed function
			//int mappingIndex = getCandidateMappingArcByNormed(); //JWS normed function
			//int mappingIndex = getCandidateMappingByStructure(); //Christian Bridge rule function
			//int mappingIndex = getCandidateMappingByImpactor(); // our defined impactor factor
			//int mappingIndex = getCandidateMappingArcByImpactorandWeight(); //weight impactor factor
			if(mappingIndex != -1)
			{						
				System.out.println("The current mapping is\n"+maps.get(mappingIndex).toString());
				//��ӡmapping��context��Ϣ
				int sourceId=maps.get(mappingIndex).getSourceId();
				int targetId=maps.get(mappingIndex).getTargetId();
				MappingRelation r=maps.get(mappingIndex).getRelationship();
				//printDetailInformation(maps.get(mappingIndex));
		        if(aml.getReferenceAlignment().contains(sourceId, targetId, r))
		        //if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))	
		        { 	   		        	
		            System.out.println("Execute the action according to the approved mapping.");  
		        	if(!isUserFailing())
		        	{
		        		rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
		        		//rMap.strongApproveComplete(mappingIndex,wantMappings,unwantMappings);	
		        		approveNum++;
		        	}
		        	else
		        	{
		        		System.out.println("Execute the action according to the declined mapping.");
						//rMap.simpleReject(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
		        		rMap.strongRejectComplete(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
						//rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
						rejectNum++;
		        	}
		        }
		        else //��������չ��unknown�����
		        {  
		        	if(!isUserFailing())
		        	{
		        		System.out.println("Execute the action according to the declined mapping.");
						//rMap.simpleReject(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
						rMap.strongRejectComplete2(mappingIndex,wantMappings,unwantMappings,reliableMappings); //���ܾ���Ӱ��
						rejectNum++;
		        		
		        	}
		        	else
		        	{
		        		System.out.println("Execute the action according to the approved mapping."); 
		        		rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
		        		approveNum++;
		        	}
		        } 
		        
		       /* if(aml.getReferenceAlignment().containsMapping(sourceId, targetId))	
		        { 	   		        	
		        	System.out.println("The mapping needs to be approved.");
		        	rMap.entailBasedApprove(mappingIndex,wantMappings,unwantMappings); //Christian�� Tool��֧�ַ��� �����汾
		        	approveNum++;
		        }
		        else //��������չ��unknown�����
		        {  
		        	System.out.println("The mapping needs to be removed.");
					rMap.simpleReject(mappingIndex,wantMappings,unwantMappings); //���ܾ���Ӱ��
					rejectNum++;
		        } */
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
		}
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The manual mapping revision is finished! ");
		HashSet<Integer> filterSet=new HashSet<>();
		int approvedNum=0,rejectedNum=0;
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The approved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: wantMappings)
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				approvedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		if(wantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The rejected mappings are listed as followed:");		
		for(Mapping m:unwantMappings)
		{
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
				rejectedNum++;
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}
		if(unwantMappings.isEmpty())
			System.out.println("None");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The number of reliable mappings is "+reliableMappings.size());
//		for(Mapping m: reliableMappings)
//		{
//			System.out.println(m);
//		}
		System.out.println("The number of original mappings is "+ (approvedNum+rejectedNum));
		System.out.println("The number of repaired mappings is "+ approveNum);
		System.out.println("Your make "+ (approveNum+rejectNum) +" decisions."+" Approve: "+approveNum +" Reject: "+ rejectNum);		
		double saving=1-(approveNum+rejectNum)*1.0/(approvedNum+rejectedNum);
		BigDecimal b1 = new BigDecimal(saving);  
		saving = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()*100;	
		System.out.print("The number of saved mapping is : "+ ((approvedNum+rejectedNum)-(approveNum+rejectNum)));
		System.out.print(" 		Saving: "+ String.format("%1$.1f", saving)+"%\n");
		//aml.removeIncorrect();	
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
//		aml.removeIncorrect();		
//		maps=tempMappings;
//		aml.getAlignment().getMappingSet().retainAll(tempMappings);
//		aml.getAlignment().getMappingSet().addAll(reliableMappings);		
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		System.out.println("The number of decided mappings is "+ aml.getAlignment().getMappingSet().size());
		aml.removeIncorrect();	
		System.out.println("The number of left correct mappings is "+ aml.getAlignment().getMappingSet().size());
		aml.getAlignment().getMappingSet().addAll(reliableMappings);
		System.out.println("The number of mappings (added reliableMappings) is "+ aml.getAlignment().getMappingSet().size());
	}
	
	public void RevisedMappings()
	{
		/*if(rMap.isCoherent())
		{
			System.out.println("Alignment is coherent");
			return;
		}*/
		System.out.println("Repairing Alignment");
		long time = System.currentTimeMillis()/1000;
		int revisedCount = 0;
		int reservedCount = 0;
		int removedCount = 0;
		ArrayList<Integer> revisedMappings=new ArrayList<Integer>();
		ArrayList<Integer> reservedMappings=new ArrayList<Integer>();
		ArrayList<Integer> removedMappings=new ArrayList<Integer>();
		//Loop until no more mappings can be removed
		//buildCheckListrMap.getConflictSets();
		int iteration=0;
		while(true)
		{
			System.out.println("The iteration is "+(iteration++));
			//int worstMapping = getIncorrectMapping();
			int worstMapping = getIncorrectMapping3();
			if(worstMapping != -1)
			{
				ArrayList<Integer> indexSet=new ArrayList<Integer>();
				if(aml.getAlignment().getPropertyMap().containsKey(worstMapping))
					indexSet.addAll(aml.getAlignment().getPropertyMap().get(worstMapping));
				else
					indexSet.add(worstMapping);
				indexSet.retainAll(rMap.MapMinimalConflictSet.keySet()); //�������Ա�֤	
				worstMapping=indexSet.get(0);	
				
				System.out.println("The index of wrong mapping is: "+ worstMapping);
				System.out.println(aml.getAlignment().get(worstMapping).toString());
				if(ExistCommonEntailment4(worstMapping))  //������ڣ�worstMapping���ܶ�����
				{		
					//Revise����
					if(rMap.reviseJudge(worstMapping))
					{
						System.out.println("The mapping needs to be revised.");
						revisedMappings.add(worstMapping);
						revisedCount++;
					}
					else
					{
						System.out.println("The mapping needs to be reserved.");
						reservedMappings.add(worstMapping);
						reservedCount++;
					}			
				}
				else  //Remove����
				{	
					System.out.println("The mapping needs to be removed.");	
					//rMap.remove(worstMapping,true);
					rMap.remove2(worstMapping,true);
					removedMappings.add(worstMapping);
					removedCount++;
				}
				//������remove����
				/*rMap.remove(worstMapping,true);	
				removedMappings.add(worstMapping);
				removedCount++;*/
			}
			else //�Ѿ������ڴ����mapping��
			{
				break;
			}
			//
		}
		System.out.println("The removed mappings is listed as followed:");
		for(int m:removedMappings)
		{	
			System.out.println(aml.getAlignment().get(m).toString());
		}
		System.out.println("removed " + removedCount + " mappings");
		System.out.println("The revised mappings is listed as followed:");
		for(int m:revisedMappings)
		{	
			System.out.println(aml.getAlignment().get(m).toString() + " "+aml.getAlignment().get(m).getSourceId()+" "+aml.getAlignment().get(m).getTargetId());
		}
		System.out.println("revised " + revisedCount + " mappings");
		System.out.println("The reserved mappings is listed as followed:");
		for(int m:reservedMappings)
		{	
			System.out.println(aml.getAlignment().get(m).toString() + " "+aml.getAlignment().get(m).getSourceId()+" "+aml.getAlignment().get(m).getTargetId());
		}
		System.out.println("reserved " + reservedCount + " mappings");	
		
		
		System.out.println("Finished Repair in " + 
				(System.currentTimeMillis()/1000-time) + " seconds");
		
		HashSet<Integer> filterSet=new HashSet<>();
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("The saved mappings are listed as followed:");
		Vector<Mapping> tempMappings=new Vector<Mapping>();
		for(Mapping m: aml.getAlignment())
		{	
			int source=m.getSourceId();
			int target=m.getTargetId();
			int index=aml.getAlignment().getIndexBidirectional(source,target);
			if(!filterSet.contains(index))
			{
				//System.out.println(m.toString());
				tempMappings.add(m);
			}
			if(aml.getAlignment().getPropertyMap().containsKey(index))
				filterSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		}	
		System.out.println("--------------------------------------------------------------------------------");			
		maps=tempMappings;
		//��Ҫ��Ϊ��ˢ�½�ɫ������Ķ���mappings����
		aml.getAlignment().getMappingSet().retainAll(tempMappings);
		aml.removeIncorrect();
	}
	
	@Override
	public void flag()
	{
		System.out.println("Running Coherence Flagger");
		long time = System.currentTimeMillis()/1000;
		for(Integer i : rMap)
			if(rMap.getMapping(i).getStatus().equals(MappingStatus.UNKNOWN))
				rMap.getMapping(i).setStatus(MappingStatus.FLAGGED);
		System.out.println("Finished in " +	(System.currentTimeMillis()/1000-time) + " seconds");
	}
	
	private int randomSelect() //����MIPSs�ĸ�����mapping��Ȩ��
	{			
		int candidateMapping = -1;	
		ArrayList<Integer> randomSet=new ArrayList<Integer>();
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				int originalIndex=restoreRoleMapping(i);  //���ǵ���ɫ������Ҫ���ϴ˾�
				if(!randomSet.contains(originalIndex))
					randomSet.add(originalIndex);
			}
		}
		if(randomSet.isEmpty())
			return candidateMapping;	
		Random ra =new Random();
		int randomIndex=ra.nextInt(randomSet.size());
		candidateMapping=randomSet.get(randomIndex);
		return candidateMapping;
	}
	
	private int getCandidateMapping() //����MIPSs�ĸ�����mapping��Ȩ��
	{			
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();		
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				int card=-1;
				if(rMap.getMapMinimalConflictSets().containsKey(i))
				{
				  card = rMap.getMapMinimalConflictSets().get(i).size();
				}
				else 
				{
				  card=0;
				}
				//���ǵ��н�ɫ������Ҫ���л�ԭ(����Ǹ���Ļ����Ǿ���ͬһ������)
				int originalIndex=restoreRoleMapping(i);
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{
					card=card+CandidateMappings.get(originalIndex);
					CandidateMappings.put(originalIndex, card);	
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, card);	
				}
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
        
        //�ȿ���MIPSs���ִ�������mappings   
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
         		maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{			
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
			WeightsMapping=getminimalWeight2(CardMapping);
			candidateMapping=WeightsMapping.get(0);		
		}
		return candidateMapping;	
	}
	
	private int getCandidateMapping2()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Double> CandidateMappings=new HashMap<Integer,Double>();		
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				/*double sim = maps.get(i).getSimilarity();
				CandidateMappings.put(i, sim);*/			
				int originalIndex=restoreRoleMapping(i);
				double sim = maps.get(i).getSimilarity();
				CandidateMappings.put(originalIndex, sim);			
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //��������
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
        
        //�ȿ���MIPSs���ִ�������mappings   
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		double maxSim = 0;
		for(Entry<Integer, Double> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxSim==0)
         	{
            	maxSim=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxSim==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		candidateMapping=CardMapping.get(0);				
		return candidateMapping;	
	}
	
	private int getCandidateMappingByStructure()  //����ԭ����Ľṹ����������  Christian�� Tool��ԭʼ���Ż�����
	{	
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{	
				Mapping m=maps.get(i);
				int source=m.getSourceId();
				int target=m.getTargetId();
				int impactor=0;
				if(m.getRelationship().equals(MappingRelation.SUBCLASS))
				{
					Set<Integer> subClass1 = aml.getRelationshipMap().getSubClasses(source, true);
					Set<Integer> superClass2 = aml.getRelationshipMap().getSuperClasses(target, true);
					Set<Integer> disClass2 = aml.getRelationshipMap().getDisjoint(target);
					
					impactor=subClass1.size()*(superClass2.size()+disClass2.size());				
				}
				else if(m.getRelationship().equals(MappingRelation.SUPERCLASS))
				{
					Set<Integer> superClass1 = aml.getRelationshipMap().getSuperClasses(source, true);
					Set<Integer> subClass2 = aml.getRelationshipMap().getSubClasses(target, true);
					Set<Integer> disClass2 = aml.getRelationshipMap().getDisjoint(target);		
					
					impactor=superClass1.size()*(subClass2.size()+disClass2.size());
				}	
				else if(m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					Set<Integer> subClass1 = aml.getRelationshipMap().getSubClasses(source, true);
					Set<Integer> superClass2 = aml.getRelationshipMap().getSuperClasses(target, true);
					Set<Integer> disClass21 = aml.getRelationshipMap().getDisjoint(target);
					
					int impactor1=subClass1.size()*(superClass2.size()+disClass21.size());
					
					Set<Integer> superClass1 = aml.getRelationshipMap().getSuperClasses(source, true);
					Set<Integer> subClass2 = aml.getRelationshipMap().getSubClasses(target, true);
					Set<Integer> disClass22 = aml.getRelationshipMap().getDisjoint(target);		
					
					int impactor2=superClass1.size()*(subClass2.size()+disClass22.size());
					
					impactor=impactor1+impactor2;					
				}       
                int originalIndex=restoreRoleMapping(i);
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{							
					impactor=CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
					CandidateMappings.put(originalIndex, impactor);			
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, impactor);	
				}		
				
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 	
		candidateMapping=CardMapping.get(0);		
		return candidateMapping;	
	}
	
	private int getCandidateMappingByStructure(ArrayList<Integer> candidateSet)  //����ԭ����Ľṹ����������  Christian�� Tool��ԭʼ���Ż�����
	{	
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		for(int i=0;i<candidateSet.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(candidateSet.get(i)).getStatus().equals(MappingStatus.UNKNOWN))
			{	
				Mapping m=maps.get(candidateSet.get(i));
				int source=m.getSourceId();
				int target=m.getTargetId();
				int impactor=0;
				if(m.getRelationship().equals(MappingRelation.SUBCLASS))
				{
					Set<Integer> subClass1 = aml.getRelationshipMap().getSubClasses(source, true);
					Set<Integer> superClass2 = aml.getRelationshipMap().getSuperClasses(target, true);
					Set<Integer> disClass2 = aml.getRelationshipMap().getDisjoint(target);
					
					impactor=subClass1.size()*(superClass2.size()+disClass2.size());				
				}
				else if(m.getRelationship().equals(MappingRelation.SUPERCLASS))
				{
					Set<Integer> superClass1 = aml.getRelationshipMap().getSuperClasses(source, true);
					Set<Integer> subClass2 = aml.getRelationshipMap().getSubClasses(target, true);
					Set<Integer> disClass2 = aml.getRelationshipMap().getDisjoint(target);		
					
					impactor=superClass1.size()*(subClass2.size()+disClass2.size());
				}	
				else if(m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					Set<Integer> subClass1 = aml.getRelationshipMap().getSubClasses(source, true);
					Set<Integer> superClass2 = aml.getRelationshipMap().getSuperClasses(target, true);
					Set<Integer> disClass21 = aml.getRelationshipMap().getDisjoint(target);
					
					int impactor1=subClass1.size()*(superClass2.size()+disClass21.size());
					
					Set<Integer> superClass1 = aml.getRelationshipMap().getSuperClasses(source, true);
					Set<Integer> subClass2 = aml.getRelationshipMap().getSubClasses(target, true);
					Set<Integer> disClass22 = aml.getRelationshipMap().getDisjoint(target);		
					
					int impactor2=superClass1.size()*(subClass2.size()+disClass22.size());
					
					impactor=impactor1+impactor2;					
				}       
                int originalIndex=restoreRoleMapping(candidateSet.get(i));
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{							
					impactor=CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
					CandidateMappings.put(originalIndex, impactor);			
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, impactor);	
				}		
				
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 	
		candidateMapping=CardMapping.get(0);		
		return candidateMapping;	
	}
	
	private int getCandidateMappingByImpactor()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		HashMap<Integer,Boolean> mappingJudge=new HashMap<Integer,Boolean>();
		//int maxImpactor=0;
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			Boolean flag=true; //true��ʾ��ͬ��false��ʾ�ܾ�
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				//System.out.println(i);
				//System.out.println(maps.get(i).toString());
				int impactPlus=obtainImpactPlus(i); //��ȡ����Ӱ���ֵ
				//int impactConflict=obtainImpactConflict(i); //��ȡ��ͻӰ���ֵ
				int impactConflict=obtainImpactConflict2(i); //��ȡ��ͻӰ���ֵ
				int impactMinor=obtainImpactMinor(i);   //��ȡ����Ӱ���ֵ 
                int impactor=Math.max(impactPlus+impactConflict, impactMinor);
                if((impactPlus+impactConflict)<impactMinor) //Ĭ���Ǿܾ������
                	flag=false;         
                int originalIndex=restoreRoleMapping(i);
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{
					boolean tempflag=mappingJudge.get(originalIndex);
					if(tempflag==flag) //Ŀ��һ��,����Ϊ��ͬ���߾ܾ�
					{					
						impactor=(impactor-1)+CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
						CandidateMappings.put(originalIndex, impactor);
						mappingJudge.put(originalIndex, flag);
					}
					else 
					{
						int tempImpactor=CandidateMappings.get(originalIndex);
						if(tempImpactor<=impactor) //ԭʼ������ȵ�ǰ���Ӱ�����Ӹ���,����ԭʼ�ķ���
						{
							CandidateMappings.put(originalIndex, impactor);
							mappingJudge.put(originalIndex, flag);
							
						}
					}			
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, impactor);	
					mappingJudge.put(originalIndex, flag);
				}		
				
				mappingJudge.put(originalIndex, flag);
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{
			Boolean judge=mappingJudge.get(CardMapping.get(0)); //������ͬ���Ǿܾ�������
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
			WeightsMapping=selectCandidateMappingByWeight(CardMapping,judge); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������		
			candidateMapping=WeightsMapping.get(0);		
		}		
		return candidateMapping;	
	}
	
	private int getCandidateMappingArcByImpactorandWeight()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Double> CandidateMappings=new HashMap<Integer,Double>();	
		HashMap<Integer,Boolean> mappingJudge=new HashMap<Integer,Boolean>();
		
		//HashMap<Double,ArrayList<Integer>> weightedMappings=new HashMap<Double,ArrayList<Integer>>();

		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			Boolean flag=true; //true means to select the large weight，false means to select the small weight 
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				//System.out.println(i);
				//System.out.println(maps.get(i).toString());
				double weight=rMap.getMapping(i).getSimilarity();
				double impactPlus=weight*obtainImpactPlus(i); //��ȡ����Ӱ���ֵ
				//int impactConflict=obtainImpactConflict(i); //��ȡ��ͻӰ���ֵ
				
				double impactConflict=weight*obtainImpactConflict2(i); //��ȡ��ͻӰ���ֵ
				
				double impactMinor=weight*obtainImpactMinor(i);   //��ȡ����Ӱ���ֵ 
				
				double impactor=Math.max(impactPlus+impactConflict, impactMinor);
                if((impactPlus+impactConflict)<impactMinor) //Ĭ���Ǿܾ������
                	flag=false;     
                
                int originalIndex=restoreRoleMapping(i);
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{
					boolean tempflag=mappingJudge.get(originalIndex);
					if(tempflag==flag) //Ŀ��һ��,����Ϊ��ͬ���߾ܾ�
					{					
						//impactor=(impactor-1)+CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
						impactor=(impactor-weight)+CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
						CandidateMappings.put(originalIndex, impactor);
						mappingJudge.put(originalIndex, flag);
					}
					else 
					{
						double tempImpactor=CandidateMappings.get(originalIndex);
						if(tempImpactor<=impactor) //ԭʼ������ȵ�ǰ���Ӱ�����Ӹ���,����ԭʼ�ķ���
						{
							CandidateMappings.put(originalIndex, impactor);
							mappingJudge.put(originalIndex, flag);
						}
					}			
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, impactor);	
					mappingJudge.put(originalIndex, flag);
				}						
				mappingJudge.put(originalIndex, flag);
			}
		}
		
	
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //��������
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		double maxCard = 0;
		for(Entry<Integer, Double> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else //ranking by their weight
		{
			Boolean judge=mappingJudge.get(CardMapping.get(0)); //������ͬ���Ǿܾ�������
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
			WeightsMapping=selectCandidateMappingByWeight(CardMapping,judge); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������
			candidateMapping=WeightsMapping.get(0);		
		}		
		return candidateMapping;	
	}
	
	private int getCandidateMappingByImpactor(ArrayList<Integer> candidates)  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		HashMap<Integer,Boolean> mappingJudge=new HashMap<Integer,Boolean>();
		//int maxImpactor=0;
		for(int i=0;i<candidates.size();i++)  //����ֻ�Դ�������mappings��������
		{
			int index=candidates.get(i);
			Boolean flag=true; //true��ʾ��ͬ��false��ʾ�ܾ�
			if(maps.get(index).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				
				int impactPlus=obtainImpactPlus(index); //��ȡ����Ӱ���ֵ
				//int impactConflict=obtainImpactConflict(i); //��ȡ��ͻӰ���ֵ
				int impactConflict=obtainImpactConflict2(index); //��ȡ��ͻӰ���ֵ
				int impactMinor=obtainImpactMinor(index);   //��ȡ����Ӱ���ֵ 
                int impactor=Math.max(impactPlus+impactConflict, impactMinor);
                if((impactPlus+impactConflict)<impactMinor) //Ĭ���Ǿܾ������
                	flag=false;         
                int originalIndex=restoreRoleMapping(index);
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{
					
					boolean tempflag=mappingJudge.get(originalIndex);
					if(tempflag==flag) //Ŀ��һ��,����Ϊ��ͬ���߾ܾ�
					{					
						impactor=(impactor-1)+CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
						CandidateMappings.put(originalIndex, impactor);
						mappingJudge.put(originalIndex, flag);
					}
					else 
					{
						int tempImpactor=CandidateMappings.get(originalIndex);
						if(tempImpactor<=impactor) //ԭʼ������ȵ�ǰ���Ӱ�����Ӹ���,����ԭʼ�ķ���
						{
							CandidateMappings.put(originalIndex, impactor);
							mappingJudge.put(originalIndex, flag);
							
						}
					}			
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, impactor);	
					mappingJudge.put(originalIndex, flag);
				}		
				
				mappingJudge.put(originalIndex, flag);
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{
			Boolean judge=mappingJudge.get(CardMapping.get(0)); //������ͬ���Ǿܾ�������
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
			WeightsMapping=selectCandidateMappingByWeight(CardMapping,judge); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������		
			candidateMapping=WeightsMapping.get(0);		
		}		
		return candidateMapping;	
	}
	
	private int getCandidateMappingArcByGuaranteed()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		HashMap<Integer,Boolean> mappingJudge=new HashMap<Integer,Boolean>();
		
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			Boolean flag=true; //true��ʾ��ͬ��false��ʾ�ܾ�
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				//System.out.println(i);
				//System.out.println(maps.get(i).toString());
				int impactPlus=obtainImpactPlus(i); //��ȡ����Ӱ���ֵ
				
				//int impactConflict=obtainImpactConflict(i); //��ȡ��ͻӰ���ֵ
				int impactConflict=obtainImpactConflict2(i); //��ȡ��ͻӰ���ֵ
				
				int impactMinor=obtainImpactMinor(i);   //��ȡ����Ӱ���ֵ 
				
                int guaranteed=Math.min(impactPlus+impactConflict, impactMinor);
                
                if((impactPlus+impactConflict)<impactMinor) //flag of whether of conflicts
                	flag=false; 
                          
                int originalIndex=restoreRoleMapping(i);
                
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{				
					boolean tempflag=mappingJudge.get(originalIndex);
					if(tempflag==flag) //equal means there are two situations that are conflict
					{					
						guaranteed=(guaranteed-1)+CandidateMappings.get(originalIndex); //(calculate the impacts of conflicts)
						CandidateMappings.put(originalIndex, guaranteed);
						mappingJudge.put(originalIndex, flag);
					}
					else 
					{
						int tempImpactor=CandidateMappings.get(originalIndex);
						if(tempImpactor<=guaranteed) //ԭʼ������ȵ�ǰ���Ӱ�����Ӹ���,����ԭʼ�ķ���
						{
							CandidateMappings.put(originalIndex, guaranteed);
							mappingJudge.put(originalIndex, flag);						
						}
					}			
				}
				else //
				{
					CandidateMappings.put(originalIndex, guaranteed);	
					mappingJudge.put(originalIndex, flag);
				}						
				mappingJudge.put(originalIndex, flag);
			}
		}
		
		//ranking
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //descending order
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            } 
        	//ascending order
//            public int compare(Entry<Integer, Integer> o1,
//                    Entry<Integer, Integer> o2) {
//                return o1.getValue().compareTo(o2.getValue());
//            }  
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 

		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{
			Boolean judge=mappingJudge.get(CardMapping.get(0)); //������ͬ���Ǿܾ�������
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
//			WeightsMapping=selectCandidateMappingByWeight(CardMapping,judge); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������		
//			candidateMapping=WeightsMapping.get(0);					

			candidateMapping=randomselectCandidateMapping(CardMapping); //random select

			//candidateMapping=CardMapping.get(0);	
		}		
		return candidateMapping;	
	}
	
	private int getCandidateMappingArcByGuaranteed2()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		//HashMap<Integer,Boolean> mappingJudge=new HashMap<Integer,Boolean>();
		
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				//System.out.println(i);
				//System.out.println(maps.get(i).toString());
				int impactPlus=obtainImpactPlus(i)-1; //��ȡ����Ӱ���ֵ
				
				//int impactConflict=obtainImpactConflict(i); //��ȡ��ͻӰ���ֵ
				int impactConflict=obtainImpactConflict2(i); //��ȡ��ͻӰ���ֵ
				
				int impactMinor=obtainImpactMinor(i);   //��ȡ����Ӱ���ֵ 
				
                //int guaranteed=Math.min(impactPlus+impactConflict, impactMinor);
                int guaranteed=Math.min((impactPlus+impactConflict)/2, impactMinor);
                                         
                int originalIndex=restoreRoleMapping(i);
                
                CandidateMappings.put(originalIndex, guaranteed);                
			}
		}
		
		//ranking
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //descending order
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            } 
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 

		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
//			WeightsMapping=selectCandidateMappingByWeight(CardMapping); //selected by weights			
//			candidateMapping=WeightsMapping.get(0);		
			
			candidateMapping=randomselectCandidateMapping(CardMapping); //random select		
		}		
		return candidateMapping;	
	}
	
	private int getCandidateMappingArcByNormed()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Double> CandidateMappings=new HashMap<Integer,Double>();	
		//HashMap<Double,Integer> WeightedMappings=new HashMap<Double,Integer>();
		
		int leftMappingsNum=0;
		double validityRatio=0.75; //Measure By Number of MIPSs
		for(int i=0;i<maps.size();i++)
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{	
				leftMappingsNum++;
			}
		}
	
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			Boolean flag=true; //true��ʾ��ͬ��false��ʾ�ܾ�
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{		
				//System.out.println(i);
				//System.out.println(maps.get(i).toString());
				int impactPlus=obtainImpactPlus(i)-1; //��ȡ����Ӱ���ֵ
				
				//int impactConflict=obtainImpactConflict(i); //��ȡ��ͻӰ���ֵ
				int impactConflict=obtainImpactConflict2(i); //��ȡ��ͻӰ���ֵ
				
				int impactMinor=obtainImpactMinor(i);   //��ȡ����Ӱ���ֵ 
				
				double impactNormPlus=(impactPlus)*1.0/leftMappingsNum;
				double impactNormConflict=(impactConflict)*1.0/leftMappingsNum;
				double impactNormMinor=(impactMinor)*1.0/leftMappingsNum;
				
				double NormPlus=-Math.abs(validityRatio-impactNormPlus);
				double NormConflict=-Math.abs(1-validityRatio-impactNormConflict);
				double NormMinor=-Math.abs(1-validityRatio-impactNormMinor);
				        
                double impact=Math.max(Math.max(NormPlus,NormConflict),NormMinor);
                                         
                int originalIndex=restoreRoleMapping(i);
                
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{				
					double tempImpactor=CandidateMappings.get(originalIndex);
					if(tempImpactor<=impact) //ԭʼ������ȵ�ǰ���Ӱ�����Ӹ���,����ԭʼ�ķ���
					{
					  CandidateMappings.put(originalIndex, impact);
					}						
				}
				else //
				{
					  CandidateMappings.put(originalIndex, impact);	
				}						
			}
		}
		
		//ranking
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //��������
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		double maxCard = 0.0;
		for(Entry<Integer, Double> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{
			//Boolean judge=mappingJudge.get(CardMapping.get(0)); //������ͬ���Ǿܾ�������
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
			//WeightsMapping=selectCandidateMappingByWeight(CardMapping,judge); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������	
//			WeightsMapping=selectCandidateMappingByWeight(CardMapping); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������	
//			candidateMapping=WeightsMapping.get(0);		
			
			candidateMapping=randomselectCandidateMapping(CardMapping); //random select
			//candidateMapping=CardMapping.get(0); //select first one

		}		
		return candidateMapping;	
	}
	
	private int getCandidateMappingByStructureAndImpactor()  //������Ȩ�ص�������
	{			
		int candidateMapping = -1;
		HashMap<Integer,Integer> CandidateMappings=new HashMap<Integer,Integer>();	
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{	
				Mapping m=maps.get(i);
				int source=m.getSourceId();
				int target=m.getTargetId();
				int impactor=0;
				if(m.getRelationship().equals(MappingRelation.SUBCLASS))
				{
					Set<Integer> subClass1 = aml.getRelationshipMap().getSubClasses(source, true);
					Set<Integer> superClass2 = aml.getRelationshipMap().getSuperClasses(target, true);
					Set<Integer> disClass2 = aml.getRelationshipMap().getDisjoint(target);
					
					impactor=subClass1.size()*(superClass2.size()+disClass2.size());				
				}
				else if(m.getRelationship().equals(MappingRelation.SUPERCLASS))
				{
					Set<Integer> superClass1 = aml.getRelationshipMap().getSuperClasses(source, true);
					Set<Integer> subClass2 = aml.getRelationshipMap().getSubClasses(target, true);
					Set<Integer> disClass2 = aml.getRelationshipMap().getDisjoint(target);		
					
					impactor=superClass1.size()*(subClass2.size()+disClass2.size());
				}	
				else if(m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					Set<Integer> subClass1 = aml.getRelationshipMap().getSubClasses(source, true);
					Set<Integer> superClass2 = aml.getRelationshipMap().getSuperClasses(target, true);
					Set<Integer> disClass21 = aml.getRelationshipMap().getDisjoint(target);
					
					int impactor1=subClass1.size()*(superClass2.size()+disClass21.size());
					
					Set<Integer> superClass1 = aml.getRelationshipMap().getSuperClasses(source, true);
					Set<Integer> subClass2 = aml.getRelationshipMap().getSubClasses(target, true);
					Set<Integer> disClass22 = aml.getRelationshipMap().getDisjoint(target);		
					
					int impactor2=superClass1.size()*(subClass2.size()+disClass22.size());
					
					impactor=impactor1+impactor2;					
				}       
                int originalIndex=restoreRoleMapping(i);
				if(CandidateMappings.containsKey(originalIndex)) //��ɫ�����
				{							
					impactor=CandidateMappings.get(originalIndex); //(impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
					CandidateMappings.put(originalIndex, impactor);			
				}
				else //��������
				{
					CandidateMappings.put(originalIndex, impactor);	
				}		
				
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
              
        ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
            	maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 	
		
		if(CardMapping.size()==1)
		{
			candidateMapping=CardMapping.get(0);		
		}
		else
		{
			candidateMapping=getCandidateMappingByImpactor(CardMapping);	
		}			
		return candidateMapping;	
	}
	
	
	
	
	private int getCandidateMappingByVadidityRatio()  //����JWS2012�ĵ�impactor������������
	{				
		double validatityRatio=0.75;
		if(iteration!=1) //�����жϵ���������Զ�����
			validatityRatio=wantMappings.size()*1.0/(wantMappings.size()+unwantMappings.size());		
		int candidateMapping = -1;
		HashMap<Integer,Double> CandidateMappings=new HashMap<Integer,Double>();	
		double sum=0;
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
				sum++;
		}
			
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{					
				double impactPlus=obtainImpactPlus(i)/sum; //��ȡ����Ӱ���ֵ
				double impactConflict=obtainImpactConflict(i)/sum; //��ȡ��ͻӰ���ֵ
				double impactMinor=obtainImpactMinor(i)/sum;   //��ȡ����Ӱ���ֵ 
               
				//��ȡ��׼����ֵ������������
				double normImpactPlus=-Math.abs(validatityRatio-impactPlus);
				BigDecimal b1 = new BigDecimal(normImpactPlus);  
				normImpactPlus = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				double normImpactConflict=-Math.abs(1-validatityRatio-impactConflict);
				BigDecimal b2 = new BigDecimal(normImpactConflict);  
				normImpactConflict = b2.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				double normImpactMinor=-Math.abs(1-validatityRatio-impactMinor);
				BigDecimal b3 = new BigDecimal(normImpactMinor);  
				normImpactMinor = b3.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			
				//norm����һ��ƫ��Ĵ�С�ĺ���
				double norm=Math.max(normImpactPlus, Math.max(normImpactConflict, normImpactMinor));
							
				CandidateMappings.put(i, norm);	
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //��������
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
        
        
        ArrayList<Integer> impactorMapping=new ArrayList<Integer>();
		double impactor = 0;
		for(Entry<Integer, Double> mapping:list)
		{ 
            int key=mapping.getKey();
            if(impactor==0)
         	{
            	impactor=mapping.getValue();
            	impactorMapping.add(key);
         	}
            else if(impactor==mapping.getValue())
            	impactorMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		//�Ƿ��������ƶȽ�����չ(�������Ļ��������Ļ��ǲ�̫��˵)
		candidateMapping=impactorMapping.get(0);		
		return candidateMapping;	
	}
	
	private int getCandidateMappingByVadidityRatio2()  //����JWS2012�ĵ�impactor������������
	{				
		double validatityRatio=0.5;
		HashMap<Integer,Boolean> mappingJudge=new HashMap<Integer,Boolean>();
		if(iteration!=1) //�����жϵ���������Զ�����
			validatityRatio=wantMappings.size()*1.0/(wantMappings.size()+unwantMappings.size());		
		int candidateMapping = -1;
		HashMap<Integer,Double> CandidateMappings=new HashMap<Integer,Double>();	
		double sum=0;
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
				sum++;
		}
		
		//����ƫ���뷴��ƫ��
		for(int i=0;i<maps.size();i++)  //����ֻ�Դ�������mappings��������
		{
			boolean flag=true;
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{					
				double impactPlus=obtainImpactPlus(i)/sum; //��ȡ����Ӱ���ֵ
				double impactConflict=obtainImpactConflict(i)/sum; //��ȡ��ͻӰ���ֵ
				double impactMinor=obtainImpactMinor(i)/sum;   //��ȡ����Ӱ���ֵ 
               
				//��ȡ��׼����ֵ������������
				double normImpactPlus=-Math.abs(validatityRatio-impactPlus);
				BigDecimal b1 = new BigDecimal(normImpactPlus);  
				normImpactPlus = b1.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				double normImpactConflict=-Math.abs(1-validatityRatio-impactConflict);
				BigDecimal b2 = new BigDecimal(normImpactConflict);  
				normImpactConflict = b2.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
				double normImpactMinor=-Math.abs(1-validatityRatio-impactMinor);
				BigDecimal b3 = new BigDecimal(normImpactMinor);  
				normImpactMinor = b3.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			
				//norm����һ��ƫ��Ĵ�С�ĺ���
				double norm=Math.max(normImpactPlus, Math.max(normImpactConflict, normImpactMinor));
				if(norm==normImpactPlus||norm==normImpactConflict)
					flag=true;
				else
					flag=false;
							
				CandidateMappings.put(i, norm);	
				mappingJudge.put(i, flag);
			}
		}
		if(CandidateMappings.isEmpty())
			return -1;
		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(CandidateMappings.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //��������
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
        
        
        ArrayList<Integer> impactorMapping=new ArrayList<Integer>();
		double impactor = 0;
		for(Entry<Integer, Double> mapping:list)
		{ 
            int key=mapping.getKey();
            if(impactor==0)
         	{
            	impactor=mapping.getValue();
            	impactorMapping.add(key);
         	}
            else if(impactor==mapping.getValue())
            	impactorMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		
		//�Ƿ��������ƶȽ�����չ(�������Ļ��������Ļ��ǲ�̫��˵)
		//candidateMapping=impactorMapping.get(0);	
		if(impactorMapping.size()==1)
		{
			candidateMapping=impactorMapping.get(0);		
		}
		else
		{
			Boolean judge=mappingJudge.get(impactorMapping.get(0)); //������ͬ���Ǿܾ�������
			ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
			WeightsMapping=selectCandidateMappingByWeight(impactorMapping,judge); //���ݾܾ�������ͬ����������ķ�ʽ,��ͬ���ǽ��򣬾ܾ���������		
			candidateMapping=WeightsMapping.get(0);		
		}		
		return candidateMapping;		
	}
	

	/*private int getIncorrectMapping()
	{			
		int worstMapping = -1;
		int maxCard = 0;	
		HashMap<Integer,ArrayList<Integer>> CardMapping=new HashMap<Integer,ArrayList<Integer>>();
		for(Integer map: rMap.getMapMinimalConflictSets().keySet())
		{		
			int card = rMap.getMapMinimalConflictSets().get(map).size();
			if(card > maxCard )			
			{
				maxCard = card;			
			}
			if(CardMapping.keySet().contains(card))
			{
				CardMapping.get(card).add(map);
			}
			else
			{
				ArrayList<Integer> list=new ArrayList<Integer>();
				list.add(map);
				CardMapping.put(card, list);
			}
		}
		if(maxCard==0) //no MIPPs
			return -1;
			
		ArrayList<Integer> maxCardMapping=new ArrayList<Integer>();
		maxCardMapping=CardMapping.get(maxCard);
		if(maxCardMapping.size()==1)
		{
			worstMapping=maxCardMapping.get(0);		
		}
		else
		{
			ArrayList<Integer> commonEntailment=new ArrayList<Integer>();
			commonEntailment=getMinimalEntailment(maxCardMapping);
			if(commonEntailment.size()==1)
				worstMapping=commonEntailment.get(0);
			else
			{
				ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
				WeightsMapping=getminimalWeight(commonEntailment);
				worstMapping=WeightsMapping.get(0);
			}
		}
		return worstMapping;
	}*/
	
	private int getIncorrectMapping2()
	{	
		int worstMapping = -1;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for(Integer mappings: rMap.getMapMinimalConflictSets().keySet())
		{		
			map.put(mappings,rMap.getMapMinimalConflictSets().get(mappings).size());	
		}
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
		
		ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		 for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
         		maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		if(maxCard==0) //no MIPPs
			return -1;
		if(CardMapping.size()==1)
		{
			worstMapping=CardMapping.get(0);		
		}
		else
		{
			ArrayList<Integer> commonEntailment=new ArrayList<Integer>();
			commonEntailment=getMinimalEntailment2(CardMapping);
			if(commonEntailment.size()==1)
				worstMapping=commonEntailment.get(0);
			else
			{
				ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
				WeightsMapping=getminimalWeight2(commonEntailment);
				worstMapping=WeightsMapping.get(0);
			}
		}
		return worstMapping;
	}
	
	private int getIncorrectMapping3()
	{	
		int worstMapping = -1;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for(Integer mappings: rMap.getMapMinimalConflictSets().keySet())  //����ֻ�Դ�������mappings��������
		{		
			System.out.println("The Id is "+mappings+" "+maps.get(mappings).toString());
			int impactConflict = rMap.getMapMinimalConflictSets().get(mappings).size(); // ��ȡ��ͻӰ���ֵ
			int originalIndex = restoreRoleMapping(mappings);
			if (map.containsKey(originalIndex)) // ��ɫ�����
			{
				int impactor = impactConflict + map.get(originalIndex); // (impactor-1��Ϊ�˷�ֹ��ͬ������ɫ���ۼ�)
				map.put(originalIndex, impactor);
			} 
			else 
			{
				/*if(mappings!=originalIndex) //��ɫ���������
					impactConflict++;*/
				map.put(originalIndex, impactConflict);
			}
		}
		
	
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }         
        });
		
		ArrayList<Integer> CardMapping=new ArrayList<Integer>();
		int maxCard = 0;
		 for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(maxCard==0)
         	{
         		maxCard=mapping.getValue();
         		CardMapping.add(key);
         	}
            else if(maxCard==mapping.getValue())
        		CardMapping.add(key);
        	else 
        	{
				break;
			}     
        } 
		if(maxCard==0) //no MIPPs
			return -1;
		if(CardMapping.size()==1)
		{
			worstMapping=CardMapping.get(0);		
		}
		else
		{
			ArrayList<Integer> commonEntailment=new ArrayList<Integer>();
			commonEntailment=getMinimalEntailment3(CardMapping);
			if(commonEntailment.size()==1)
				worstMapping=commonEntailment.get(0);
			else
			{
				ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
				WeightsMapping=getminimalWeight2(commonEntailment);
				worstMapping=WeightsMapping.get(0);
			}
		}
		return worstMapping;
	}
	
	/*private ArrayList<Integer> getMinimalEntailment(ArrayList<Integer> maxCardMapping) 
	{
		int commonNum=Integer.MAX_VALUE;
		HashMap<Integer,ArrayList<Integer>> CommonMapping=new HashMap<Integer,ArrayList<Integer>>();
		for(Integer map:maxCardMapping)
		{
			int num=0;
			for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp:rMap.getMapMinimalConflictSets().get(map))
			{
				//HashMap<Path, Set<Integer>> commonEntailment=getCommonEntailment(mipp);
				//HashMap<Path, Set<Integer>> commonEntailment=mipp.getCommonEntailment();
				for(Path path:commonEntailment.keySet())
				{
					if(path.contains(map))
						num=num+commonEntailment.get(path).size();
				}
			}
			if(commonNum>num)
			{
				commonNum=num;
			}
			if(CommonMapping.keySet().contains(num))
			{
				CommonMapping.get(num).add(map);
			}
			else
			{
				ArrayList<Integer> list=new ArrayList<Integer>();
				list.add(map);
				CommonMapping.put(num, list);
			}			
		}
		ArrayList<Integer> commonEntailmentMappping=new ArrayList<Integer>();
		commonEntailmentMappping=CommonMapping.get(commonNum);
		return commonEntailmentMappping;
	}*/
	
	private ArrayList<Integer> getMinimalEntailment2(ArrayList<Integer> maxCardMapping) 
	{
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();	
		for(Integer mappings:maxCardMapping)
		{
			int num=0;
			for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp:rMap.getMapMinimalConflictSets().get(mappings))
			{
				/*HashMap<Integer, Set<Integer>> commonEntailment=mipp.getCommonEntailment();
				for(Integer path:commonEntailment.keySet())
				{
					if(path==mappings)
						num=num+commonEntailment.get(path).size();
				}*/
				HashMap<Integer, Integer> commonEntailment=mipp.getCommonEntailment();
				for(Integer path:commonEntailment.keySet())
				{
					if(path==mappings)
						num=num+commonEntailment.get(path);
				}
			}
			map.put(mappings, num);
		}
		
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }         
        });
		
		ArrayList<Integer> commonEntailmentMappping=new ArrayList<Integer>();
		int commonNum = -1;
		 for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(commonNum==-1)
         	{
            	commonNum=mapping.getValue();
         		commonEntailmentMappping.add(key);
         	}
            else if(commonNum==mapping.getValue())
            	commonEntailmentMappping.add(key);
        	else 
        	{
				break;
			}     
        }
		return commonEntailmentMappping;
	}
	
	private ArrayList<Integer> getMinimalEntailment3(ArrayList<Integer> maxCardMapping) 
	{
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();	
		for(Integer mappings:maxCardMapping)
		{
			HashSet<Integer> indexSet=new HashSet<Integer>();
			if(aml.getAlignment().getPropertyMap().containsKey(mappings))
				indexSet.addAll(aml.getAlignment().getPropertyMap().get(mappings));
			else
				indexSet.add(mappings);
			indexSet.retainAll(rMap.MapMinimalConflictSet.keySet()); //�������Ա�֤
			int num=0;
			for(Integer in:indexSet)
			{
				for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp:rMap.getMapMinimalConflictSets().get(in))
				{
					HashMap<Integer, Integer> commonEntailment=mipp.getCommonEntailment();
					for(Integer path:commonEntailment.keySet())
					{
						if(path==mappings)
							num=num+commonEntailment.get(path);
					}
				}
			}
			map.put(mappings, num);
		}
		
		List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
            //��������
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }         
        });
		
		ArrayList<Integer> commonEntailmentMappping=new ArrayList<Integer>();
		int commonNum = -1;
		 for(Entry<Integer, Integer> mapping:list)
		{ 
            int key=mapping.getKey();
            if(commonNum==-1)
         	{
            	commonNum=mapping.getValue();
         		commonEntailmentMappping.add(key);
         	}
            else if(commonNum==mapping.getValue())
            	commonEntailmentMappping.add(key);
        	else 
        	{
				break;
			}     
        }
		return commonEntailmentMappping;
	}
	
	private ArrayList<Integer> getminimalWeight(ArrayList<Integer> commonEntailment) 
	{
		double minWeight=Integer.MAX_VALUE;
		HashMap<Double,ArrayList<Integer>> weightMapping=new HashMap<Double,ArrayList<Integer>>();
		for(Integer map:commonEntailment)
		{
			double weight=rMap.getMapping(map).getSimilarity();
			if(minWeight>weight)
			{
				minWeight=weight;
			}
			if(weightMapping.keySet().contains(weight))
			{
				weightMapping.get(weight).add(map);
			}
			else
			{
				ArrayList<Integer> list=new ArrayList<Integer>();
				list.add(map);
				weightMapping.put(weight, list);
			}	
		}
		ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
		WeightsMapping=weightMapping.get(minWeight);
		return WeightsMapping;		
	}
	
	private ArrayList<Integer> getminimalWeight2(ArrayList<Integer> commonEntailment) 
	{
		Map<Integer, Double> map = new TreeMap<Integer, Double>();
		for(Integer mappings:commonEntailment)
		{
			double weight=rMap.getMapping(mappings).getSimilarity();
			map.put(mappings, weight);
		}
		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
        //Ȼ��ͨ���Ƚ�����ʵ������
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //��������
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }         
        });
		
		ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
		double minWeight = -1;
		 for(Entry<Integer, Double> mapping:list)
		{ 
            int key=mapping.getKey();
            if(minWeight==-1)
         	{
            	minWeight=mapping.getValue();
            	WeightsMapping.add(key);
         	}
            else if(minWeight==mapping.getValue())
            	WeightsMapping.add(key);
        	else 
        	{
				break;
			}     
        }
		return WeightsMapping;		
	}
	
	private Integer randomselectCandidateMapping(ArrayList<Integer> CardMapping) 
	{
		int candidateNum=-1;
		
		Random ra =new Random();
		
		int randomIndex=ra.nextInt(CardMapping.size());
		candidateNum=CardMapping.get(randomIndex);
		
		return candidateNum;
	
	}
	
	private ArrayList<Integer> selectCandidateMappingByWeight(ArrayList<Integer> CardMapping) 
	{
		Map<Integer, Double> map = new TreeMap<Integer, Double>();
		for(Integer mappings:CardMapping)
		{
			double weight=rMap.getMapping(mappings).getSimilarity();
			map.put(mappings, weight);
		}

		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
		
		Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() 
		{// ��������
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) 
			{
					return o2.getValue().compareTo(o1.getValue());
			}
		});

		
		ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
		
		double Weight = -1;
		for (Entry<Integer, Double> mapping : list) 
		{
			int key = mapping.getKey();
			if (Weight == -1) {
				Weight = mapping.getValue();
				WeightsMapping.add(key);
			} 
			else if (Weight == mapping.getValue())
				WeightsMapping.add(key);
			else {
			break;
			}
		}
		return WeightsMapping;		
	}
	
	private ArrayList<Integer> selectCandidateMappingByWeight(ArrayList<Integer> CardMapping,Boolean Judge) 
	{
		Map<Integer, Double> map = new TreeMap<Integer, Double>();
		for(Integer mappings:CardMapping)
		{
			double weight=rMap.getMapping(mappings).getSimilarity();
			map.put(mappings, weight);
		}

		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
	
        //Ȼ��ͨ���Ƚ�����ʵ������
		if(Judge==false)
		{
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() {// descending order
						public int compare(Entry<Integer, Double> o1,
								Entry<Integer, Double> o2) {
							return o1.getValue().compareTo(o2.getValue());
						}
					});
		}
		else {
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() {// ascending order
				public int compare(Entry<Integer, Double> o1,
						Entry<Integer, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
		}
		
		ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
		
		double Weight = -1;
		for (Entry<Integer, Double> mapping : list) 
		{
			int key = mapping.getKey();
			if (Weight == -1) {
				Weight = mapping.getValue();
				WeightsMapping.add(key);
			} 
			else if (Weight == mapping.getValue())
				WeightsMapping.add(key);
			else {
			break;
			}
		}
		return WeightsMapping;		
	}
	
	private ArrayList<Integer> selectCandidateMappingByWeightPlus(ArrayList<Integer> CardMapping,int maxcard, Boolean Judge) 
	{
		Map<Integer, Double> map = new TreeMap<Integer, Double>();
		for(Integer mappings:CardMapping)
		{
			double weight=rMap.getMapping(mappings).getSimilarity();
			map.put(mappings, weight);
		}

		List<Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
		
		
        //Ȼ��ͨ���Ƚ�����ʵ������
		if(Judge==false)
		{
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() {// descending order
						public int compare(Entry<Integer, Double> o1,
								Entry<Integer, Double> o2) {
							return o1.getValue().compareTo(o2.getValue());
						}
					});
		}
		else {
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() {// ascending order
				public int compare(Entry<Integer, Double> o1,
						Entry<Integer, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
		}
		
		ArrayList<Integer> WeightsMapping=new ArrayList<Integer>();
		
		double Weight = -1;
		for (Entry<Integer, Double> mapping : list) 
		{
			int key = mapping.getKey();
			if (Weight == -1) {
				Weight = mapping.getValue();
				WeightsMapping.add(key);
			} 
			else if (Weight == mapping.getValue())
				WeightsMapping.add(key);
			else {
			break;
			}
		}
		return WeightsMapping;		
	}
	
	
	/*private boolean ExistCommonEntailment(int mapping)
	{
		int num=0;
		boolean flag=true;
		for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp: rMap.getMapMinimalConflictSets().get(mapping))
		{
			HashMap<Path, Set<Integer>> commonEntailment=mipp.getCommonEntailment();
			boolean hasEntailments=false;
			for(Path path:commonEntailment.keySet())
			{
				if(path.contains(mapping))
				{
					hasEntailments=true;
					num=num+commonEntailment.get(path).size();
				}
			}	
			flag=flag&&hasEntailments;  //��ʵ��������Ƚϲ���
			if(!flag)
				break;
		}	
		return flag;
	}*/
	
	private boolean ExistCommonEntailment2(int mapping)
	{
		int num=0;
		boolean flag=true;
		for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp: rMap.getMapMinimalConflictSets().get(mapping))
		{
			/*HashMap<Integer, Set<Integer>> commonEntailment=mipp.getCommonEntailment();
			boolean hasEntailments=false;
			if(commonEntailment.keySet().contains(mapping))  //��ʵ��������Ƚϲ���
			{
				hasEntailments=true;
			}*/
			
			HashMap<Integer, Integer> commonEntailment=mipp.getCommonEntailment();
			boolean hasEntailments=false;
			if(commonEntailment.keySet().contains(mapping))  //��ʵ��������Ƚϲ���
			{
				hasEntailments=true;
			}
			flag=flag&&hasEntailments; 
			if(!flag)
				break;
		}	
		return flag;
	}
	
	private boolean ExistCommonEntailment3(int mapping)
	{
		int num=0;	
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(mapping))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(mapping));
		else
			indexSet.add(mapping);
		indexSet.retainAll(rMap.MapMinimalConflictSet.keySet()); //�������Ա�֤	
		for(int index:indexSet)
		{
			for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp: rMap.getMapMinimalConflictSets().get(index))
			{
				HashMap<Integer, Integer> commonEntailment=mipp.getCommonEntailment();
				if(commonEntailment.keySet().contains(index))  //��ʵ��������Ƚϲ���
				{
					num++;
				}

			}
			if(num!=0)
				return true;			
		}
		return false;
	}
	
	private boolean ExistCommonEntailment4(int mapping)
	{
		int num=0;
		HashSet<Integer> indexSet=new HashSet<Integer>();
		boolean flag=true;
		if(aml.getAlignment().getPropertyMap().containsKey(mapping))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(mapping));
		else
			indexSet.add(mapping);
		indexSet.retainAll(rMap.MapMinimalConflictSet.keySet()); //�������Ա�֤	
		for(int index:indexSet)
		{
		for(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp: rMap.getMapMinimalConflictSets().get(index))
		{

			
			HashMap<Integer, Integer> commonEntailment=mipp.getCommonEntailment();
			boolean hasEntailments=false;
			if(commonEntailment.keySet().contains(index))  //��ʵ��������Ƚϲ���
			{
				hasEntailments=true;
			}
			flag=flag&&hasEntailments; 
			if(!flag)
				break;
		}	
		}
		return flag;
	}
	
	
	
	/*public HashMap<Path, Set<Integer>> getCommonEntailment(Pair<ArrayList<Integer>, ArrayList<Integer>> mipp)
	{
		HashMap<Path, Integer> conflictTailIndex1=new HashMap<Path, Integer>();
		HashMap<Path, Integer> conflictTailIndex2=new HashMap<Path, Integer>();
		for(int i=0;i<mipp.left.size()-1;i++)
		{
			int index=-1;
			int node1=mipp.left.get(i);
			int node2=mipp.left.get(i+1);		
			index=aml.getAlignment().getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				conflictTailIndex1.put(new Path(index),node2);
			}
		}
		
		for(int i=0;i<mipp.right.size()-1;i++)
		{
			int index=-1;
			int node1=mipp.right.get(i);
			int node2=mipp.right.get(i+1);		
			index=aml.getAlignment().getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				conflictTailIndex2.put(new Path(index),node2);
			}
		}
		
		HashMap<Path, Set<Integer>> commonEntailment=new HashMap<Path, Set<Integer>>();
		for(Path path1:conflictTailIndex1.keySet())
		{
			int tail1=conflictTailIndex1.get(path1);
			Set<Integer> ancestor1=aml.getRelationshipMap().getSuperClasses(tail1,false);
			for(Path path2:conflictTailIndex2.keySet())
			{
				int tail2=conflictTailIndex2.get(path2);
				Set<Integer> ancestor2=aml.getRelationshipMap().getSuperClasses(tail2,false);
				ancestor2.retainAll(ancestor1);
				if(!ancestor2.isEmpty())
				{
					commonEntailment.put(path1, ancestor2);
					commonEntailment.put(path2, ancestor2);
				}			
			}
		}
		return commonEntailment;
	}*/
	
	public Integer restoreRoleMapping(Integer index)
	{
		Mapping m=maps.get(index);
		String sourceString=m.getSourceURI();
		String targetString=m.getTargetURI();
		sourceString=sourceString.replace("exist_", "").replace("inverse_", "");
		targetString=targetString.replace("exist_", "").replace("inverse_", "");
		
		int sourceId=aml.getURIMap().getIndex(sourceString);
		int targetId=aml.getURIMap().getIndex(targetString);
		
		int originalIndex=aml.getAlignment().getIndexBidirectional(sourceId, targetId);		
		return originalIndex;	
	}
	
	public int obtainImpactPlus(Integer index)
	{
		Mapping m = aml.getAlignment().get(index);
		int source = m.getSourceId();
		int target = m.getTargetId();
		MappingRelation r=null;  //����Ĺ�ϵ�Ǵ��ڶ�Ӧ�����,����ǰmapping��ʲô��ϵ����ôӰ����mappingҲӦ��ͬ���Ĺ�ϵ
		int num=0;
		
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.SUPERCLASS))
		{
			r=m.getRelationship();
			Set<Integer> descendant1 = aml.getRelationshipMap().getSubClasses(source, false);
			Set<Integer> ancestor2 = aml.getRelationshipMap().getSuperClasses(target, false);
			descendant1.add(source);
			ancestor2.add(target);
			Set<Integer> set1= getRelatedMappingsNum(descendant1, ancestor2, r);

			Set<Integer> ancestor1 = aml.getRelationshipMap().getSuperClasses(source, false);
			Set<Integer> descendant2 = aml.getRelationshipMap().getSubClasses(target, false);
			ancestor1.add(source);
			descendant2.add(target);
			Set<Integer> set2 = getRelatedMappingsNum(ancestor1, descendant2, r);	
			set1.addAll(set2);
			num=set1.size();
		}
		else if(m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			r=MappingRelation.SUBCLASS;
			Set<Integer> descendant1 = aml.getRelationshipMap().getSubClasses(source, false);
			Set<Integer> ancestor2 = aml.getRelationshipMap().getSuperClasses(target, false);
			descendant1.add(source);
			ancestor2.add(target);			
			Set<Integer> set1 = getRelatedMappingsNum(descendant1, ancestor2, r);

			Set<Integer> ancestor1 = aml.getRelationshipMap().getSuperClasses(source, false);
			Set<Integer> descendant2 = aml.getRelationshipMap().getSubClasses(target, false);
			ancestor1.add(source);
			descendant2.add(target);
			Set<Integer> set2 = getRelatedMappingsNum(ancestor1, descendant2, r);	
			
			r=MappingRelation.SUPERCLASS;
			Set<Integer> set3 = getRelatedMappingsNum(descendant1, ancestor2, r);
			Set<Integer> set4 = getRelatedMappingsNum(ancestor1, descendant2, r);	
			set1.addAll(set2);
			set1.addAll(set3);
			set1.addAll(set4);
			num=set1.size();		
		}	
		return num;
	}

	public int obtainImpactConflict(Integer index) //����ʵ�ֵ��Ǽ򻯵İ汾��ͳ��MIPS��ͻ���������Ϊ���ӵİ汾�漰���հ�����Ҫһֱ����
	{
		int num=0;
		HashSet<Set<Integer>> conflictSet=new HashSet<Set<Integer>>();
		if(rMap.MapMinimalConflictSet.keySet().contains(index))
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(rMap.MapMinimalConflictSet.get(index));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) 
			{
				// ͨ��·������ȡmappings(�����mappingӦ�þ�����δ��ǵ�,��Ϊ����ִ��approve����reject������mapping��Ӧ��MIPS���ᱻ����)
				Set<Integer> mappings = rMap.getMappings(mipp.left, mipp.right);
				if (rMap.existRejectiveMapping(mappings, wantMappings, index)) // ������Խ��wantmapping��������ͻ�����
				{
					num++;
				}
			}
		}		
		return num;
	}
	
	public int obtainImpactConflict2(Integer index) //����ʵ�ֵ��Ǽ򻯵İ汾��ͳ��MIPS��ͻ���������Ϊ���ӵİ汾�漰���հ�����Ҫһֱ����
	{
		//int num=0;
		HashSet<Set<Integer>> conflictSet=new HashSet<Set<Integer>>();
		if(rMap.MapMinimalConflictSet.keySet().contains(index))
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(rMap.MapMinimalConflictSet.get(index));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) 
			{
				// ͨ��·������ȡmappings(�����mappingӦ�þ�����δ��ǵ�,��Ϊ����ִ��approve����reject������mapping��Ӧ��MIPS���ᱻ����)
				Set<Integer> mappings = rMap.getMappings(mipp.left, mipp.right);
				if (rMap.existRejectiveMapping(mappings, wantMappings, index)) // ������Խ��wantmapping��������ͻ�����
				{
					conflictSet.add(mappings);
					//num++;
				}
			}
		}		
		return conflictSet.size();
	}
	
	public int obtainImpactMinor(Integer index)
	{
		Mapping m = aml.getAlignment().get(index);
		int source = m.getSourceId();
		int target = m.getTargetId();
		MappingRelation r=MappingRelation.EQUIVALENCE;  //����Ĺ�ϵ�Ǵ��ڶ�Ӧ�����,����ǰmapping��ʲô��ϵ����ôӰ����mappingҲӦ��ͬ���Ĺ�ϵ
		int num=0;
				
		boolean flag=false;
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS))
		{
			r=MappingRelation.SUBCLASS;
			flag=true;
		}
		else if(m.getRelationship().equals(MappingRelation.SUBCLASS))
		{
			r=MappingRelation.SUPERCLASS;
			flag=true;
		}
		
		if(flag)
		{
			Set<Integer> descendant1=aml.getRelationshipMap().getSubClasses(source,false);
			Set<Integer> ancestor2=aml.getRelationshipMap().getSuperClasses(target,false);		
			descendant1.add(source);
			ancestor2.add(target);	
			Set<Integer> set1=getRelatedMappingsNum(descendant1,ancestor2,r);
			
			Set<Integer> ancestor1=aml.getRelationshipMap().getSuperClasses(source,false);
			Set<Integer> descendant2=aml.getRelationshipMap().getSubClasses(target,false);	
			ancestor1.add(source);
			descendant2.add(target);	
			Set<Integer> set2=getRelatedMappingsNum(ancestor1,descendant2,r);
			set1.addAll(set2);
			num=set1.size();
		}
		else 
		{
			r=MappingRelation.SUBCLASS;
			Set<Integer> descendant1=aml.getRelationshipMap().getSubClasses(source,false);
			Set<Integer> ancestor2=aml.getRelationshipMap().getSuperClasses(target,false);	
			descendant1.add(source);
			ancestor2.add(target);
			Set<Integer> set1=getRelatedMappingsNum(descendant1,ancestor2,r);
			
			Set<Integer> ancestor1=aml.getRelationshipMap().getSuperClasses(source,false);
			Set<Integer> descendant2=aml.getRelationshipMap().getSubClasses(target,false);		
			ancestor1.add(source);
			descendant2.add(target);
			Set<Integer> set2=getRelatedMappingsNum(ancestor1,descendant2,r);
			
			r=MappingRelation.SUPERCLASS;
			Set<Integer> set3 = getRelatedMappingsNum(descendant1, ancestor2, r);
			Set<Integer> set4 = getRelatedMappingsNum(ancestor1, descendant2, r);	
			set1.addAll(set2);
			set1.addAll(set3);
			set1.addAll(set4);
			
			num=set1.size();			
		}			
		return num;
	}
	
	public Set<Integer> getRelatedMappingsNum(Set<Integer> set1, Set<Integer> set2, MappingRelation r)
	{
		Set<Integer> mappingSet=new HashSet<Integer>();
		for(Integer node1:set1)
		{
			for(Integer node2:set2)
			{
				int mapping=aml.getAlignment().getIndexBidirectional(node1, node2);	
				if(mapping!=-1&&maps.get(mapping).getStatus().equals(MappingStatus.UNKNOWN))
				{
					Mapping m = aml.getAlignment().get(mapping);
					if(m.getRelationship().equals(r)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
						mappingSet.add(mapping);
				}
			}
		}
		return mappingSet;
	}
	
	public void printDetailInformation(Mapping m)
	{
		int sourceId=m.getSourceId();
		int targetId=m.getTargetId();
		if(aml.getSource().getClasses().contains(sourceId))
		{
			Set<Integer> parent1=aml.getRelationshipMap().getParents(sourceId);
			Set<Integer> children1=aml.getRelationshipMap().getChildren(sourceId);
							
			System.out.println("The context of "+aml.getURIMap().getLocalName(sourceId)+" are listed as follow");
			System.out.print("The parents are: ");
			if(parent1.isEmpty())
				System.out.print("None");
			for(Integer par: parent1)
			{
				System.out.print(aml.getURIMap().getURI(par).replace(aml.getSource().getURI()+"#", "")+" ");				
			}
			System.out.println();
			System.out.print("The children are: ");
			if(children1.isEmpty())
				System.out.print("None");
			for(Integer child: children1)
			{
				System.out.print(aml.getURIMap().getURI(child).replace(aml.getSource().getURI()+"#", "")+" ");			
			}	
			System.out.println();		
			Set<Integer> parent2=aml.getRelationshipMap().getParents(targetId);
			Set<Integer> children2=aml.getRelationshipMap().getChildren(targetId);
			
			System.out.println("The context of "+aml.getURIMap().getLocalName(targetId)+" are listed as follow");
			System.out.print("The parents are: ");
			if(parent2.isEmpty())
				System.out.print("None");		
			for(Integer par: parent2)
			{
				System.out.print(aml.getURIMap().getURI(par).replace(aml.getTarget().getURI()+"#", "")+" ");
			}	
			System.out.println();
			System.out.print("The children are: ");
			if(children2.isEmpty())
				System.out.print("None");		
			for(Integer child: children2)
			{
				System.out.print(aml.getURIMap().getURI(child).replace(aml.getTarget().getURI()+"#", "")+" ");		
			}	
				
		}		
		else if(aml.getSource().getObjectProperties().contains(sourceId))
		{
			ObjectProperty objectProperty1=aml.getSource().getObjectProperty(sourceId);
			Set<Integer> domain1=objectProperty1.getDomain();
			Set<Integer> range1=objectProperty1.getRange();
			
			System.out.println("The context of "+aml.getURIMap().getLocalName(sourceId)+" are listed as follow");
			System.out.print("The domain are: ");
			if(domain1.isEmpty())
				System.out.print("None");
			for(Integer dom: domain1)
			{
				System.out.print(aml.getURIMap().getLocalName(dom)+" ");
			}
			System.out.println();
			System.out.print("The range are: ");
			if(range1.isEmpty())
				System.out.print("None");
			for(Integer ran: range1)
			{
				System.out.print(aml.getURIMap().getLocalName(ran)+" ");
			}	
			System.out.println();	
			
			if(aml.getTarget().getObjectProperties().contains(targetId))
			{
				ObjectProperty objectProperty2=aml.getTarget().getObjectProperty(targetId);
				Set<Integer> domain2=objectProperty2.getDomain();
				Set<Integer> range2=objectProperty2.getRange();
			
				System.out.println("The context of "+aml.getURIMap().getLocalName(targetId)+" are listed as follow");
				System.out.print("The domain are: ");
				if(domain2.isEmpty())
					System.out.print("None");
				for(Integer dom: domain2)
				{
					System.out.print(aml.getURIMap().getLocalName(dom)+" ");
				}
				System.out.println();
				System.out.print("The range are: ");
				if(range2.isEmpty())
					System.out.print("None");
				for(Integer ran: range2)
				{
					System.out.print(aml.getURIMap().getLocalName(ran)+" ");
				}	
			}
			else { //��������ƥ������Ͳ�һ����һ���Ƕ������ԣ�һ����ֵ����
				DataProperty dataProperty2=aml.getTarget().getDataProperty(targetId);
				Set<Integer> domain2=dataProperty2.getDomain();
				Set<String> range2=dataProperty2.getRange();
				
				System.out.println("The context of "+aml.getURIMap().getLocalName(targetId)+" are listed as follow");
				System.out.print("The domains are: ");
				if(domain2.isEmpty())
					System.out.print("None");
				for(Integer dom: domain2)
				{
					System.out.print(aml.getURIMap().getLocalName(dom)+" ");
				}
				System.out.println();
				System.out.print("The ranges are: ");
				if(range2.isEmpty())
					System.out.print("None");
				for(String ran: range2)
				{
					System.out.print(ran+" ");
				}	
			}
			
		}
		else if(aml.getSource().getDataProperties().contains(sourceId))
		{
			DataProperty dataProperty1=aml.getSource().getDataProperty(sourceId);
			Set<Integer> domain1=dataProperty1.getDomain();
			Set<String> range1=dataProperty1.getRange();
			
			System.out.println("The context of "+aml.getURIMap().getLocalName(sourceId)+" are listed as follow");
			System.out.print("The domains are: ");
			if(domain1.isEmpty())
				System.out.print("None");
			for(Integer dom: domain1)
			{
				System.out.print(aml.getURIMap().getLocalName(dom)+" ");
			}
			System.out.println();
			System.out.print("The ranges are: ");
			if(range1.isEmpty())
				System.out.print("None");
			for(String ran: range1)
			{
				System.out.print(ran+" ");
			}	
			System.out.println();	
			
			if(aml.getTarget().getDataProperties().contains(targetId))
			{
				DataProperty dataProperty2=aml.getTarget().getDataProperty(targetId);
				Set<Integer> domain2=dataProperty2.getDomain();
				Set<String> range2=dataProperty2.getRange();
			
				System.out.println("The context of "+aml.getURIMap().getLocalName(targetId)+" are listed as follow");
				System.out.print("The domains are: ");
				if(domain2.isEmpty())
					System.out.print("None");
				for(Integer dom: domain2)
				{
					System.out.print(aml.getURIMap().getLocalName(dom)+" ");
				}
				System.out.println();
				System.out.print("The ranges are: ");
				if(range2.isEmpty())
					System.out.print("None");
				for(String ran: range2)
				{
					System.out.print(ran+" ");
				}	
			}
			else { //��������ƥ������Ͳ�һ����һ���Ƕ������ԣ�һ����ֵ����
				ObjectProperty objectProperty2=aml.getTarget().getObjectProperty(targetId);
				Set<Integer> domain2=objectProperty2.getDomain();
				Set<Integer> range2=objectProperty2.getRange();
			
				System.out.println("The context of "+aml.getURIMap().getLocalName(targetId)+" are listed as follow");
				System.out.print("The domain are: ");
				if(domain2.isEmpty())
					System.out.print("None");
				for(Integer dom: domain2)
				{
					System.out.print(aml.getURIMap().getLocalName(dom)+" ");
				}
				System.out.println();
				System.out.print("The range are: ");
				if(range2.isEmpty())
					System.out.print("None");
				for(Integer ran: range2)
				{
					System.out.print(aml.getURIMap().getLocalName(ran)+" ");
				}	
			}
		}
		System.out.println();
		int index=aml.getAlignment().getIndexBidirectional(sourceId, targetId);
		if(rMap.getMapMinimalConflictSets().containsKey(index))
			System.out.println("This mapping is an incoherent mapping.");
	}
		
}




