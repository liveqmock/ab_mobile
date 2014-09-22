package com.feihuwang.utils;


import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.xstream.XStream;

public class DataFormatUtil {
	private static final Log log = LogFactory.getLog(DataFormatUtil.class);
	
	private static final XMLSerializer xmlSerializer = new XMLSerializer();
	private static final ThreadLocal<XStream> xstreamThread = new ThreadLocal<XStream>(){
		@Override
		protected XStream initialValue() {
			return new XStream();
		}
	};
	private static final ObjectMapper m = new ObjectMapper();
	private static final ConcurrentHashMap<Class<?>, ObjectReader> objectReaderMap = new ConcurrentHashMap<Class<?>, ObjectReader>();
	private static final Object objectReaderMapLock = new Object();
	
	static {
		m.setSerializationInclusion(Include.NON_EMPTY);
		m.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * 根据java对象生成json string
	 * @param obj
	 * @return 如果参数为null则返回"";<br/>如果序列化错误则返回null;<br/>正常返回json字符串
	 */
	public static String toJsonString(final Object obj){
		if(obj == null){
			return "";
		}
		try {
			return m.writeValueAsString(obj);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 根据json字符串生成jsonNode对象
	 * @param jsonString
	 * @return
	 */
	public static JsonNode toJsonNode(final String jsonString){
		if(StringUtils.isBlank(jsonString)){
			return null;
		}
		try {
			return m.readTree(jsonString);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return null;
	}
	
	/**
	 * JSON to XML
	 *
	 * @param jsonStr
	 * @return
	 */
	public static String toXml(String rootName, String jsonStr) {
		xmlSerializer.setTypeHintsEnabled(false);
		xmlSerializer.setElementName("item");
		xmlSerializer.setRootName(rootName);
		return xmlSerializer.write(JSONSerializer.toJSON(jsonStr));
	}
	
	/**
	 * java bean to XML
	 * @param rootName
	 * @param object
	 * @return
	 */
	public static String toXml(String rootName, Object object){
		if(object != null){
			XStream xStream = xstreamThread.get();
			if(StringUtils.isNotBlank(rootName)){
				xStream.alias(rootName, object.getClass());
			}
			return xStream.toXML(object);
		}
		return "";
	}
	
	public static <T> T toBean(Class<T> clazz, String jsonStr){
		if(clazz == null || StringUtils.isBlank(jsonStr)){
			log.error("class and jsonStr params can't be empty;class=" + clazz + ";jsonStr=" + jsonStr);
			return null;
		}
		ObjectReader reader = objectReaderMap.get(clazz);
		if(reader == null){
			synchronized (objectReaderMapLock) {
				if((reader = objectReaderMap.get(clazz)) == null){
					reader = objectReaderMap.putIfAbsent(clazz, m.reader(clazz));
					if(reader == null){
						reader = objectReaderMap.get(clazz);
					}
				}
			}
		}
		try {
			return reader.readValue(jsonStr);
		} catch (Exception e) {
			log.error(e.getMessage() + "class=" + clazz + ";jsonStr=" + jsonStr, e);
		}
		return null;
	}
	
	/**
	 * 创建jsonNode对象
	 * @return
	 */
	public static ArrayNode createArrayNode(){
		try {
			return m.createArrayNode();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 创建jsonNode对象数组
	 * @return
	 */
	public static ObjectNode createObjectNode(){
		try {
			return m.createObjectNode();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
