package thesauform.controller.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class ExportFile
 */
@WebServlet("/administration/exportFile")
public class ExportFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String VUE_FAILED = "/WEB-INF/scripts/exportFile.jsp";
	private static final String PERSON_TYPE = "person";
	private static final String DATA_TYPE = "annotation";
	private static final String DATA_PUBLIC_TYPE = "public";
	private static final String TYPE_ERROR = "type";
	private static final String TYPE_ERROR_MESSAGE = "Wrong type of file given";
	private static final String USER_ERROR_PARAMETER = "my_errors";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		errors.put(TYPE_ERROR, "");
		//get parameter
		String fileType = request.getParameter("file");
		//do treatment
		try {
			List<String> fileList = new ArrayList<String>();
			fileList.add(PERSON_TYPE);
			fileList.add(DATA_TYPE);
			fileList.add(DATA_PUBLIC_TYPE);
			if (fileType != null && !fileType.isEmpty() && fileList.contains(fileType)) {
				File file = null;
				switch (fileType) {
					case PERSON_TYPE:
						file = new File(getServletContext().getRealPath(ThesauformConfiguration.person_file));
						break;
					case DATA_TYPE:
						file = new File(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						break;
					case DATA_PUBLIC_TYPE:
						file = new File(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
						break;
					default:
						errors.put(TYPE_ERROR, TYPE_ERROR_MESSAGE);
						throw new Exception(TYPE_ERROR_MESSAGE);
				}
				response.setContentType("text/xml");
				response.setContentLength((int)file.length());
				response.setHeader("Content-Disposition","attachment; filename=\"" + fileType + "save.owl\"");
				FileInputStream fileInputStream = new FileInputStream(file);
				OutputStream responseOutputStream = response.getOutputStream();
				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				}
				fileInputStream.close();				
			}
			else {
				errors.put(TYPE_ERROR, TYPE_ERROR_MESSAGE);
				throw new Exception(TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception e) {
			request.setAttribute(USER_ERROR_PARAMETER, errors);
			this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);			
		}
	}
}
