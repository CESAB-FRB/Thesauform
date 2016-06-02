package thesauform.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class hierarchy
 */
@WebServlet("/visualizationHierarchy")
public class HierarchyVisualization extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2589355470778944109L;

	private static final String VUE_SUCCESS = "/WEB-INF/scripts/hierarchyVisualization.jsp";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// set visualization request
		request.setAttribute(ThesauformConfiguration.GET_VIZ, "1");
		this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
	}
}
