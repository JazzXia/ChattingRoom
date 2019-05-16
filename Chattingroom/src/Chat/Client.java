package Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * �����ҿͻ���
 * @author JazzXdh
 *
 */
public class Client {
	/*
	 * java.net.Socket
	 * ��װ��TCPͨѶЭ�飬ʹ������Զ�̼��������
	 * ����ͨѶ��
	 */
	private Socket socket;
	/**
	 * ���췽����������ʼ���ͻ���
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public Client() throws UnknownHostException, IOException{
		/*
		 * ʵ����Socket����Ҫ������������
		 * 1:�����ip��ַ
		 * 2:����˶˿�
		 * ͨ��IP��ַ�����ҵ������ϵķ�������ڵļ����
		 * ͨ���˿ڿ������ӵ��ü�����ϵķ����Ӧ�ó���
		 * 
		 * ʵ����Socket�Ĺ��̾��ǽ������ӵĹ��̣�������
		 * ���ӷ����ʧ�ܣ�������׳��쳣��
		 */
		System.out.println("���������˽�������...");
		socket = new Socket("localhost",8088);
		System.out.println("���������ӳɹ�!");
	}
	/**
	 * �ͻ��˵����������������￪ʼִ�пͻ����߼�
	 */
	public void start(){
		try {
			Scanner scanner = new Scanner(System.in);
			
			OutputStream out = socket.getOutputStream();
			
			OutputStreamWriter osw
				= new OutputStreamWriter(out,"UTF-8");
			
			PrintWriter pw
				= new PrintWriter(osw,true);
			
			//������ȡ����˷��͹�����Ϣ���߳�
			ServerHandler handler 
				= new ServerHandler();
			Thread t = new Thread(handler);
			t.start();
			
			
			String line = null;
			System.out.println("��ʼ�����!");
			//�ϴη��ͺ��ʱ��
			long lasttime = System.currentTimeMillis();
			while(true){
				line = scanner.nextLine();
				long curr = System.currentTimeMillis();
				//���η���ʱ�����ϴη���ʱ��Ҫ���1������
				if(curr-lasttime>1000){
					pw.println(line);
					lasttime = curr;
				}else{
					System.out.println("˵���ٶ�̫��!");
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�ͻ�������ʧ��!");
		}
	}
	
	/**
	 * ���߳�����ѭ�����շ���˷��͹�������Ϣ�������
	 * �ͻ����Լ��Ŀ���̨��
	 * @author adminitartor
	 *
	 */
	private class ServerHandler implements Runnable{
		public void run(){
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr
					= new InputStreamReader(in,"UTF-8");
				BufferedReader br
					= new BufferedReader(isr);
				
				String message = null;
				while((message = br.readLine())!=null){
					System.out.println(message);
				}
					
				
				
			} catch (Exception e) {
				
			}
		}
	}
}







