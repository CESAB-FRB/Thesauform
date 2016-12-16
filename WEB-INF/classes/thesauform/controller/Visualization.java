package thesauform.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesauform.model.ThesauformConfiguration;

@WebServlet("/home")
public class Visualization extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2462762542967205941L;
	
	private static final String VUE_SUCCESS = "/WEB-INF/scripts/homeVisualization.jsp";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set visualization request
		request.setAttribute(ThesauformConfiguration.GET_VIZ, "1");
		this.getServletContext().getRequestDispatcher( VUE_SUCCESS ).forward( request, response );
	}
}
