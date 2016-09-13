package thesauform.controller.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class ExportFile
 */
@WebServlet("/administration/importFile")
public class ImportFile extends HttpServlet {
	/**
	 * import excel template (to define) load template row into model write
	 * model into temp file test temp file save old file write real file
	 */
	private static final long serialVersionUID = 1L;

	public static final String VUE_FAILED = "/WEB-INF/scripts/exportFile.jsp";
	private static final String TYPE_ERROR = "type";
	private static final String USER_ERROR_PARAMETER = "my_errors";

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		// Create path components to save the file
		String path = "";
		if (ThesauformConfiguration.database_path == null || ThesauformConfiguration.database_path == "") {
			path = "/tmp";
		} else {
			path = ThesauformConfiguration.database_path;
		}
		Part filePart = request.getPart("file");
		String fileName = getFileName(filePart);
		OutputStream out = null;
		InputStream filecontent = null;
		PrintWriter writer = response.getWriter();
		try {
			out = new FileOutputStream(new File(path + File.separator + fileName));
			filecontent = filePart.getInputStream();
			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			writer.println("New file " + fileName + " created at " + path);

		} catch (FileNotFoundException fne) {
			writer.println("You either did not specify a file to upload or are "
					+ "trying to upload a file to a protected or nonexistent " + "location.");
			writer.println("<br/> ERROR: " + fne.getMessage());

		} finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private String getFileName(final Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		errors.put(TYPE_ERROR, "");
		// get parameter
		String file = request.getParameter("file");
		// do treatment
		try {

		} catch (Exception e) {
			request.setAttribute(USER_ERROR_PARAMETER, errors);
			this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
		}
	}
}
