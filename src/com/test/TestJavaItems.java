package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJavaItems {

	private static Map<Integer, Set<String>> keyLevel = new HashMap<Integer, Set<String>>();;
	private static Map<String, Object> masterObject = new HashMap<String, Object>();
	private static Set<String> dataSet = new HashSet<String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String data = "bkey=bk1;pid=p1;data.payType=card;data.cinfo.coAppOpted=true;"
				+ "data.cinfo.coAppAuthType=acctNum;" + "data.cinfo.coAppAuthVal=102;"
						+ "data.cinfo.coAppInvestmentAmt=233_43;"
				+ "data.cinfo.applts[0].appltType=k1;" + "data.cinfo.applts[1].appltType=k2;"
				+ "data.pinfo[0].srvc.errorCode=ec;" + "data.pinfo[0].srvc.errorDesc=desc;"
				+ "otherdata.acctType[0]=checking;" + "otherdata.acctType[1]=savings";
		ObjectMapper mapper = new ObjectMapper();

		for (String d : data.split(";")) {
			masterObject.putAll((Map<? extends String, ? extends Object>) reccurseObject(masterObject, "Map", d));
			// masterObject.put(objectName(d), reccurseObject(masterObject, "Map", d));
			System.out.println(" Added Data "+d);

		}

		try {
			String jsonOutput = mapper.writeValueAsString(masterObject);
			System.out.println("jsonOutput= " + jsonOutput);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	private static Object reccurseObject(Object ob, String type, String restOfItem) {
		//System.out.println(" Rest of Item : " + restOfItem);
		String currentItem = restOfItem.split("\\.")[0];
		String currentType = analyzeField(currentItem);

		/*
		 * System.out.println( "Parsing for " + currentItem + " with parent Type as " +
		 * type + " and current Type as " + currentType);
		 */
		// String set to in Map
		if (currentType.equalsIgnoreCase("String") && type.equalsIgnoreCase("Map")) {
		//	System.out.println(" String Value " + stringVal(currentItem) + " of variable " + objectName(currentItem));
			((Map<String, Object>) ob).put(objectName(currentItem), stringVal(currentItem));
			return ob;
		}
		// String set in Array Of Strings
		if (currentType.equalsIgnoreCase("String") && type.equalsIgnoreCase("ArrayOfStrings")) {
			int count = Integer.parseInt(currentItem.split("\\[")[1].split("\\]")[0]);
			int currentCount = ((List<String>) ob).size();
			if (currentCount < count) {
				((List<String>) ob).add(currentItem.split("=")[1]);
			} else {
				((List<String>) ob).set(count, currentItem.split("=")[1]);
			}
			return ob;
		}
		// Boolean set in Map
		if (currentType.equalsIgnoreCase("boolean") && type.equalsIgnoreCase("Map")) {
			/*
			 * System.out.println( " booleanVal Value " + booleanVal(currentItem) +
			 * " of variable " + objectName(currentItem));
			 */
			((Map<String, Object>) ob).put(objectName(currentItem), booleanVal(currentItem));
			return ob;
		}
		// Float set in Map
		if (currentType.equalsIgnoreCase("float") && type.equalsIgnoreCase("Map")) {
			/*
			 * System.out.println( " booleanVal Value " + booleanVal(currentItem) +
			 * " of variable " + objectName(currentItem));
			 */
			((Map<String, Object>) ob).put(objectName(currentItem), floatVal(currentItem));
			return ob;
		}
		// Float set in Map
				if (currentType.equalsIgnoreCase("long") && type.equalsIgnoreCase("Map")) {
					/*
					 * System.out.println( " booleanVal Value " + booleanVal(currentItem) +
					 * " of variable " + objectName(currentItem));
					 */
					((Map<String, Object>) ob).put(objectName(currentItem), longVal(currentItem));
					return ob;
				}
		
		// Boolean set in Array Of Strings
		if (currentType.equalsIgnoreCase("boolean") && type.equalsIgnoreCase("ArrayOfStrings")) {
			int count = Integer.parseInt(currentItem.split("\\[")[1].split("\\]")[0]);
			int currentCount = ((List<String>) ob).size();
			if (currentCount < count) {
				((List<String>) ob).add(currentItem.split("=")[1]);
			} else {
				((List<String>) ob).set(count, currentItem.split("=")[1]);
			}
			return ob;
		}
		// Map set in Map
		if (currentType.equalsIgnoreCase("Map") && type.equalsIgnoreCase("Map")) {
			Map<String, Object> newObj;
			if (!((Map<String, Object>) ob).containsKey(objectName(currentItem))) {
				((Map<String, Object>) ob).put(objectName(currentItem), new HashMap<String, Object>());
			}

			((Map<String, Object>) ob).put(objectName(currentItem),
					reccurseObject(((Map<String, Object>) ob).get(objectName(currentItem)), currentType,
							restOfItem.replaceAll("^" + objectName(currentItem) + "\\.", "")));
			// ((Map<String, Object>) ob).putAll(newObj);

			return ob;
		}
		// ArrayOfObject set in a Map
		if (currentType.equalsIgnoreCase("ArrayOfObjects") && type.equalsIgnoreCase("Map")) {

			if (!((Map<String, Object>) ob).containsKey(objectName(currentItem).split("\\[")[0])) {
				((Map<String, Object>) ob).put(objectName(currentItem).split("\\[")[0],
						new ArrayList<Map<String, Object>>());
			}
			List<Map<String, Object>> listObjs = (List<Map<String, Object>>) ((Map<String, Object>) ob)
					.get(objectName(currentItem).split("\\[")[0]);
			// count check and add items.
			int currentCount = Integer.parseInt(currentItem.split("\\[")[1].split("\\]")[0]);
			int count = listObjs.size();
			if (count < (currentCount + 1)) {
				listObjs.add(new HashMap<String, Object>());
			}
			listObjs.set(currentCount, (Map<String, Object>) reccurseObject(listObjs.get(currentCount), "Map",
					restOfItem.replaceAll("^" + objectName(currentItem).split("\\[")[0] + "\\[[0-9]*\\]\\.", "")));
			((Map<String, Object>) ob).put(objectName(currentItem).split("\\[")[0], listObjs);
			return ob;
		}
		// ArrayOfStrings set in a Map
		if (currentType.equalsIgnoreCase("ArrayOfStrings") && type.equalsIgnoreCase("Map")) {

			if (!((Map<String, Object>) ob).containsKey(objectName(currentItem).split("\\[")[0])) {
				((Map<String, Object>) ob).put(objectName(currentItem).split("\\[")[0],
						new ArrayList<String>());
			}
			List<String> listStr = (List<String>) ((Map<String, Object>) ob)
					.get(objectName(currentItem).split("\\[")[0]);
			int currentCount = Integer.parseInt(currentItem.split("\\[")[1].split("\\]")[0]);
			int count = listStr.size();
			if (count < (currentCount + 1)) {
				listStr.add(currentItem.split("=")[1]);
			}
			((Map<String, Object>) ob).put(objectName(currentItem).split("\\[")[0], listStr);
			return ob;
		}

		// Map set in ArrayOfObjects
		if (currentType.equalsIgnoreCase("Map") && type.equalsIgnoreCase("ArrayOfObjects")) {
			int count = ((List<Map<String, Object>>) ob).size();
			int currentCount = Integer.parseInt(currentItem.split("\\[")[1].split("\\]")[0]);

			Map<String, Object> myMap;
			if (count < currentCount) {
				myMap = new HashMap<String, Object>();
				String newChildObj = restOfItem.replaceAll("^" + objectName(currentItem), "");
				String newChildItem = newChildObj.split("\\.")[0];
				String newChildType = analyzeField(newChildItem);
				myMap.put(objectName(newChildItem), reccurseObject(myMap, "Map",
						newChildObj.replaceAll("^" + objectName(newChildItem) + "\\.", "")));
				((List<Map<String, Object>>) ob).add(myMap);
			} else {
				myMap = ((List<Map<String, Object>>) ob).get(currentCount);
				String newChildObj = restOfItem.replaceAll("^" + objectName(currentItem), "");
				String newChildItem = newChildObj.split("\\.")[0];
				String newChildType = analyzeField(newChildItem);
				myMap.put(objectName(newChildItem), reccurseObject(myMap, "Map",
						newChildObj.replaceAll("^" + objectName(newChildItem) + "\\.", "")));
				((List<Map<String, Object>>) ob).add(myMap);
			}

			return ob;
		}

		return null;

	}

	private static String objectName(String val) {
		if (val.contains("\\.")) {
			return val.split("\\.")[0];
		} else {
			return val.split("=")[0];
		}
	}

	private static String stringVal(String val) {
		return val.split("=")[1];
	}

	private static boolean booleanVal(String val) {
		return val.split("=")[1].contains("true");
	}
	
	private static float floatVal(String val) {
		return Float.parseFloat(val.split("=")[1].replaceAll("\\_", "\\."));
	}
	
	private static long longVal(String val) {
		return Long.parseLong(val.split("=")[1]);
	}

	private static String analyzeField(String field) {
		String type = "Map";
		// array of string
		if (field.contains("[") && field.contains("=")) {
		//	System.out.println(field + " Array of String");
			type = "ArrayOfStrings";
		}
		// array of object
		else if (field.contains("[")) {
		//	System.out.println(field + " Array of Objects ");
			type = "ArrayOfObjects";
		}

		// String / boolean field
		else if (field.contains("=")) {
			if (field.contains("=true") || field.contains("=false")) {
				//	System.out.println(field + " Field is boolean");
					type = "boolean";
				} else {
				//	System.out.println(field + " Field is String");
					type = "String";
				}
			if(field.matches(".*=[0-9]*\\_[0-9]*$")) {
				type = "float";
			}
			if(field.matches(".*=[0-9]*$")) {
				type = "long";
			}
			
		}
		// hash map
		else {
		//	System.out.println(field + " Field is Map ");
		}
		return type;
	}

}
