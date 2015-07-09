package Server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import Program.Reconhecedor;

public class recognizerServer {
	
	private Logger logger;
	ServerSocket server;
	int contThread;

	public recognizerServer()
	{
		contThread = 1;
		logger = Logger.getLogger(this.getClass().getName());
		logger.info("trying to instantiate");
		try 
		{
			server = new ServerSocket(11112);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		logger.info("succeeded to instantiate");
	}
	
	public void accept()
	{
		//aceita toda conexão em loop
		logger.info("Started to listen to clients");
		while(true)
		{
			try 
			{
				//aceita a nova conexão e dispara uma thread para atende-la
				Socket client = server.accept();
				logger.info("New client accepted");
				recognizerListener recognizerThread = new recognizerListener(client,contThread);
				recognizerThread.start();
				logger.info("Client thread started");
				contThread++;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
