package com.wxb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * xml 序列化和反序列工具类
 * @title XmlUtils.java
 * @description
 * @version 1.0
 * @created 2015年7月21日下午1:29:59
 */
public class XmlUtils {

	/**
	 * 将model转为xml
	 * @param model
	 * @return xml
	 */
	public static String toXml(Model<? extends Model<?>> model) {
		return toXml(CPI.getAttrs(model));
	}

	/**
	 * 将Collection<Model>转换为xml
	 * @param models
	 * @return xml
	 */
	public static String toXml(Collection<Model<? extends Model<?>>> models) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (Model<? extends Model<?>> model : models) {
			list.add(CPI.getAttrs(model));
		}
		return toXml(list);
	}

	/**
	 * 将 record 转为xml
	 * @param record
	 * @return xml
	 */
	public static String toXml(Record record) {
		return toXml(record.getColumns());
	}

	/**
	 * 将List<Record>转换为xml
	 * @param models
	 * @return xml
	 */
	public static String toXml(List<Record> records) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (Record record : records) {
			list.add(record.getColumns());
		}
		return toXml(list);
	}

	/**
	 * 将 Object 转为xml
	 * @param record
	 * @return xml
	 */
	public static String toXml(Object object) {
		XmlMapper mapper = new XmlMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将字符串类型的xml转成bean
	 * @param content
	 * @param valueType
	 * @return T
	 */
	public static <T> T toBean(String content, Class<T> valueType){
		XmlMapper mapper = new XmlMapper();
		try {
			return mapper.readValue(content, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将流转成bean
	 * @param content
	 * @param valueType
	 * @return T
	 */
	public static <T> T toBean(InputStream src, Class<T> valueType){
		XmlMapper mapper = new XmlMapper();
		try {
			return mapper.readValue(src, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
