import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;

public class HWEnv extends jason.environment.Environment {

  private Logger logger = Logger.getLogger("testing.mas2j."+HWEnv.class.getName());

  /** Called before the MAS execution with the args informed in .mas2j */
  @Override
  public void init(String[] args) {    
	  super.init(args);
	  try{
		  addPercept(ASSyntax.parseLiteral("percept(demo)"));
		  
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  
  }

  @Override
  public boolean executeAction(String agName, Structure action) {
	  logger.info("executing: "+action+", but not implemented!");
	  informAgsEnvironmentChanged();
	  return true; // the action was executed with success 
  }

  /** Called before the end of MAS execution */
  @Override
  public void stop() {
    super.stop();
  }
}
