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
 * �����ҷ����
 * @author JazzXdh
 *
 */
public class Server {
	/*
	 * java.net.ServerSocket
	 * �����ڷ���˵�ServerSocket��Ҫ����������
	 * 1:�������˿�
	 * 2:��������˿ڣ�һ��һ���ͻ���ͨ���ö˿ڽ���
	 *   ���ӣ��򴴽�һ��Socket������ÿͻ���ͨѶ
	 */
	private ServerSocket server;
	/*
	 * �ü�������������пͻ��˵�����������ڽ���Ϣ
	 * �㲥�����пͻ���
	 */
	private List<PrintWriter> allOut;
	
	public Server() throws IOException{
		/*
		 * ��ʼ��ServerSocket��ͬʱ��Ҫָ������˿�
		 * �ö˿ںŲ�����ϵͳ����Ӧ�ó���������Ķ˿�
		 * ���ظ���������׳��쳣��
		 */
		server = new ServerSocket(8088);
		
		allOut = new ArrayList<PrintWriter>();
	}
	
	public void start(){
		try {
			/*
			 * ServerSocket�ṩ����:
			 * Socket accept()
			 * �÷��������ServerSocket����ķ���˿�
			 * ������һ������������ֱ��һ���ͻ���ͨ��
			 * �ö˿����ӲŻ᷵��һ��Socket���������
			 * ��Socket�����������ӵĿͻ��˽���ͨѶ�ġ�
			 * 
			 */
			while(true){
				System.out.println("�ȴ��ͻ�������...");
				Socket socket = server.accept();
				System.out.println("һ���ͻ���������!");
				/*
				 * ����һ���߳���ÿͻ��˽���
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
	 * ����������Ϣ�㲥�����пͻ���
	 * @param message
	 */
	private void sendMessage(String message){
		synchronized (allOut) {
			//ת�������пͻ���
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
	 * ���߳����������ָ���ͻ��˽����Ĺ���
	 * @author adminitartor
	 *
	 */
	private class ClientHandler implements Runnable{
		/*
		 * ��ǰ�߳�ͨ�����Socket��ָ���ͻ��˽���
		 */
		private Socket socket;
		/*
		 * Զ�̼������ַ��Ϣ�������ǿͻ��˵ĵ�ַ
		 */
		private String host;
		
		public ClientHandler(Socket socket){
			this.socket = socket;
			/*
			 * ͨ��Socket���Ի�ȡԶ�˼������ַ��Ϣ
			 */
			InetAddress address 
				= socket.getInetAddress();
			/*
			 * ��ȡԶ�˼����IP��ַ���ַ�����ʽ
			 */
			host = address.getHostAddress();
		}
		
		public void run(){
			PrintWriter pw = null;
			try {			
				/*
				 * InputStream getInputStream()
				 * Socket�ṩ�ĸ÷������Ի�ȡһ����������
				 * ͨ�������ͻ���ȡ��Զ�˼�������͹���
				 * �����ݡ�
				 */
				InputStream in = socket.getInputStream();
				InputStreamReader isr
					= new InputStreamReader(in,"UTF-8");
				BufferedReader br
					= new BufferedReader(isr);
				
				/*
				 * ͨ��Socket��ȡ����������ڽ����ݷ���
				 * ���ͻ���
				 */
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw
					= new OutputStreamWriter(out,"UTF-8");
				pw = new PrintWriter(osw,true);
				
				/*
				 * ���ÿͻ��˵���������뵽��������
				 * 
				 * ���ڶ���̶߳�����øü��ϵ�add����������
				 * ��������������Ϊ�˱�֤�̰߳�ȫ�����Խ�
				 * �ü��ϼ�����
				 */
				synchronized (allOut) {
					allOut.add(pw);
				}
				
				
				
				
				sendMessage(host+"������!,��ǰ����"+allOut.size()+"��");
				
				String message = null;
				while((message = br.readLine())!=null){
//					System.out.println(host+"˵:"+message);
					//�ظ�����ǰ�ͻ���
//					pw.println(host+"˵:"+message);
					
					sendMessage(host+"˵:"+message);
				}
				
				
			} catch (Exception e) {
				
			} finally{
				//����ͻ��˶Ͽ������Ժ�Ĺ���
				
				synchronized (allOut) {
					//���ÿͻ��˵�������ӹ�������ɾ��
					allOut.remove(pw);
				}
				
				
				sendMessage(host+"������!,��ǰ����"+allOut.size()+"��");
				
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








