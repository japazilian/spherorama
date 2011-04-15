import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProcessSphereThread extends Thread {
		private String file, path;
		public ProcessSphereThread(String file) {
			//file should be format xxx.zip
			path = file.substring(0, file.lastIndexOf("/"));
			this.file = file;
		}		
		
		public void run() {
			String sphereName = file.substring(file.lastIndexOf("/"), file.lastIndexOf(".zip"));
			System.err.println("making dir: " + path + "/mnt/sdcard/Spherorama"+ sphereName);
			File f = new File(path+"/mnt/sdcard/Spherorama"+sphereName);
			f.mkdirs();
			unzipFile();
			File delZipFile = new File(file);
			delZipFile.delete();
		}
		
		public void unzipFile() {
			try {
      ZipFile zipFile = new ZipFile(file);

      Enumeration entries = zipFile.entries();

      while(entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry)entries.nextElement();

        if(entry.isDirectory()) {
          // Assume directories are stored parents first then children.
          System.err.println("Extracting directory: " + entry.getName());
          // This is not robust, just for demonstration purposes.
          (new File(entry.getName())).mkdir();
          continue;
        }

        System.err.println("Extracting file: " + entry.getName());
        copyInputStream(zipFile.getInputStream(entry),
           new BufferedOutputStream(new FileOutputStream(path+entry.getName())));
      }

      zipFile.close();
		  } catch (IOException ioe) {
		    System.err.println("Unhandled exception:");
		    ioe.printStackTrace();
		    return;
		  }
		}
		
		public void copyInputStream(InputStream in, OutputStream out) throws IOException
		{
		  byte[] buffer = new byte[1024];
		  int len;

		  while((len = in.read(buffer)) >= 0)
		    out.write(buffer, 0, len);

		  in.close();
		  out.close();
		}
		
	} // End Thread