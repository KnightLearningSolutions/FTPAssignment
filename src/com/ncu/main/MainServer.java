package com.ncu.main;
import com.ncu.processors.*;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class MainServer {
	String constantconfig = System.getProperty("user.dir")+ File.separator + "configs/constants/constants.properties";  

	// public final static int SOCKET_PORT = 13267;  // you may change this

	public static void main(String args[]){

		MainServer ms = new MainServer();
		ms.start();
	}

	public void start(){
		try{
		FileInputStream configObj = new FileInputStream(constantconfig);
		Properties propSetObject = new Properties();
		propSetObject.load(configObj);
		Integer port = Integer.parseInt(propSetObject.getProperty("SOCKET_PORT"));
		FileServer fs= new FileServer(port);	
		fs.start();
	}catch(Exception e){}
}

}