package com.feihuwang.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.feihuwang.rest.service.DemoRestService;

@Path("/demo")
public class DemoController extends BaseController{
	private static final Log log = LogFactory.getLog(DemoController.class);
	@Autowired
	private DemoRestService demoRestService;

	@GET
	@Path("info.{returnType}")
	public Response loadAlbum(
			@QueryParam("id") long categoryId,
			@QueryParam("plat") String plat) throws IOException{
		return response(demoRestService.loadDemoInfo(categoryId));
	}
}
