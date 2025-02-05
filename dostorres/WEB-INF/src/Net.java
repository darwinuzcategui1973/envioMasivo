import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class Net {

	public static void main(String[] args) throws Exception {

		System.out.println(getMacLinux());
		System.out.println(getIpFromMachine());
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

    public static String getMacLinux() {
		try {

			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface network = networkInterfaces.nextElement();
				//System.out.println("network : " + network);
				byte[] mac = network.getHardwareAddress();
				if (mac == null) {
					System.out.println("null mac");
				} else {
					//System.out.print("MAC address : ");

					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
					//System.out.println(sb.toString());
					return sb.toString();
				}
			}
		} catch (SocketException e) {

			e.printStackTrace();

		}
		return null;
	}
	
    
}
