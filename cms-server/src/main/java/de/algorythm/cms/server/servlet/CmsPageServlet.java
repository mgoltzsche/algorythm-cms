package de.algorythm.cms.server.servlet;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class CmsPageServlet extends HttpServlet {

	static private final long serialVersionUID = -965493197886111412L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getOutputStream().print("asdf");
	}
}
