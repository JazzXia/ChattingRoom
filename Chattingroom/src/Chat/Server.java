package Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室服务端
 * @author JazzXdh
 *
 */
public class Server {
	/*
	 * java.net.ServerSocket
	 * 运行在服务端的ServerSocket主要有两个作用
	 * 1:申请服务端口
	 * 2:监听服务端口，一旦一个客户端通过该端口建立
	 *   连接，则创建一个Socket用于与该客户端通讯
	 */
	private ServerSocket server;
	/*
	 * 该集合用来存放所有客户端的输出流，用于将消息
	 * 广播给所有客户端
	 */
	private List<PrintWriter> allOut;
	
	public Server() throws IOException{
		/*
		 * 初始化ServerSocket的同时需要指定服务端口
		 * 该端口号不能与系统其他应用程序已申请的端口
		 * 号重复，否则会抛出异常。
		 */
		server = new ServerSocket(8088);
		
		allOut = new ArrayList<PrintWriter>();
	}
	
	public void start(){
		try {
			/*
			 * ServerSocket提供方法:
			 * Socket accept()
			 * 该方法会监听ServerSocket申请的服务端口
			 * 。这是一个阻塞方法，直到一个客户端通过
			 * 该端口连接才会返回一个Socket。这个返回
			 * 的Socket是用于与连接的客户端进行通讯的。
			 * 
			 */
			while(true){
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				System.out.println("一个客户端连接了!");
				/*
				 * 启动一个线程与该客户端交互
				 */
				ClientHandler handler
					= new ClientHandler(socket);
				Thread t = new Thread(handler);
				t.start();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将给定的消息广播给所有客户端
	 * @param message
	 */
	private void sendMessage(String message){
		synchronized (allOut) {
			//转发给所有客户端
			for(PrintWriter o : allOut){
				o.println(message);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 该线程用来完成与指定客户端交互的工作
	 * @author adminitartor
	 *
	 */
	private class ClientHandler implements Runnable{
		/*
		 * 当前线程通过这个Socket与指定客户端交互
		 */
		private Socket socket;
		/*
		 * 远程计算机地址信息，这里是客户端的地址
		 */
		private String host;
		
		public ClientHandler(Socket socket){
			this.socket = socket;
			/*
			 * 通过Socket可以获取远端计算机地址信息
			 */
			InetAddress address 
				= socket.getInetAddress();
			/*
			 * 获取远端计算机IP地址的字符串格式
			 */
			host = address.getHostAddress();
		}
		
		public void run(){
			PrintWriter pw = null;
			try {			
				/*
				 * InputStream getInputStream()
				 * Socket提供的该方法可以获取一个输入流，
				 * 通过该流客户读取到远端计算机发送过来
				 * 的数据。
				 */
				InputStream in = socket.getInputStream();
				InputStreamReader isr
					= new InputStreamReader(in,"UTF-8");
				BufferedReader br
					= new BufferedReader(isr);
				
				/*
				 * 通过Socket获取输出流，用于将数据发送
				 * 给客户端
				 */
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw
					= new OutputStreamWriter(out,"UTF-8");
				pw = new PrintWriter(osw,true);
				
				/*
				 * 将该客户端的输出流存入到共享集合中
				 * 
				 * 由于多个线程都会调用该集合的add方法向其中
				 * 添加输出流，所以为了保证线程安全，可以将
				 * 该集合加锁。
				 */
				synchronized (allOut) {
					allOut.add(pw);
				}
				
				
				
				
				sendMessage(host+"上线了!,当前在线"+allOut.size()+"人");
				
				String message = null;
				while((message = br.readLine())!=null){
//					System.out.println(host+"说:"+message);
					//回复给当前客户端
//					pw.println(host+"说:"+message);
					
					sendMessage(host+"说:"+message);
				}
				
				
			} catch (Exception e) {
				
			} finally{
				//处理客户端断开连接以后的工作
				
				synchronized (allOut) {
					//将该客户端的输出流从共享集合中删除
					allOut.remove(pw);
				}
				
				
				sendMessage(host+"下线了!,当前在线"+allOut.size()+"人");
				
				if(socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}








