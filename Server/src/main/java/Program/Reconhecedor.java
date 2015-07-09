package Program;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import javax.sound.sampled.AudioInputStream;
 
public class Reconhecedor {
    private ConfigurationManager cm;
    private Recognizer recognizer;
    private AudioFileDataSource dataSource;
    private Logger logger;
    
    public Reconhecedor(){
    	logger = Logger.getLogger(this.getClass().getName());
    	logger.info("Before configuration");
    	
    	URL path = Reconhecedor.class.getResource("/Modelo/recognizer.config.xml"); //"/app-root/runtime/repo/src/main/java/Modelo";
		
		cm =  new ConfigurationManager(path);
		
    	recognizer = (Recognizer) cm.lookup("recognizer");
    	recognizer.allocate();
    	dataSource = (AudioFileDataSource) cm.lookup("audioFileDataSource");
    	
    	
        logger.info("Reconhecedor instantiated with success");

    }
 
    public String Recognize(AudioInputStream audio){
    	
    	dataSource.setInputStream(audio, null);
    	Result result = recognizer.recognize();
    	
		return result.getBestFinalResultNoFiller().compareTo("") == 0 ? "Não reconheceu" : result.getBestFinalResultNoFiller();
    }
}