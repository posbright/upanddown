package com.dzm.download;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownJsp
 */
@WebServlet("/downJsp")
public class DownJsp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = this.getServletContext().getRealPath("/WEB-INF/upload");
		System.out.println(path);
		File file = new File(path);
		File[] fileList = file.listFiles();
		for (File file2 : fileList) {
			if(file2.isFile()){
				System.out.println(file2.getName());
				request.setAttribute("filename", file2.getName());
			}
		}
		request.getRequestDispatcher("/WEB-INF/page/downloadA.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
