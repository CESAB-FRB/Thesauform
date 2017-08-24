package thesauform.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api")
public class Api extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 784658132415610613L;
	
	private static final String VUE_SUCCESS = "/WEB-INF/scripts/api.jsp";
	public static final String GET_VIZ = "viz";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set visualization request
		request.setAttribute(GET_VIZ, "1");
		this.getServletContext().getRequestDispatcher( VUE_SUCCESS ).forward( request, response );
	}
}
