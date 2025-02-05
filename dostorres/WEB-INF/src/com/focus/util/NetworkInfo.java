package com.focus.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/** try to determine MAC address of local network card; this is done
 using a shell to run ifconfig (linux) or ipconfig (windows). The
 output of the processes will be parsed.

 <p>

 To run the whole thing, just type java NetworkInfo

 <p>

 Current restrictions:

 <ul>
 <li>Will probably not run in applets

 <li>Tested Windows / Linux only

 <li>Tested J2SDK 1.4 only

 <li>If a computer has more than one network adapters, only
 one MAC address will be returned

 <li>will not run if user does not have permissions to run
 ifconfig/ipconfig (e.g. under linux this is typically
 only permitted for root)
 </ul>

 */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NetworkInfo {
	private static final Logger log = LoggerFactory.getLogger(NetworkInfo.class);

	public static String ipAddress = "";

	public final static String getMacAddress() throws IOException {
		String os = System.getProperty("os.name");

		String mac = "";
		try {
			if (os.startsWith("Windows")) {
				try {
					mac = windowsParseMacAddress(windowsRunIpConfigCommand());
				} catch (ParseException ex) {
					ex.printStackTrace();
				}
			} else if (os.startsWith("Linux")) {
				//mac = linuxParseMacAddress(linuxRunIfConfigCommand());
				//mac = mac.replaceAll("�??", ":").replaceAll("-", ":");
				mac = getMacLinux();
			} else if (os.startsWith("Mac OS X")) {
				//mac = getMacLinux();
				// TODO: JAIRO devolvemos una mac fija por el momento
				mac = "C2-70-E6-E1-87-C6";
			} else {
				throw new IOException("unknown operating system: " + os);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
		
		return mac.replaceAll("0", "F"); //antes de emitir la version
		//return "3E:95:F9:BE:4E:3D";
		
		//System.out.println("F0-4D-A2-73-1C-A0".replaceAll("�??", ":").replaceAll("-", ":").replaceAll("0", "F"));
		//return "F0-4D-A2-73-CC-A0".replaceAll("�??", ":").replaceAll("-", ":").replaceAll("0", "F");
	}
	
	public static String getMacLinux() {
		try {

			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface network = networkInterfaces.nextElement();
				System.out.println("network : " + network);
				byte[] mac = network.getHardwareAddress();
				if (mac == null) {
					System.out.println("null mac");
				} else {
					System.out.print("MAC address : ");

					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
					System.out.println(sb.toString());
					return sb.toString();
				}
			}
		} catch (SocketException e) {

			e.printStackTrace();

		}
		return null;
	}

	
	public final static String getSerialMac() throws IOException {
		return BaseConverterUtil.MacToBase36(getMacAddress());
	}
	
	public final static int getMacAddressSuma(String macAddress) throws IOException {
		String mac = macAddress.replaceAll("[^a-zA-Z0-9]", "");
		char[] numero = String.valueOf(Long.parseLong(mac, 16)).toCharArray();
		int suma = 0;
		for(int i=0;i<numero.length;i++) {
			suma += Integer.parseInt(String.valueOf(numero[i]));
		}
		return suma;
	}
	

	public final static String getHostAddress() throws Exception {
		String os = System.getProperty("os.name");

		try {
			if (os.startsWith("Windows")) {
				return getIpFromMachine();
			} else if (os.startsWith("Linux")) {
				return getIpFromMachine();
			} else {
				throw new IOException("unknown operating system: " + os);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
	}
	
    public static String getIpFromMachine() throws SocketException {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String ip;
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
	        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	        	ip = inetAddress.getHostAddress();
	        	if(ip.matches(PATTERN) && !ip.startsWith("127.")) {
	            	return ip;
	        	}
	        }
        }
        return "";
     }


	/*
	 * Linux stuff
	 */
	private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException {
		String localHost = null;
		// try {
		// localHost = InetAddress.getLocalHost().getHostAddress();
		localHost = getLinuxHostAddress(ipConfigResponse);
		// } catch(java.net.UnknownHostException ex) {
		// ex.printStackTrace();
		// throw new ParseException(ex.getMessage(), 0);
		// }

		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0;

			// see if line contains IP address
			if (containsLocalHost && lastMacAddress != null) {
				return lastMacAddress;
			}

			// see if line contains MAC address
			int macAddressPosition = -1;
			int n = 0;
			if (line.indexOf("HWaddr") != -1) {
				macAddressPosition = line.indexOf("HWaddr");
				n = 6;
			} else if (line.indexOf("nHW") != -1) {
				macAddressPosition = line.indexOf("nHW");
				n = 3;
			}

			if (macAddressPosition <= 0)
				continue;

			String macAddressCandidate = line.substring(macAddressPosition + n).trim();
			if (linuxIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
			
			log.info("lastMacAddress=" + lastMacAddress);
		}

		/*
		ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
		ex.printStackTrace();
		throw ex;
		*/
		// no enviaremos un error, enviaremos una mac generica
		return "3E:95:09:BE:4E:3D";
	}

	private final static boolean linuxIsMacAddress(String macAddressCandidate) {
		// TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	public static boolean linuxIsIPAddress(String dir) {
		// En java un StringTokenizer es una version mejorada del String
		// q permite darle formato a las cadenas. Al llamar el constructor
		// y pasar por ej. la direccion="255.192.168.1" y el String punto "." lo
		// q
		// hago es separar la ip y dejar a st={"255","192","168","1"}
		StringTokenizer st = new StringTokenizer(dir, ".");

		// ahora verifico q si el tama�o de st no es 4, osea, los 4 numeros
		// q debe tener toda direcicon ip, digo q esta mal la direccion ip.
		if (st.countTokens() != 4) {
			return false;
		}

		// ahora sigo verificando, y le digo a st con el metodo nextTokens()
		// q me de uno por uno sus elementos, es decir, los 4 numeros de las
		// de la direcicon ip. Luego verifo q el numero tenga solo 1 o 3 digitos
		// y
		// q este entre 0 y 255. Y listo, eso es todo.
		while (st.hasMoreTokens()) {
			String nro = st.nextToken();
			if ((nro.length() > 3) || (nro.length() < 1)) {
				return false;
			}
			int nroInt = 0;
			try {
				nroInt = Integer.parseInt(nro);
			} catch (NumberFormatException s) {
				return false;
			}
			if ((nroInt < 0) || (nroInt > 255)) {
				return false;
			}
		}
		return true;
	}


	/*
	private final static String linuxRunIfConfigCommand() throws IOException {
		//String[] cmd = { "/bin/sh", "-c", "ifconfig | grep 'inet '" };
		String[] cmd = { "/bin/sh", "-c", "ifconfig" };
		Process p = Runtime.getRuntime().exec(cmd);
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();

		stdoutStream.close();

		return outputText;
	}*/

	/*
	 * Windows stuff
	 */
	private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException {
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
		}

		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;
		log.info("localhost------------------------------->> = '" + localHost + "'");
		
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			//System.out.println(line);
			
			// see if line contains MAC address
			int macAddressPosition = line.indexOf(":");
			if (macAddressPosition <= 0)
				continue;

			// see if line contains IP address
			String[] lineSplit = line.split(":");
			String prefijo = "";
			if (lineSplit.length > 1) {
				prefijo = lineSplit[1].trim();
			}
			if ((line.endsWith(localHost) || prefijo.startsWith(localHost)) && lastMacAddress != null) {
				return lastMacAddress;
			}

			String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
			if (windowsIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}
		
		// haremos un intento nuevamente, en caso de que la maquina este desconectada
		tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		lastMacAddress = null;
		boolean hacer = false;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			
			// see if line contains MAC address
			int macAddressPosition = line.indexOf(":");
			if (macAddressPosition <= 0)
				continue;

			String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
			if (windowsIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				hacer = true;
				break;
			}
		}
		
		if (hacer && lastMacAddress != null) {
			return lastMacAddress;
		}
		
		ParseException ex = new ParseException("cannot read MAC address from [" + ipConfigResponse + "]", 0);
		ex.printStackTrace();
		throw ex;
	}

	/**
	 * 
	 * @param ipConfigResponse
	 * @return
	 */
	private static String getLinuxHostAddress(String ipConfigResponse) {
		System.out.println(ipConfigResponse);
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastIpAddress = null;
		String sep=":";

		int cont = 0;
		while (tokenizer.hasMoreTokens()) {
			System.out.println(cont++);
			String line = tokenizer.nextToken().trim();

			int ipAddressPosition = -1;

			//System.out.println(line);
			// see if line contains MAC address
			if (line.indexOf("inet addr") != -1) {
				ipAddressPosition = line.indexOf("inet addr");
			} else if (line.indexOf("inet direcci") != -1) {
				ipAddressPosition = line.indexOf("inet direcci");
			} else if (line.indexOf("Direc. inet") != -1) {
				ipAddressPosition = line.indexOf("Direc. inet");
			} else if (line.indexOf("inet 192") != -1) {
				ipAddressPosition = line.indexOf("inet 192");
				sep=" ";
			} else if (line.indexOf("inet ") != -1) {
				ipAddressPosition = line.indexOf("inet ");
				sep=" ";
			}
			if (ipAddressPosition < 0)
				continue;

			String[] arr = line.split(sep);

			String ipAddressCandidate = arr[1].trim();
			if (ipAddressCandidate.indexOf(" ") != -1) {
				ipAddressCandidate = ipAddressCandidate.split(" ")[0];
			}
			if(!ipAddressCandidate.equals("127.0.0.1")) {
				if (linuxIsIPAddress(ipAddressCandidate)) {
					lastIpAddress = ipAddressCandidate;
					break;
				}
			}
			
			
		}
		
		log.info("lastIpAddress: " + lastIpAddress);
		return lastIpAddress;
	}

	private final static boolean windowsIsMacAddress(String macAddressCandidate) {
		// TODO: use a smart regular expression
		boolean result = true;
		
		if (macAddressCandidate.length() != 17){
			result = false;
		}
		
		return result;
	}

	private final static String windowsRunIpConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ipconfig /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();

		stdoutStream.close();

		return outputText;
		//return testIpConfigWindow();
	}

	public static String configLinuxTest() {

		StringBuffer sb = new StringBuffer();

		sb.append("		eth0      Link encap:Ethernet  direcci�nHW 00:14:4f:80:d8:9a\n");
		sb.append("        inet direcci�n:192.168.10.42  Difusi�n:192.168.10.255  M�scara:255.255.255.0\n");
		sb.append("        direcci�n inet6: fe80::214:4fff:fe80:d89a/64 Alcance:V�nculo\n");
		sb.append("        ARRIBA DIFUSI�N CORRIENDO MULTICAST  MTU:1500  M�trica:1\n");
		sb.append("        RX packets:30925 errors:0 dropped:0 overruns:0 frame:0\n");
		sb.append("        TX packets:22275 errors:0 dropped:0 overruns:0 carrier:0\n");
		sb.append("        colisiones:0 txqueuelen:1000\n");
		sb.append("        RX bytes:16868245 (16.8 MB)  TX bytes:12857969 (12.8 MB)\n");
		sb.append("        Interrupci�n:248 Direcci�n base: 0x6000\n");
		sb.append("        \n");
		sb.append("eth1      Link encap:Ethernet  direcci�nHW 00:14:4f:80:d8:9b\n");
		sb.append("        ARRIBA DIFUSI�N MULTICAST  MTU:1500  M�trica:1\n");
		sb.append("        RX packets:0 errors:0 dropped:0 overruns:0 frame:0\n");
		sb.append("        TX packets:0 errors:0 dropped:0 overruns:0 carrier:0\n");
		sb.append("        colisiones:0 txqueuelen:1000\n");
		sb.append("        RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)\n");
		sb.append("        Interrupci�n:249 Direcci�n base: 0xc000\n");
		sb.append("        \n");
		sb.append("lo        Link encap:Bucle local\n");
		sb.append("        inet direcci�n:127.0.0.1  M�scara:255.0.0.0\n");
		sb.append("        direcci�n inet6: ::1/128 Alcance:Anfitri�n\n");
		sb.append("        ARRIBA LOOPBACK CORRIENDO  MTU:16436  M�trica:1\n");
		sb.append("        RX packets:2406 errors:0 dropped:0 overruns:0 frame:\n");
		sb.append("        TX packets:2406 errors:0 dropped:0 overruns:0 carrier:0\n");
		sb.append("        colisiones:0 txqueuelen:0\n");
		sb.append("        RX bytes:1811809 (1.8 MB)  TX bytes:1811809 (1.8 MB)\n");

		return sb.toString();
	}

	public static String configLinuxTest2() {

		StringBuffer sb = new StringBuffer();

		sb.append("eth0      Link encap:Ethernet  HWaddr 6E:04:C4:BB:99:DC\n");
		sb.append("          inet addr:200.6.158.204  Bcast:200.6.157.255  Mask:255.255.255.0\n");
		sb.append("          inet6 addr: fe80::6c04:c4ff:febb:99dc/64 Scope:Link\n");
		sb.append("          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n");
		sb.append("          RX packets:525964681 errors:0 dropped:720434 overruns:0 frame:0\n");
		sb.append("          TX packets:122084305 errors:0 dropped:0 overruns:0 carrier:0\n");
		sb.append("          collisions:0 txqueuelen:1000\n");
		sb.append("          RX bytes:32954175565 (30.6 GiB)  TX bytes:110675392830 (103.0 GiB)\n");
		sb.append("          Interrupt:193 Base address:0x6100\n");
		sb.append("\n");
		sb.append("eth0:1    Link encap:Ethernet  HWaddr 6E:04:C4:BB:99:DC\n");
		sb.append("          inet addr:200.6.158.205  Bcast:200.6.158.255  Mask:255.255.255.0\n");
		sb.append("          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n");
		sb.append("          Interrupt:193 Base address:0x6100\n");
		sb.append("\n");
		sb.append("eth0:2    Link encap:Ethernet  HWaddr 6E:04:C4:BB:99:DC\n");
		sb.append("          inet addr:200.6.158.206  Bcast:200.6.158.255  Mask:255.255.255.0\n");
		sb.append("          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n");
		sb.append("          Interrupt:193 Base address:0x6100\n");
		sb.append("\n");
		sb.append("eth0:3    Link encap:Ethernet  HWaddr 6E:04:C4:BB:99:DC\n");
		sb.append("          inet addr:200.6.158.207  Bcast:200.6.158.255  Mask:255.255.255.0\n");
		sb.append("          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n");
		sb.append("          Interrupt:193 Base address:0x6100\n");
		sb.append("\n");
		sb.append("eth0:4    Link encap:Ethernet  HWaddr 6E:04:C4:BB:99:DC\n");
		sb.append("          inet addr:200.6.158.208  Bcast:200.6.158.255  Mask:255.255.255.0\n");
		sb.append("          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n");
		sb.append("          Interrupt:193 Base address:0x6100\n");
		sb.append("\n");

		return sb.toString();
	}
	
	public static String testIpConfigWindow() {
		StringBuffer s = new StringBuffer();
		
		s.append("Configuraci�n IP de Windows\n" );
		s.append("\n" );
		s.append("Nombre del host . . . . . . . . . : WKS-CCS-32\n" );
		s.append("Sufijo DNS principal  . . . . . . : Focus.local\n" );
		s.append("Tipo de nodo . . . . . . . . . . .: difusi�n\n" );
		s.append("Enrutamiento habilitado. . . . . .: No\n" );
		s.append("Proxy WINS habilitado. . . . .    : No\n" );
		s.append("\n" );
		s.append("Adaptador Ethernet Conexiones de red inal�mbricas          :\n" );
		s.append("\n" );
		s.append("Estado de los medios. . . .: medios desconectados\n" );
		s.append("Descripci�n. . . . . . . . . . .  : Intel(R) PRO/Wireless 2200BG Network\n" );
		s.append("Connection\n" );
		s.append("Direcci�n f�sica. . . . . . . . . : 00-16-6F-11-66-37\n" );
		s.append("\n" );
		s.append("Adaptador Ethernet Conexi�n de �rea local 2          :\n" );
		s.append("\n" ); 
		s.append("Estado de los medios. . . .: medios desconectados\n" );
		s.append("Descripci�n. . . . . . . . . . .  : Marvell Yukon 88E8036 PCI-E Fast Ethernet Controller\n" );
		s.append("Direcci�n f�sica. . . . . . . . . : 00-A0-D1-2E-0C-0E\n" );
       		
		return s.toString();
	}

	/*
	 * Main
	 */
	public final static void main(String[] args) {
		try {

			System.out.println("Network infos");

			try {
				InetAddress ip = InetAddress.getLocalHost();
				System.out.println("Current IP address : " + ip.getHostAddress());
		 
				NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		 
				byte[] mac = network.getHardwareAddress();
		 
				System.out.print("Current MAC address : is up?: " + network.isUp() + " ");
		 
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				System.out.println(sb.toString());
				
				Enumeration<NetworkInterface> cards = NetworkInterface.getNetworkInterfaces();
				while(cards.hasMoreElements()) {
					network = cards.nextElement();
					System.out.print("Current MAC address '" + network.getDisplayName()  + "': is up?: " + network.isUp() + " ");
					
					sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
					}
					System.out.println(sb.toString());
				}
			} catch (Exception e) {
		 		e.printStackTrace();
		 	}
			
			System.out.println("  Operating System: " + System.getProperty("os.name"));
			System.out.println("  IP/Localhost: " + getHostAddress());
			System.out.println("  MAC Address: " + getMacAddress());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
