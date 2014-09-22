package com.feihuwang.rest.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.feihuwang.pc.service.DemoService;
import com.feihuwang.rest.service.DemoRestService;
import com.feihuwang.utils.DataFormatUtil;

@Component("demoRestService")
public class DemoRestServiceImpl implements DemoRestService {
	private static final Log log = LogFactory.getLog(DemoRestServiceImpl.class);
	@Autowired
	private DemoService demoService;
	
	@Override
	public String loadDemoInfo(Long categoryId) {
		return DataFormatUtil.toJsonString(demoService.loadDemoInfo(categoryId));
	}
}