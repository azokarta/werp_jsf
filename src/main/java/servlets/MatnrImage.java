package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import general.Validation;

import static general.util.GlobalValues.matnrImagePath;

@WebServlet(urlPatterns = "/servlet/matnr-image")
public class MatnrImage extends HttpServlet {



	/**
	 * 
	 */
	private static final long serialVersionUID = 8471275873024929576L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String imageName = req.getParameter("imageName");
		String matnrCode = req.getParameter("matnrCode");
		if (Validation.isEmptyString(imageName)) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		String filePath;
		try {
			filePath = getFilePath(matnrCode, imageName);
		} catch (ParseException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		File f = new File(filePath);
		if (!f.exists()) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		resp.setContentLength((int) f.length());
		resp.setContentType("image/png");

		FileInputStream in = new FileInputStream(f);
		OutputStream out = resp.getOutputStream();
		byte[] buf = new byte[(int) f.length()];
		int count = 0;
		while ((count = in.read(buf)) >= 0) {
			out.write(buf, 0, count);
		}
		in.close();
		out.close();
	}

	private String getFilePath(String matnrCode, String imageName) throws ParseException, IOException {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(matnrImagePath);
		stringBuilder.append(File.separator);
		stringBuilder.append(matnrCode);
		stringBuilder.append(File.separator);
		stringBuilder.append(imageName);
		return stringBuilder.toString();

	}
}
