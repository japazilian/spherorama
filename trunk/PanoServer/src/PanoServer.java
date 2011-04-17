import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class PanoServer {
	public static void main(String[] args) {
		if(args.length<2) {
			System.out.println("Usage: java PanoServer [port] [password]");
			System.exit(0);
		}
		try {
			ServerSocket s = new ServerSocket(Integer.parseInt(args[0]));
			while(true) {
				Socket incoming = s.accept();
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
                PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
                
                String password = in.readLine();
                Date date = new Date();
                if(password.equals(args[1])) {
                	System.out.println(date.toString()+" - New client.");
                	System.out.println("From: "+incoming.getInetAddress());
                	out.println("passed");
                }
                else {
                	System.out.println(date.toString()+" - Password failed: "+password);
                	System.out.println("Attempt from: "+incoming.getInetAddress());
                	out.println("failed");
                	out.flush();
                	in.close();
                	out.close();
                	incoming.close();
                	continue;
                }
                
                String filename = in.readLine();  
            	System.out.println("File name: "+filename);
                File f = new File(filename);
                f.createNewFile();
                
                byte[] receivedData = new byte[8192];
                BufferedInputStream bis = new BufferedInputStream(incoming.getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                //length = bis.read(receivedData);
                int num;
                while ((num = bis.read(receivedData)) != -1){
                	bos.write(receivedData,0,num);
                }
                bis.close();
                bos.close();
                
                ProcessSphereThread p = new ProcessSphereThread(f.getAbsolutePath());
                p.start();
				
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
}
