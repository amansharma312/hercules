/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hercules.hercules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.hercules.hercules.Frame2;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.*;

public class SmartLocker {
	//private static Logger logger = LoggerFactory.getLogger(SmartLocker.class.getName());
	static int BaudRate = 115200;
	static int DataBits = 8;
	static int StopBits = SerialPort.ONE_STOP_BIT;
	static int Parity   = SerialPort.NO_PARITY;
	
	static Scanner scanner_stream;
	static String received_string;
	static int received_str_len;
	
	
	
	public static void main (String[] Args)
	   {
                
                //new SmartLocker().openLock("EDAA0001f2");
		 SerialPort MySerialPort;
	     SerialPort [] AvailablePorts = SerialPort.getCommPorts();

	       // use the for loop to print the available serial ports
	         for(SerialPort S : AvailablePorts)
	              System.out.println(S.toString());
	
	         MySerialPort = AvailablePorts[0];
	         
	       //Sets all serial port parameters at one time
	     	MySerialPort.setComPortParameters(BaudRate,
	     	                                  DataBits,
	     	                                  StopBits,
	     	                                    Parity);

	     	//Set Read Time outs
	     	MySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 
	     	                                 1000, 
	     	                                    0); 

	     	MySerialPort.openPort(); 
	     	         
	         if (MySerialPort.isOpen())//Check whether port open/not
	              System.out.println("is Open ");
	        else
	           System.out.println(" Port not open ");
	         
	         
	         comm("D0",MySerialPort);
	         
	         MySerialPort.addDataListener(new SerialPortDataListener() 
				{
					@Override
					public int getListeningEvents() 
					{
						return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
					}
			        
					public void serialEvent(SerialPortEvent event) 
					{
						if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			                 return;
						
	 				    scanner_stream= new Scanner( MySerialPort.getInputStream()); 
	 					while(scanner_stream.hasNextLine()) 
	 					{
	 						received_string= scanner_stream.nextLine();
	 						 
	 						received_str_len=received_string.length();
	 						String string = received_string;
	 						
	 						
	 						try {
								if("false".equals( requestHandler())) {
										
									comm(string.substring(1), MySerialPort);
						 									
										System.out.println("rec_string1 "+string);
										byte[] rec_byte = string.getBytes();
										
										 StringBuilder str_builder = new StringBuilder();
								        for (int i = 0; i < rec_byte.length; i++)
								        {
								        	
								            str_builder.append(Integer.toString((rec_byte[i] & 0xff) + 0x100, 16).substring(1));
								        }
								        System.out.println("SB : "+str_builder.toString());
								        String publish_hex_str = str_builder.toString();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}       	
			 				        //publish(publish_hex_str); 
	 
	   	        		}
	 					
	 				}
			   }); 
	       
  
	         
	   }
	
	
	public static void openLock(String lockHex) {
		 SerialPort MySerialPort;
		try {
			 
				 SerialPort [] AvailablePorts = SerialPort.getCommPorts();
				 System.out.println("Avaiable Ports lenth:"+AvailablePorts.length);
		
			       // use the for loop to print the available serial ports
			         for(SerialPort S : AvailablePorts)
			              //System.out.println(S.toString());
			        	System.out.println("Serial Port S.toString() :"+S.toString());
			
				  for(SerialPort S : AvailablePorts)
		              //System.out.println(S.toString());
				  System.out.println("Serial Port 2 nd Loop S.toString() :"+S.toString());
		
		         MySerialPort = AvailablePorts[0];
                           System.out.println("MySerial Port :"+AvailablePorts[0]);
                           System.out.println("Baud Rate :"+BaudRate +"DataBits :"+DataBits +"StopBits :"+StopBits +"Parity :"+Parity);
		         
		       //Sets all serial port parameters at one time
		     	/*MySerialPort.setComPortParameters(BaudRate,
		     	                                  DataBits,
		     	                                  StopBits,
		     	                                    Parity);*/
                        MySerialPort.setComPortParameters(BaudRate, DataBits, StopBits, Parity);
		
		     	//Set Read Time outs
		     	MySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 
		     	                                 100000, 
		     	                                    0); 
		
		     	MySerialPort.openPort(); 
		     	
		     	 
		         if (MySerialPort.isOpen()){//Check whether port open/not
		              System.out.println("is Open ");
		              //logger.info("is Open");
		              comm(lockHex, MySerialPort);
		              MySerialPort.removeDataListener();
		              MySerialPort.closePort();
		              MySerialPort =null;
		         }else {
		           System.out.println(" Port not open ");
		           //logger.info("Port not open");
		         
		         }
		}catch(Exception e) {
			//logger.info("Error openLock function :"+e);
                        e.printStackTrace();
		}
         
	}
	public static void comm(String s,SerialPort MySerialPort) {
		
		  try 

	         {
	        	 byte[] ans = new byte[s.length() / 2];
	             
	             System.out.println("Hex String : "+s);
	            //logger.info("Hex String : "+s);
	         
	             for (int i = 0; i < ans.length; i++) {
	                 int index = i * 2;
	                
	                   // Using parseInt() method of Integer class
	                 int val = Integer.parseInt(s.substring(index, index + 2), 16);
	                 ans[i] = (byte)val;
	             }
	        	 
	        	 
	        	 
	               int bytesTxed  = 0;
	          
	               bytesTxed  = MySerialPort.writeBytes(ans, ans.length);
	          
	               System.out.print(" Bytes Transmitted -> " + bytesTxed );
	               //logger.info(" Bytes Transmitted -> " + bytesTxed );
	        	 
	                  
	         }catch (Exception e) {
				// TODO: handle exception
			} 
		
	}
	
	private static String   requestHandler() throws IOException
	{
		URL url=new URL("http://205.147.103.18:8080/hundi/rest/stateless/box");
		String readLine=null;
		HttpURLConnection conn= (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type","application/json");
		conn.setRequestProperty("versioncode","18");
		conn.setRequestProperty("authKey","G4s4cCMx2aM7lky1");
		conn.setRequestProperty("Accept", "*/*");
		
		//conn.setRequestProperty(readLine, readLine);
		//int responseCode=conn.getResponseCode();
		//if(responseCode==)
		BufferedReader in=new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		StringBuffer response=new StringBuffer();
		while((readLine=in.readLine())!=null){
			response.append(readLine);
			
		}
		in.close();
		System.out.println("Response :"+response.toString());
		return response.toString();
		
	}
	
	
}