package thesauform.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class search
 */
@WebServlet("/visualizationFacet")
public class FacetVisualization extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7348837835799181294L;
	
	private static final String VUE_SUCCESS = "/WEB-INF/scripts/facetVisualization.jsp";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// variable to write code
		this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
	}
}