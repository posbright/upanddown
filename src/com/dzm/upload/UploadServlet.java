package com.dzm.upload;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * 文件上传Servlet,实现一个或者多个文件上传
 * @author dzm
 *
 */

public class UploadServlet extends HttpServlet {

	/**
	 * 序列号，添加之后可以防止报警告
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String path = req.getServletContext().getRealPath("/WEB-INF/upload");
		System.out.println("获得文件件要上传的服务器路径为："+path);
		File file = new File(path);
		if(!file.exists()&& !file.isDirectory()){
			System.out.println("文件不存在，正在为你创建！！");
			file.mkdir();
		}
		/*
		 * 使用Apache上传文件步骤：
		 * 1 创建文件上传DiskFileItemFactory工厂
		 */
		DiskFileItemFactory factory  = new DiskFileItemFactory();
		//2 创建文件解析器并传入文件工厂
		ServletFileUpload upload = new ServletFileUpload(factory);
		//解决上传文件中文名乱码问题
		upload.setHeaderEncoding("UTF-8");
		//判断上传的文件是否是普通的form表单
		if(!ServletFileUpload.isMultipartContent(req)){
			//按传统方式获取数据
			
			return;
		}
		try {
			List<FileItem> fileItem = upload.parseRequest(req);
			String mess = "";
			for (FileItem item : fileItem) {
				//判断获得的组件是否为普通form表单数据
				if(item.isFormField()){
					//获得该组件字段的名字
					String name = item.getFieldName();
					//得到该字段的值
					String value = item.getString("UTF-8");
					System.out.println(name+"  ==  "+value);
				}else{
					//得到文件的名字
					String fileName = item.getName();
					System.out.println(fileName);
					if(fileName==null || fileName.trim().equals("")){
					continue;
					}
					//注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
					//处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
					//获取item中的上传文件的输入流
					String file1 = UUID.randomUUID()+fileName;
					File fileup = new File(file,file1);
					try {
						//将文件写入到指定文件夹下
						item.write(fileup);
						item.delete();
						mess = "文件上传成功！！";
					} catch (Exception e) {
						e.printStackTrace();
						mess = "文件上传失败";
					}
				}
				
			}
			req.setAttribute("message", mess);
			req.getRequestDispatcher("/WEB-INF/page/success.jsp").forward(req, resp);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
