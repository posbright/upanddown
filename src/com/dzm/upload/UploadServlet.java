package com.dzm.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * 文件上传Servlet,实现一个或者多个文件上传
 * @author dzm
 * 
 * 文件上传注意事项
 * 1、为保证服务器安全，上传文件应该放在外界无法直接访问的目录下，比如放于WEB-INF目录下。

　　2、为防止文件覆盖的现象发生，要为上传文件产生一个唯一的文件名。

　　3、为防止一个目录下面出现太多文件，要使用hash算法打散存储。

　　4、要限制上传文件的最大值。

　　5、要限制上传文件的类型，在收到上传文件名时，判断后缀名是否合法。
 *
 */

public class UploadServlet extends HttpServlet {

	/**
	 * 序列号，添加之后可以防止报警告
	 */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//设置请求和响应的字符编码
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		//获得文件上传到服务器的服务器地址
		String path = req.getServletContext().getRealPath("/WEB-INF/upload");
		String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
		
		System.out.println("获得文件件要上传的服务器路径为："+path);
		//根据指定的文件存放地址，创建文件
		File file = new File(path);
		//判断文件是否存在且是一个目录
		if(!file.exists()&& !file.isDirectory()){
			System.out.println("文件不存在，正在为你创建！！");
			//不存在就创建一个文件
			file.mkdir();
		}
		/*
		 * 使用Apache上传文件步骤：
		 * 1 创建文件上传DiskFileItemFactory工厂
		 */
		DiskFileItemFactory factory  = new DiskFileItemFactory();
		//2 创建文件解析器并传入文件工厂
		ServletFileUpload upload = new ServletFileUpload(factory);
		//设置上传文件时工厂生成的临时文件存放目录
		factory.setRepository(new File(tempPath));
		//设置工厂缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
		factory.setSizeThreshold(1024 * 100);// 设置文件的最大不超过100KB,如果不指定默认是10KB
		// 监听文件上传进度
		upload.setProgressListener(new ProgressListener() {
			public void update(long pBytesRead, long pContentLength, int arg2) {
				System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
				/**
				 * 文件大小为：14608,当前已处理：4096 
				 * 文件大小为：14608,当前已处理：7367
				 * 文件大小为：14608,当前已处理：11419
				 *  文件大小为：14608,当前已处理：14608
				 */
			}
		});
		//解决上传文件中文名乱码问题
		upload.setHeaderEncoding("UTF-8");
		//定义一个消息变量
		String mess = "";
		//判断上传的文件是否是普通的form表单
		if(!ServletFileUpload.isMultipartContent(req)){
			//按传统方式获取数据
			
			return;
		}
		// 设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
		upload.setFileSizeMax(1024 * 1024);
		// 设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
		upload.setSizeMax(1024 * 1024 * 10);
		try {
			//通过文件解析器解析request 请求。获得文件组件列表
			List<FileItem> fileItem = upload.parseRequest(req);
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
						/*
						 * 或者通过文件输入或者输出流进行文件的读写
						 * 
						 * //获取item中的上传文件的输入流
						InputStream in = item.getInputStream();
						//创建一个文件输出流
						FileOutputStream out = new FileOutputStream(path + "\\" + file1);
						//创建一个缓冲区
						byte buffer[] = new byte[1024];
						//判断输入流中的数据是否已经读完的标识
						int len = 0;
						//循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
						while((len=in.read(buffer))>0){
						//使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
						out.write(buffer, 0, len);
						}
						//关闭输入流
						in.close();
						//关闭输出流
						out.close();*/
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
