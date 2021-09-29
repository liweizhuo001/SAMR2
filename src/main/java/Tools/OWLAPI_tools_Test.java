package Tools;

import java.util.ArrayList;
import java.util.HashMap;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import Tools.OWLAPI_tools;
import Tools.TreeMap_Tools;

public class OWLAPI_tools_Test {
	public  static  void main(String args[]) throws OWLOntologyCreationException
	{
			//String ontPath="ontologies debugs/cheminf.rdf";
			String ontPath="exampleOntology/cmt.owl";
			//String   ontPath = "ontologies debugs/Geography_disj.owl";
			

			OWLAPI_tools onto1=new OWLAPI_tools();
			onto1.readOnto(ontPath);
			
			ArrayList<String>contents=onto1.getOntologyContent();
			
			for(int i=0;i<contents.size();i++)
			{
				System.out.println(contents.get(i));
			}
			
			//onto1.isConsistent();
				
		//	onto1.GetConcept_Children("Chairman");
			//�������еĸ���
			//ArrayList<String> classes=onto1.getConcepts();
/*			ArrayList<String> sas=onto1.getConceptAnnoations();
		    System.out.println("*************************");*/
		    

		/*	for(String concept:	classes)
			{
				System.out.println(concept);
			}*/
			//System.out.println("����ĸ���Ϊ��"+classes.size());
			
			//�������е�����
/*			ArrayList<String> OB_Properties=onto1.getObjectProperties();	
			System.out.println("*************************");			
			for(String op:	OB_Properties)
			{
				System.out.println(op);
			}
			System.out.println("�������Եĸ���Ϊ��"+OB_Properties.size());
			
			ArrayList<String> Data_Properties=onto1.getDataProperties();	
			System.out.println("*************************");			
			for(String dp:	Data_Properties)
			{
				System.out.println(dp);
			}
			System.out.println("��ֵ���Եĸ���Ϊ��"+Data_Properties.size());*/
			
					
			
			//����ABOX�ı���
			/*ArrayList<String> Instances=onto1.getConceptInstances2();
			System.out.println("ʵ�����£�");		
			for(int i=0;i<Instances.size();i++)
			{
				System.out.println(Instances.get(i));
			}*/
/*			System.out.println("*****************************************");
			System.out.println("����ĸ���Ϊ��"+classes.size());
			System.out.println("�������Եĸ���Ϊ��"+OB_Properties.size());
			System.out.println("��ֵ���Եĸ���Ϊ��"+Data_Properties.size());*/
			
			System.out.println("******************************************");
		//    String concept="NCI_C49779";	
		    String concept="Giraffe";	
		    
/*			ArrayList<String> conceptSubClasses=onto1.getConceptSubClasses(concept);	
			for(String a:conceptSubClasses)
			{
				System.out.println(a);
			}*/
		    System.out.println(concept);
		    onto1.getConceptAnnoations(concept);
		    System.out.println("*************************");
			ArrayList<String>classes_labels=onto1.getConceptAnnoations();
			for(int i=0;i<classes_labels.size();i++)
			{
				System.out.println(classes_labels.get(i));
			}
		   /* String concept="Person";	
			System.out.println("******************************************");
			ArrayList<String> conceptInstances=onto1.getConceptInstances(concept);	
			for(String a:conceptInstances)
			{
				System.out.println(a);
			}*/
/*			System.out.println("******************************************");
			double start=System.currentTimeMillis()/1000;
			ArrayList<String> subClasses=onto1.getSubClasses();
			double end=System.currentTimeMillis()/1000;
			for(String a:subClasses)
			{
				System.out.println(a);
			}
			System.out.println("size�Ĵ�СΪ"+subClasses.size());
			System.out.println("���ĵ�ʱ��Ϊ"+(end-start)+"s");*/
			/*System.out.println("******************************************");
			ArrayList<String> subDirectClasses=onto1.getDirectSubClasses();
			for(String a:subDirectClasses)
			{
				System.out.println(a);
			}*/
			/*System.out.println("******************************************");
			ArrayList<String> superClasses=onto1.getSuperClasses();
			for(String a:superClasses)
			{
				System.out.println(a);
			}*/
			
			/*System.out.println("******************************************");
			ArrayList<String> superDirectClasses=onto1.getDirectSuperClasses();
			for(String a:superDirectClasses)
			{
				System.out.println(a);
			}*/
			
		/*	System.out.println("******************************************");
			ArrayList<String> siblings1=onto1.getSibling(subDirectClasses);
			for(String a:siblings1)
			{
				System.out.println(a);
			}*/
			/*System.out.println("******************************************");
			System.out.println("******************************************");
			ArrayList<String> disjoint1=onto1.getDisjointwith();
			for(String a:disjoint1)
			{
				System.out.println(a);
			}
			TreeMap_Tools Ontology1_Disjoint=new TreeMap_Tools(disjoint1);
			System.out.println(Ontology1_Disjoint.size());*/
			
			/*ArrayList<String> classlabel1=onto1.getConceptAnnoations();
			for(String a:classlabel1)
			{
				System.out.println(a);
			}*/
			/*System.out.println("******************************************");
			ArrayList<String> objectPropertieslabel1=onto1.getObjectPropertyAnnoations();
			for(String a:objectPropertieslabel1)
			{
				System.out.println(a);
			}*/
			
			/*System.out.println("******************************************");
			ArrayList<String> dataPropertieslabel1=onto1.getDataPropertyAnnoations();
			for(String a:dataPropertieslabel1)
			{
				System.out.println(a);
			}*/
			
		/*	ArrayList<String> propertiesInverse1=onto1.getPropertyAndInverse();
			for(String a:propertiesInverse1)
			{
				System.out.println(a);
			}
			System.out.println("����ʵ���ĸ��������"+Instances.size());	*/
			
			
	
/*			ArrayList<String> instances=onto1.getConceptInstances2("Possible_Reviewer");
			for(String a:instances)
			{
				System.out.println(a);
			}*/
			//onto1.isConceptInstance("Possible_Reviewer","Li-YanYuan");
			
			/*ArrayList<String> Relation=onto1.getRelationInstances();	
			for(String relation:	Relation)
			{
				System.out.println(relation);
			}*/
		
			
			
			/*ArrayList<String> Relation=onto1.GetRelationInstance("PengWang", "writePaper");	
			for(String relation:	Relation)
			{
				System.out.println(relation);
			}*/
			
			
		/*	System.out.println("******************************************");
			ArrayList<String> objectRelations1=onto1.getObjectRelations();
			for(String relation:	objectRelations1)
			{
				System.out.println(relation);
			}*/
			
/*			ArrayList<String> dataRelations1=onto1.getDataPropertyRelations();
			for(String relation:	dataRelations1)
			{
				System.out.println(relation);
			}
			
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			
			HashMap<String,ArrayList<String>> a=changeTripleMap(dataRelations1);
			for(String x:a.keySet())
			{
				ArrayList<String> y=a.get(x);
				for(String z:y)
				{
					String part[]=z.split("--");
					System.out.println(x+","+part[0]+","+part[1]);
				}		
			}*/
			
		/*	onto1.getEquivalentClass();
			onto1.getEquivalentObjectProperty();
			onto1.getEquivalenDataProperty();*/
			
			//ArrayList<String> Restrictions1=onto1.getSomeRestrictions();
			/*for(String relation:	Restrictions1)
			{
				System.out.println(relation);
			}*/
			
			
			//��Ѱ����ʵ����URL
			/*String URL=onto1.findInstancesUrl();
			System.out.println(URL);*/	
	}
	
	public static HashMap<String,ArrayList<String>> changeTripleMap(ArrayList<String> triple)
	{
		HashMap<String,ArrayList<String>> relationMap=new HashMap<String,ArrayList<String>>();
		for(int i=0;i<triple.size();i++)
		{
			String part[]=triple.get(i).split(",");
			if(!relationMap.keySet().contains(part[0].toLowerCase())) //���key�����ڣ�����µ�(key,Value)��
			{
				ArrayList<String> a=new ArrayList<String>();
				a.add(part[1].toLowerCase()+"--"+part[2].toLowerCase());
				relationMap.put(part[0].toLowerCase(), a);
			}
			else	//���key���ڣ�ֱ�Ӷ�Value���Ͻ������
			{
				relationMap.get(part[0].toLowerCase()).add(part[1].toLowerCase()+"--"+part[2].toLowerCase());
			}
		}
		return relationMap;
	}
}
