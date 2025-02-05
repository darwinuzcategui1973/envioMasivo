import java.math.*;
import java.io.*;

public class TextToWord{ 
  public static void main(String arg[]){
    try{
      FileOutputStream fs = new FileOutputStream("c:/temp/TextToWord.doc");
      OutputStreamWriter out = new OutputStreamWriter(fs);            
      out.write("<H1>Welcome to RoseIndia!</H1>");
      out.close();
    }
    catch (IOException e){
      System.err.println(e);
    }
  }
}