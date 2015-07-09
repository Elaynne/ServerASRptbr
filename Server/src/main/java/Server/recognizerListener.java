package Server;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;










import Program.Reconhecedor;

public class recognizerListener extends Thread {
	
	private Reconhecedor reconhecedor;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	Socket client;
	PrintWriter out;
	DataInputStream in;
	int id;
	
	public recognizerListener(Socket client, int id)
	{
		this.id = id;
		reconhecedor = new Reconhecedor();
		this.client = client;
		
		try 
		{
			out = new PrintWriter(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	
	public void run() 
	{
        this.listen();
	}
	
	private void listen()
	{
		try 
		{
			while(!client.isClosed())
			{
				File f_input = null, f_output = null;
				//logger.info("waiting to read from client");
				
				ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
				int count;
				
				try
				{
					count = in.readInt();
				}
				catch(EOFException e)
				{
					continue;
				}
				
				
				logger.info("receiving " + count + " bytes of audio");
				
				byte[] message = null;
				
				if(count > 0) 
				{
					message = new byte[count];
				    in.readFully(message, 0, message.length); // read the message
				    
				    //salva o audio enviado no disco
				    f_input = new File("audioinput" + id + ".3gp");
					f_input.createNewFile();
					
					FileOutputStream fos = new FileOutputStream(f_input);
					fos.write(message);
					fos.flush();
					fos.close();
					
					//salva o recipiente .wav no disco
					f_output = new File("audiooutput" + id + ".wav");
					f_output.createNewFile();
					
					//configura a codificação para .wav
					AudioAttributes audio = new AudioAttributes();
					//audio.setCodec("pcm_s8");
					audio.setBitRate(44100);
					audio.setChannels(1);
					audio.setSamplingRate(22050);
					
					EncodingAttributes attrs = new EncodingAttributes();
					attrs.setFormat("wav");
					attrs.setAudioAttributes(audio);
					
					Encoder encoder = new Encoder();
					//codifica
					try {
						encoder.encode(f_input, f_output, attrs);
					} catch (IllegalArgumentException | EncoderException e) {
						e.printStackTrace();
					}
					
					//le o arquivo já codificado para .wav
					
			    	BufferedInputStream in = new BufferedInputStream(new FileInputStream("audiooutput" + id + ".wav"));
			
			    	int read;
			    	byte[] buff = new byte[1024];
			    	
			    	while ((read = in.read(buff)) > 0)
			    	{
			    		outByteStream.write(buff, 0, read);
			    	}
			    	in.close();
			    	outByteStream.flush();
			    	
				}
				else
					client.close();
				
				if(count > 0)
				{
					logger.info("reading done, recognizing now");
					
					String result = "Not recognized";
			    	ByteArrayInputStream music = new ByteArrayInputStream(outByteStream.toByteArray());
			    	AudioInputStream ais;
			        try {
			        	logger.info("Transforming in audioStream");
			        	ais = AudioSystem.getAudioInputStream(music);
			        	logger.info("Audio transformed with success");
			        	result = reconhecedor.Recognize(ais);
			        	logger.info("returning: " + result);
			            //session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Game ended"));
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			        
			        out.println(result);
			        out.flush();
			        if (f_input != null && f_output != null)
			        {
			        	f_input.delete();
			        	f_output.delete();
			        }
				}
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
