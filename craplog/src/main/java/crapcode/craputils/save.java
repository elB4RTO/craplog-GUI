
package crapcode.craputils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class save {

    
    public static void storeSession(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        boolean storeACC, boolean storeERR,
        String stats_dir,
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> collection
    ) {
        
        Path path = Paths.get( stats_dir );
        // check the existence of the 'crapstats' dir
        if ( !Files.exists(path) ) {
            ioutils.createDir(proceed, undo, path);
        }
        
        if (proceed.get("state").equals("true")) {
            path = Paths.get( String.format("%s/sessions",stats_dir) );
            // check the existence of the 'crapstats/sessions' dir
            if ( !Files.exists(path) ) {
                ioutils.createDir(proceed, undo, path);
            }
        }
        
        ArrayList<Path> already_created = new ArrayList<>();
        // start saving session stats
        for ( String log_type : collection.keySet() ) {
            if (proceed.get("state").equals("false")) {
                break;
            }
            // check the existence of log-types dir
            path = Paths.get( String.format("%s/sessions/%s",stats_dir,log_type) );
            if ( !Files.exists(path) ) {
                ioutils.createDir(proceed, undo, path);
            }
            if (proceed.get("state").equals("false")) {
                break;
            }
            if ((log_type.equals("access") && storeACC == true )
            ||  (log_type.equals("error")  && storeERR == true )) {
                for ( String date : collection.get(log_type).keySet() ) {
                    path = Paths.get( String.format("%s/sessions/%s/%s",stats_dir, log_type, date) );
                    // check the existence of a folder for every date
                    if ( !already_created.contains(path) ) {
                        already_created.add(path);
                        if ( !Files.exists(path) ) {
                            ioutils.createDir(proceed, undo, path);
                        } else {
                            if (JOptionPane.showConfirmDialog(null, String.format("WARNING!\nA directory with this same date already exists\n'%s'\n\nPlease make sure you're not parsing the same log file twice.\nContinue?",path), "Name conflict", 0, 3) == JOptionPane.NO_OPTION ) {
                                proceed.replace("state", "false");
                            }
                        }
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                    }

                    // check the existence of a stat file for this date
                    for (Entry<String, HashMap<String,Integer>> map : collection.get(log_type).get(date).entrySet()) {
                        if (map.getValue().isEmpty()) {
                            continue;
                        }
                        String list_name = map.getKey();
                        HashMap<String,Integer> list = map.getValue();
                        // check the existence of a stat file for this date
                        path = Paths.get( String.format("%s/sessions/%s/%s/%s.crapstat",stats_dir,log_type,date,list_name) );
                        if (Files.exists(path)) {
                            if ( !Files.isDirectory(path) ) {
                                // retrieve previous stats
                                statutils.updateList( proceed, path, list );
                                if (proceed.get("state").equals("false")) {
                                    break;
                                }
                                // backup the (old) file as a copy
                                ioutils.newFileName(proceed, undo, path, true);
                                if (proceed.get("state").equals("false")) {
                                    break;
                                }
                            } else {
                                ioutils.newDirName(proceed, undo, path, false);
                                if (proceed.get("state").equals("false")) {
                                    break;
                                }
                            }
                        }
                        // write the new file
                        ioutils.writeFile( proceed, undo, path, statutils.sortList(list) );
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    
    
    
    public static void updateGlobals(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        boolean storeACC, boolean storeERR,
        String stats_dir,
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> collection
    ) {
       
        Path path = Paths.get( stats_dir );
        // check the existence of the 'crapstats' dir
        if ( !Files.exists(path) ) {
            ioutils.createDir(proceed, undo, path);
        }
        
        if (proceed.get("state").equals("true")) {
            path = Paths.get( String.format("%s/globals",stats_dir) );
            // check the existence of the 'crapstats/globals' dir
            if ( !Files.exists(path) ) {
                ioutils.createDir(proceed, undo, path);
            }
        }
        
        
        // make a brand new collection to collect final data
        HashMap<String, HashMap<String, HashMap<String, Integer>>> new_collection = new HashMap<>();
        new_collection.put("access", new HashMap<>());
        new_collection.put("error",  new HashMap<>());
        HashMap<String, HashMap<String, Integer>> M;
        M = new_collection.get("access");
        M.put("IP",  new HashMap<>());
        M.put("REQ", new HashMap<>());
        M.put("RES", new HashMap<>());
        M.put("UA",  new HashMap<>());
        M = new_collection.get("error");
        M.put("ERR",  new HashMap<>());
        M.put("LEV", new HashMap<>());
        M = new HashMap();
        // start updating global stats
        for ( String log_type : collection.keySet() ) {
            if (proceed.get("state").equals("false")) {
                break;
            }
            // check the existence of log-types dir
            path = Paths.get( String.format("%s/globals/%s",stats_dir,log_type) );
            if ( !Files.exists(path) ) {
                ioutils.createDir(proceed, undo, path);
            }
            if (proceed.get("state").equals("false")) {
                break;
            }
            if ((log_type.equals("access") && storeACC == true )
            ||  (log_type.equals("error")  && storeERR == true )) {
                for ( String date : collection.get(log_type).keySet() ) {
                    // retrieve items-counts map
                    for (Entry<String, HashMap<String,Integer>> map : collection.get(log_type).get(date).entrySet()) {
                        if (map.getValue().isEmpty()) {
                            continue;
                        }
                        String list_name = map.getKey();
                        HashMap<String,Integer> list = map.getValue();
                        statutils.updateCollection( new_collection, list, list_name, log_type );
                    }
                }
            }
            // store updated stats
            for ( Entry<String,HashMap<String,Integer>> map : new_collection.get(log_type).entrySet() ) {
                String list_name = map.getKey();
                HashMap<String,Integer> list = map.getValue();
                // check the existence of a stat file
                path = Paths.get( String.format("%s/globals/%s/%s.crapstat",stats_dir,log_type,list_name) );
                if (Files.exists(path)) {
                    if ( !Files.isDirectory(path) ) {
                        // retrieve previous stats
                        statutils.updateList( proceed, path, list );
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                        // backup the (old) file as a copy
                        ioutils.newFileName(proceed, undo, path, true);
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                    } else {
                        ioutils.newDirName(proceed, undo, path, false);
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                    }
                }
                // write the new file
                ioutils.writeFile( proceed, undo, path, statutils.sortList(list) );
                if (proceed.get("state").equals("false")) {
                    break;
                }
            }
        }
    }
    
    
}
