import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class HelloWorld {

   public interface CLibrary extends Library {
       CLibrary INSTANCE = (CLibrary)Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), 
          CLibrary.class);
  
       void printf(String format, Object... args);
   }

   public static void main(String[] args) {
       CLibrary.INSTANCE.printf("Hola mundo!\n");
       for (int i=0;i < args.length;i++) {
           CLibrary.INSTANCE.printf("Argumento %d: %s\n", i, args[i]);
       }     
   } 
}