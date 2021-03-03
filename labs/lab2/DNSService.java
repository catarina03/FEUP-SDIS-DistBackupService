import java.util.HashMap;

public class DNSService {
    private static HashMap<String, String> dnsTable = new HashMap<String, String>();

    public static String addEntry(Request r) { // REGISTER

        if (dnsTable.containsKey(r.name)) {
            return "ERROR: Already registered";
        }

        dnsTable.put(r.name.trim(), r.ip_address.trim());
        return "Server: Success. \n";

    }

    public static String getEntry(Request r) { // LOOKUP
        if (dnsTable.containsKey(r.name.trim())) {
            return "Server: " + dnsTable.get(r.name.trim()) + "\n";
        } else {
            return "ERROR: DNS name not found. \n";
        }

    }
}
