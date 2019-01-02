package com.ncu.processors;
import com.ncu.validators.*;

import java.io.BufferedInputStream;
import java.io.*;
import java.nio.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class FileServer {

	private ServerSocket serverSocket;
	String constantconfig = System.getProperty("user.dir")+ File.separator + "configs/constants/constants.properties";  
	public final static String SERVER_DIRECTORY = System.getProperty("user.dir") + File.separator + "Storage/server/";
	String log4jConfigFile = System.getProperty("user.dir")+ File.separator + "configs/logger/logger.properties";
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	DataInputStream dis=null;
	DataOutputStream dos=null;
	Logger logger = Logger.getLogger(FileServer.class);


	public FileServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//  start method for client hearing
	public void start(){
		PropertyConfigurator.configure(log4jConfigFile);
		try{	
			logger.info("Waiting for connection...");
			Socket clientSocket = serverSocket.accept();
			logger.info("Accepted connection : " + clientSocket);
			System.out.println("Waiting for Command ...");
			while (true) {

				try
				{
					FileInputStream configObj = new FileInputStream(constantconfig);
					Properties propSetObject = new Properties();
					propSetObject.load(configObj);
					this.dis=new DataInputStream(clientSocket.getInputStream());
					String Command=this.dis.readUTF();
					if(Command.compareTo(propSetObject.getProperty("GET_MESSAGE"))==0)
					{
						logger.info("\tGET Command Received ...");
						this.sendFile(clientSocket);
					}
					else if(Command.compareTo(propSetObject.getProperty("SEND_MESAGE"))==0)
					{
						logger.info("\tSEND Command Receiced ...");               
						this.recieveFile(clientSocket);
					}
					// else if(Command.compareTo("DISCONNECT")==0)
					// {
     //       this.dos.close();
     //       this.dis.close();
     //       serverSocket.close();
     //       FileServer fs =new FileServer(13267);
     //       fs.start();
					// }
				}catch(Exception e){}

			}
		}catch(Exception e){}
	}

// send file to client
	public void sendFile(Socket clientSock) throws IOException{
		PropertyConfigurator.configure(log4jConfigFile);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			FileInputStream configObj = new FileInputStream(constantconfig);
			Properties propSetObject = new Properties();
			propSetObject.load(configObj);
			String fileName=this.dis.readUTF();
			String filepath = SERVER_DIRECTORY + fileName;
			File file=new File(filepath);
			this.dos=new DataOutputStream(clientSock.getOutputStream());
			if(!file.exists())
			{
				this.dos.writeUTF(propSetObject.getProperty("FILE_NOT_FOUND"));
				return;
			}
			else{
				try {
					this.dos.writeUTF(propSetObject.getProperty("READY"));
					String clientMessage=this.dis.readUTF();
					if(clientMessage.compareTo("Cancel")==0){
						return;	
					}else if(clientMessage.compareTo(propSetObject.getProperty("OVERWRITE"))==0){
						File myFile = new File (filepath);
						logger.info("Sending.. " + fileName);
						byte [] mybytearray  = new byte [(int)myFile.length()];
					// to read byte oriented data from file
						fis = new FileInputStream(myFile);
						bis = new BufferedInputStream(fis);
					// It read the bytes from the specified byte-input stream into a specified byte array
						bis.read(mybytearray,0,mybytearray.length);
						os = clientSock.getOutputStream();
						os.write(mybytearray,0,mybytearray.length);
						System.out.println("Done.");
					}
				}catch(Exception e){}

			}}
			catch(Exception e){}
		}

  // recieve file from client
		public void recieveFile(Socket clientSock) throws IOException{
			String fileName = this.dis.readUTF();
			FileInputStream configObj = new FileInputStream(constantconfig);
			Properties propSetObject = new Properties();
			propSetObject.load(configObj);
			if(fileName.compareTo(propSetObject.getProperty("FILE_NOT_FOUND"))==0)
			{
				return;
			}
			String filepath = SERVER_DIRECTORY + fileName;
			FileOutputStream fos = new FileOutputStream(filepath);
			Integer FILE_SIZE = Integer.parseInt(propSetObject.getProperty("FILE_SIZE"));
			byte[] buffer = new byte[FILE_SIZE];

			int filesize = FILE_SIZE;
			int read = 0;
			int totalRead = 0;
			int remaining = this.dis.available();
			while((read = this.dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				fos.write(buffer, 0, read);
			}
			logger.info("Recieved " +fileName+"("+ totalRead+" bytes) successfully");
			fos.close();		
		}
	}