package gsn.utils.models.jgarch.wrappers;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

class TextConsole implements RMainLoopCallbacks
{
	public void rWriteConsole(Rengine re, String text, int oType) {
		System.out.print(text);
	}

	public void rBusy(Rengine re, int which) {
		System.out.println("rBusy("+which+")");
	}

	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
		System.out.print(prompt);
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			String s=br.readLine();
			return (s==null||s.length()==0)?s:s+"\n";
		} catch (Exception e) {
			System.out.println("jriReadConsole exception: "+e.getMessage());
		}
		return null;
	}

	public void rShowMessage(Rengine re, String message) {
		System.out.println("rShowMessage \""+message+"\"");
	}

	public String rChooseFile(Rengine re, int newFile) {
		FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
		fd.setVisible(true);
		String res=null;
		if (fd.getDirectory()!=null) res=fd.getDirectory();
		if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
		return res;
	}

	public void   rFlushConsole (Rengine re) {
	}

	public void   rLoadHistory  (Rengine re, String filename) {
	}			

	public void   rSaveHistory  (Rengine re, String filename) {
	}			
}

public class REngineManager {
	// Private constructor prevents instantiation from other classes
	private Rengine re;

	public enum EngineState {RUNNING, STOPPED};
	private EngineState engineState;

	private void createREngine(){
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		//    (that's the "false" as second argument)
		// 3) the callbacks are implemented by the TextConsole class above

		String[] Rargs = {"--no-save"};

		re=new Rengine(Rargs, false, new TextConsole());

		engineState = EngineState.RUNNING;

		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			engineState = EngineState.STOPPED;
			return;		
		}		
		initREngine();
	}
	
	private void initREngine() {
		re.eval("library(tseries)");
	}
	
	
	private REngineManager() {
		createREngine();
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class REngineManagerHolder { 
		private static final REngineManager INSTANCE = new REngineManager();
	}

	public static REngineManager getInstance() {
		return REngineManagerHolder.INSTANCE;
	}

	public Rengine getREngine() {
		if (engineState == EngineState.RUNNING) {
		return re;
		} else {
			return restartEngine();
		}
	}

	public void endEngine(){
		re.end();
		engineState = EngineState.STOPPED;
	}

	private Rengine restartEngine(){

		if (engineState == EngineState.RUNNING){
			System.out.println("R Engine Already Running...");			
		} else if(engineState == EngineState.STOPPED) {
			createREngine();			
		}
		
		return re;
	}
}