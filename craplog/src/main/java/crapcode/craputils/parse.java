
package crapcode.craputils;

import java.util.ArrayList;
import java.util.HashMap;

public class parse {
    
    public static void parseData(
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> collection,
        ArrayList<String> data_array,
        ArrayList<String> skip_ip,
        boolean parseACClogs, boolean parseIP, boolean parseREQ, boolean parseRES, boolean parseUA,
        boolean parseERRlogs, boolean parseERR, boolean parseLEV
    ) {
        // collect every access/error log-line in a separate array
        ArrayList acc = new ArrayList<String>();
        ArrayList err = new ArrayList<String>();
        // for every file data
        for ( String file_data : data_array ) {
            // split in single lines
            String[] lines = file_data.split("\n");
            // for every line
            for ( String line : lines ) {

                if ( line.isBlank() || line.isEmpty() ) {
                    // skip this line
                    continue;

                } else if ( line.startsWith("[") && !line.endsWith("\"") ) {
                    // error.log line
                    boolean add_line;
                    if (parseERRlogs == true) {
                        add_line = true;
                        if ( line.contains("[client ") ) {
                            // check if the line should be used or discarded
                            for (String ip : skip_ip) {
                                String IP_check = String.format("[client %s",ip);
                                if (line.contains(IP_check)) {
                                    // skip this line
                                    add_line = false;
                                    break;
                                }
                            }
                        }
                        if (add_line == true) {
                            // append the current line
                            err.add(line);
                        }
                    }

                } else if ( line.endsWith("\"") ) {
                    // access.log line
                    if (parseACClogs == true) {
                        boolean add_line = true;
                        // check if the line should be used or not
                        for (String ip : skip_ip) {
                            if (line.startsWith(ip)) {
                                // skip this line
                                add_line = false;
                                break;
                            }
                        }
                        if (add_line == true) {
                            // append the current line
                            acc.add(line);
                        }
                    }
                }
            }
        }
        
        if ( !acc.isEmpty() ) {
            parse.parseAccess( collection, acc, parseIP, parseREQ, parseRES, parseUA );
        }
        
        if ( !err.isEmpty() ) {
            parse.parseError( collection, err, parseERR, parseLEV );
        }
        
    }
    
    
    private static String processDate( String date_ ) {
        String[] date = date_.split("/");
        String day, month, year;
        day = date[0];
        switch (date[1]) {
            case "Jan":
                month = "01";
                break;
            case "Feb":
                month = "02";
                break;
            case "Mar":
                month = "03";
                break;
            case "Apr":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "Jun":
                month = "06";
                break;
            case "Jul":
                month = "07";
                break;
            case "Aug":
                month = "08";
                break;
            case "Sep":
                month = "09";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;
            default:
                month = "00";
                System.out.println("Error parsing month: '"+date[1]+"'");
                System.exit(1);
        }
        year = date[2];
        
        return String.format("%s-%s-%s",
            year, month, day);
    }
    

    private static void parseAccess(
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> collection,
        ArrayList<String> fileContent,
        boolean parseIP, boolean parseREQ, boolean parseRES, boolean parseUA
    ) {
        
        ArrayList<String> DATE = new ArrayList<>();
        ArrayList<String> IP   = new ArrayList<>();
        ArrayList<String> REQ  = new ArrayList<>();
        ArrayList<String> RES  = new ArrayList<>();
        ArrayList<String> UA   = new ArrayList<>();
        
        
        for (String line : fileContent) {
            String ip, date, req, res, ua;
            line = line.trim();
            String[] line_split = line.split("\"");
            String[] ip_date = line_split[0].split(" ");
            // retrieve single fields
            ip   = ip_date[0].trim();
            date = ip_date[3].trim().substring(1).split(":")[0];
            date = parse.processDate(date);
            req = line_split[1].trim();
            res = line_split[2].trim();
            res = res.substring(0, 3).trim();
            ua  = line_split[5].trim();
            // append to lists
            DATE.add(date);
            IP.add(ip);
            REQ.add(req);
            RES.add(res);
            UA.add(ua);
        }
        
        HashMap<String, HashMap<String, HashMap<String, Integer>>> A = collection.get("access");
        for (int i=0; i < DATE.size(); i++ ) {
            
            String date = DATE.get(i);
            
            if (!A.containsKey(date)) {
                HashMap<String, HashMap<String, Integer>> aux = new HashMap<>();
                aux.put("IP",  new HashMap<>());
                aux.put("REQ", new HashMap<>());
                aux.put("RES", new HashMap<>());
                aux.put("UA",  new HashMap<>());
                A.put(date, aux);
            }
            
            HashMap<String, Integer> IPm  = A.get(date).get("IP");
            HashMap<String, Integer> REQm = A.get(date).get("REQ");
            HashMap<String, Integer> RESm = A.get(date).get("RES");
            HashMap<String, Integer> UAm  = A.get(date).get("UA");
            
            if (parseIP == true) {
                String ip = IP.get(i);
                if (IPm.containsKey(ip)) {
                    int c = IPm.get(ip) +1;
                    IPm.replace(ip, c);
                } else {
                    IPm.put(ip, 1);
                }
            }
            if (parseREQ == true) {
                String req = REQ.get(i);
                if (REQm.containsKey(req)) {
                    int c = REQm.get(req) +1;
                    REQm.replace(req, c);
                } else {
                    REQm.put(req, 1);
                }
            }
            if (parseRES == true) {
                String res = RES.get(i);
                if (RESm.containsKey(res)) {
                    int c = RESm.get(res) +1;
                    RESm.replace(res, c);
                } else {
                    RESm.put(res, 1);
                }
            }
            if (parseUA == true) {
                String ua = UA.get(i);
                if (UAm.containsKey(ua)) {
                    int c = UAm.get(ua) +1;
                    UAm.replace(ua, c);
                } else {
                    UAm.put(ua, 1);
                }
            }
        }
        DATE.clear();
        IP.clear();
        REQ.clear();
        RES.clear();
        UA.clear();
    }

    
    private static void parseError(
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> collection,
        ArrayList<String> fileContent,
        boolean parseERR, boolean parseLEV
    ) {
        
        ArrayList<String> DATE = new ArrayList<>();
        ArrayList<String> ERR  = new ArrayList<>();
        ArrayList<String> LEV  = new ArrayList<>();
        
        
        for (String line : fileContent) {
            String date, err, lev;
            line = line.trim();
            String[] line_split = line.split("]");
            String[] date_time = line_split[0].substring(1).split(" ");
            // retrieve single fields
            date = String.format("%s/%s/%s",date_time[2],date_time[1],date_time[4]);
            date = parse.processDate(date);
            lev = line_split[1].trim().substring(1);
            if (line_split[3].startsWith(" [client")) {
                err = line_split[4].substring(2).trim();
            } else {
                err = line_split[3].trim();
            }
            // append to lists
            DATE.add(date);
            ERR.add(err);
            LEV.add(lev);
        }
        
        HashMap<String, HashMap<String, HashMap<String, Integer>>> E = collection.get("error");
        for (int i=0; i < DATE.size(); i++ ) {
            
            String date = DATE.get(i);
            
            if (!E.containsKey(date)) {
                HashMap<String, HashMap<String, Integer>> aux = new HashMap<>();
                aux.put("ERR",  new HashMap<>());
                aux.put("LEV", new HashMap<>());
                E.put(date, aux);
            }
            
            HashMap<String, Integer> ERRm = E.get(date).get("ERR");
            HashMap<String, Integer> LEVm = E.get(date).get("LEV");
            
            if (parseERR == true) {
                String err = ERR.get(i);
                if (ERRm.containsKey(err)) {
                    int c = ERRm.get(err) +1;
                    ERRm.replace(err, c);
                } else {
                    ERRm.put(err, 1);
                }
            }
            if (parseLEV == true) {
                String lev = LEV.get(i);
                if (LEVm.containsKey(lev)) {
                    int c = LEVm.get(lev) +1;
                    LEVm.replace(lev, c);
                } else {
                    LEVm.put(lev, 1);
                }
            }
        }
        DATE.clear();
        ERR.clear();
        LEV.clear();
    }
}
