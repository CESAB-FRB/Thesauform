package thesauform.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class servetAnnotation
 */
@WebServlet("/thesauformLogout")
public class LogoutThesauform extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3699369176610072531L;

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		//destruct session
		HttpSession session = request.getSession();
		session.invalidate();
		this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward( request, response );
	}

}
