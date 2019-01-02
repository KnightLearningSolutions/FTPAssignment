package com.ncu.main;
import com.ncu.processors.*;

public class MainClient {

	public static void main(String args[]){
		try{ 
		FileClient fc = new FileClient();
		fc.displayMenu();
	}catch(Exception e)
	{
		System.out.println(e);
	}
	}
}