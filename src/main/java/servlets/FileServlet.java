package servlets;

import general.services.UpdFileService;
import general.tables.UpdFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@WebServlet(urlPatterns = "/servlet/file")
public class FileServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6347928597004737834L;

	ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Long id = 0L;
		UpdFileService fileService = ctx.getBean("updFileService", UpdFileService.class);
		UpdFile updFile = null;
		try {
			id = new Long(req.getParameter("id"));
			updFile = fileService.findOne(id);
			if (updFile == null) {
				throw new Exception("File Not Found!!!");
			}

		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		File f = new File(fileService.getFilePathWithName(updFile));
		if (!f.exists()) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		res.setContentLength((int) f.length());
		res.setContentType(updFile.getMime_type());

		FileInputStream in = new FileInputStream(f);
		OutputStream out = res.getOutputStream();
		byte[] buf = new byte[(int) f.length()];
		int count = 0;
		while ((count = in.read(buf)) >= 0) {
			out.write(buf, 0, count);
		}
		in.close();
		out.close();

	}
}