package com.wxb.sdk.wx.kit;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;


public class MapKit {
	/**
	 * Map key 排序
	 * @param map
	 * @return
	 */
	public static Map<String,String> order(Map<String, String> map){
		HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(	map.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			Map.Entry<String, String> item = infoIds.get(i);
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}

	/**
	 * Map value 排序
	 * @param map
	 * @return
	 */
	public static Map<String,String> orderByValue(Map<String, String> map){
		HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(	map.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,Map.Entry<String, String> o2) {
				return (o1.getValue()).toString().compareTo(o2.getValue());
			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			Map.Entry<String, String> item = infoIds.get(i);
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}

	/**
	 * 转换对象为map
	 * @param object
	 * @param ignore
	 * @return
	 */
	public static Map<String,String> objectToMap(Object object,String... ignore){
		Map<String,String> tempMap = new LinkedHashMap<String, String>();
		for(Field f : object.getClass().getDeclaredFields()){
			if(!f.isAccessible()){
				f.setAccessible(true);
			}
			boolean ig = false;
			if(ignore!=null&&ignore.length>0){
				for(String i : ignore){
					if(i.equals(f.getName())){
						ig = true;
						break;
					}
				}
			}
			if(ig){
				continue;
			}else{
				Object o = null;
				try {
					o = f.get(object);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				tempMap.put(f.getName(), o==null?"":o.toString());
			}
		}
		return tempMap;
	}

	/**
	 * url 参数串连
	 * @param map
	 * @param keyLower
	 * @param valueUrlencode
	 * @return
	 */
	public static String mapJoin(Map<String, String> map,boolean keyLower,boolean valueUrlencode){
		StringBuilder stringBuilder = new StringBuilder();
		for(String key :map.keySet()){
			if(map.get(key)!=null&&!"".equals(map.get(key))){
				try {
					String temp = (key.endsWith("_")&&key.length()>1)?key.substring(0,key.length()-1):key;
					stringBuilder.append(keyLower?temp.toLowerCase():temp)
								 .append("=")
								 .append(valueUrlencode?URLEncoder.encode(map.get(key),"utf-8").replace("+", "%20"):map.get(key))
								 .append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		if(stringBuilder.length()>0){
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		}
		return stringBuilder.toString();
	}

	/**
	 * 简单 xml 转换为 Map
	 * @param xml
	 * @return
	 */
	public static Map<String,String> xmlToMap(String xml){
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
			Element element = document.getDocumentElement();
			NodeList nodeList = element.getChildNodes();
			Map<String, String> map = new LinkedHashMap<String, String>();
			for(int i=0;i<nodeList.getLength();i++){
				Element e = (Element) nodeList.item(i);
				map.put(e.getNodeName(),e.getTextContent());
			}
			return map;
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static  String mapToXml(Map<String,String> map){
		if(MapUtils.isEmpty(map)){
			return StringUtils.EMPTY;
		}
		StringBuffer sb = new StringBuffer("<xml>");
		Set set = map.entrySet();
		Iterator records = set.iterator();
		while (records.hasNext()) {
			Map.Entry entry = (Map.Entry) records.next();
			sb.append("<").append(entry.getKey()).append(">");
			sb.append("<![CDATA["+entry.getValue()+"]]>");
			sb.append("</").append(entry.getKey()).append(">");
		}
		sb.append("</xml>");
		return sb.toString();
	}
	/**
	 * @author lmhy
	 * xml to json
	 * @param xml
	 * @return
	 */
	public static String xmlToJsonStr(String xml) {
		try {
			SAXReader reader = new SAXReader();
			InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			InputStreamReader strInStream = new InputStreamReader(in, "UTF-8");
			org.dom4j.Document document = reader.read(strInStream);
			Map<String, String> map = new HashMap<String, String>();
			List<org.dom4j.Element> level = document.getRootElement().elements();
			for (org.dom4j.Element e : level) {
				map.put(e.getName(), e.getTextTrim());
			}
			return JSONObject.toJSONString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;
	}
}
