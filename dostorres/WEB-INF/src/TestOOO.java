public class TestOOO {

	// java -cp .:../lib/bootstrapconnector.jar:/opt/openoffice.org/ure/share/java/ridl.jar TestOOO /opt/openoffice.org3/program
	
	/*
	public static void main(String[] args) {
		try {
			// get the remote office component context
			com.sun.star.uno.XComponentContext xContext = null; 
			if(args.length>0) {
				//System.out.println("Metodo nuevo");
				//System.out.println("RUTA:"+args[0]);
				xContext = BootstrapSocketConnector.bootstrap(args[0]);
			} else {
				//System.out.println("Metodo viejo");
				xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
			}
			//System.out.println("Connected to a running office ...");
			com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();
			String available = (xMCF != null ? "available" : "not available");
			//System.out.println("remote ServiceManager is " + available);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	**/
}
