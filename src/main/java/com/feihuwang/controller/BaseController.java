package com.feihuwang.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.feihuwang.core.SupportedFormat;
import com.feihuwang.utils.AppConstants;
import com.feihuwang.utils.DataFormatUtil;

public class BaseController {
	
	private static Log log = LogFactory.getLog(BaseController.class);
	private static final String STATUS_TEXT = "statusText";
	private static final String REQUEST = "request";
	private static final String STATUS = "status";
	private static final String CALLBACK = "callback";
	private static final String ERROR_ROOT = "error";
	private static final String ENCODING = "encoding";
	private static final String FORMAT = "format";
	protected static final int DEFAULT_PAGE_SIZE = 10;
	protected static final int DEFAULT_PAGE = 1;
	protected static final int MAX_PAGE_SIZE = 50;
	
	@Context
	protected HttpServletRequest request;
	
	protected Response response(){
		return response(null);
	}
	
	protected Response response(String data){
		ResponseBuilder builder = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"status\":");
		sb.append(Status.OK.getStatusCode());
		sb.append(",");
		sb.append("\"statusText\":");
		sb.append("\"");
		sb.append(Status.OK.getReasonPhrase());
		sb.append("\"");
		if(StringUtils.isNotBlank(data) && !data.equals("null") && !data.equals("{}") && !data.equals("[]")){
			sb.append(",");
			sb.append("\"data\":");
			sb.append(data);
		}
		sb.append("}");
		
		String responseStr = sb.toString();
		String format = (String) request.getAttribute(FORMAT);
		String encoding = (String) request.getAttribute(ENCODING);
		log.debug("The encoding of data is:" + encoding);

		SupportedFormat sf = SupportedFormat.valueOf(format.toUpperCase());

		switch (sf) {

			case JSON: {
				/**
				 * 处理跨域请求
				 */
				String jsoncallback = (String) request.getAttribute(CALLBACK);
				if (StringUtils.isNotBlank(jsoncallback)) {
					builder = Response
							.ok(jsoncallback + "(" + responseStr + ");", "text/plain;charset=" + encoding);
				} else {
					builder = Response.ok(responseStr, "application/json;charset=" + encoding);
				}
				break;
			}
			case XML: {
				String uri = request.getRequestURI();
				String rootName = "root";
				if (null != uri && uri.contains("/")) {
					rootName = uri.split("/")[1];
				}
				builder = Response.ok(DataFormatUtil.toXml(rootName, responseStr),
						"application/xml;charset=" + encoding);
				break;
			}
			default:
				builder = Response.ok(responseStr, "application/json;charset=" + encoding);
		}
		if (builder != null) {
			return builder.build();
		} else {
			return null;
		}

	}

	protected Response response(int errorCode){
		ResponseBuilder builder = null;
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(STATUS, errorCode);
		jsonObject.put(REQUEST, getRealRequestUrl());
		
		Object e = request.getAttribute(ERROR_ROOT);
		String msg = "";
		if (null != e) {
			msg = e.toString();
		}
		Status s = Status.fromStatusCode(errorCode);
		if(s != null){
			if (StringUtils.isNotBlank(msg)){
				jsonObject.put(STATUS_TEXT, s.getReasonPhrase() + " : " + msg);
			}else{
				jsonObject.put(STATUS_TEXT, s.getReasonPhrase());
			}
		}
		String format = (String) request.getAttribute(FORMAT);
		String encoding = (String) request.getAttribute(ENCODING);
		log.debug("The encoding of data is:" + encoding);

		SupportedFormat sf = SupportedFormat.valueOf(format.toUpperCase());

		switch (sf) {

		case JSON: {
			builder = Response.ok(jsonObject.toString(),"application/json;charset=" + encoding);
			break;
		}
		case XML: {
			builder = Response.ok(DataFormatUtil.toXml(ERROR_ROOT, jsonObject.toString()),"application/xml;charset=" + encoding);
			break;
		}
		default:
			builder = Response.ok(jsonObject.toString(), "application/json;charset=" + encoding);
		}
		if (builder != null) {
			builder.status(errorCode);
			return builder.build();
		} else {
			return null;
		}

	}

	/**
	 * 获取ip地址
	 *
	 * @param request
	 * @return
	 */
	protected String getIpAddr(HttpServletRequest request) {

		String ip = request.getHeader("X-Forwarded-For");
		log.info("===X-Forwarded-For===:" + ip);

		if (ip != null) {
			if (ip.indexOf(',') == -1) {
				return ip;
			}
			return ip.split(",")[0];
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		log.info("The real ip is :" + ip);
		return ip;
	}
	
	protected int checkPage(int page) {
		return page <= 0 ? DEFAULT_PAGE : page;
	}
	
	protected int chekPageSize(int pageSize){
		return pageSize <= 0 ? DEFAULT_PAGE_SIZE : (pageSize > MAX_PAGE_SIZE ? MAX_PAGE_SIZE : pageSize);
	}
	
	protected String getRealRequestUrl() {
		String xRequest = request.getHeader("X-Request-Uri");
		String host = request.getHeader("Host");
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(host) && StringUtils.isNotBlank(xRequest)){
			sb.append("http://");
			sb.append(host);
			sb.append(xRequest);
		}else{
			sb.append(request.getAttribute(AppConstants.REQUEST_URL));
			String queryStr = request.getQueryString();
			if(StringUtils.isNotBlank(queryStr)){
				sb.append("?");
				sb.append(queryStr);
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		
	}
}
