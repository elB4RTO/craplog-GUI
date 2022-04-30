
package crapcode.craputils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class statutils {
    
    
    protected static ArrayList<ArrayList<String>> sortList(
        HashMap<String, Integer> list
    ) {
        ArrayList<String>  items  = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();
        ArrayList<ArrayList<String>> ordered = new ArrayList<>();
        for ( Entry<String,Integer> set : list.entrySet() ) {
            items.add(set.getKey());
            counts.add(set.getValue());
        }
        // reverse bubble sort
        for ( int i=0; i<items.size(); i++ ) {
            for ( int j=0; j<items.size(); j++ ) {
                // start sorting
                if ( counts.get(i) > counts.get(j) ) {
                    int c1 = counts.get(i);
                    int c2 = counts.get(j);
                    counts.set(i, c2);
                    counts.set(j, c1);
                    String i1 = items.get(i);
                    String i2 = items.get(j);
                    items.set(i, i2);
                    items.set(j, i1);
                }
            }
        }
        // turn integers to srings
        ArrayList<String> counts_str = new ArrayList<>();
        for ( Integer count : counts ) {
            counts_str.add( count.toString() );
        }
        // put in a single list
        ordered.add(counts_str);
        ordered.add(items);
        return ordered;
    }
    
    
    protected static void updateList(
        HashMap<String,String> proceed,
        Path path, HashMap<String,Integer> list
    ) {
        String[] data = {""};
        try (
            InputStream f_in = Files.newInputStream( path );
            BufferedInputStream buff_in = new BufferedInputStream( f_in )
        ) {
            // get data
            data = new String( buff_in.readAllBytes() ).split("\n");
            // close
            buff_in.close();
            f_in.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while reading old stat file:\n'%s'",path), "Error reading file", 0);
            proceed.replace("state", "false");
        }
        if (proceed.get("state").equals("true")) {
            for ( String line : data ) {
                line = line.trim();
                if (line.isBlank()
                ||  line.startsWith("#")) {
                    continue;
                }
                if ( line.contains(" ") ) {
                    int count = 0;
                    String item = new String();
                    int i = line.indexOf(' ');
                    // try reading count
                    try {
                        count = Integer.parseInt( line.substring(0, i) );
                    } catch (NumberFormatException e) {
                        if ( JOptionPane.showConfirmDialog(null, String.format("Error retrieving count, the value cannot be converted to integer:\n'%s'\nThe complete line is:\n'%s'\nIf you didn't edit this file, please report this error.\nIgnore and continue?",count,line), "Not a number", 0, 2) == JOptionPane.NO_OPTION ) {
                            proceed.replace("state", "false");
                            break;
                        }
                    }
                    // try reading item
                    try {
                        item  = line.substring(i+1);
                        if (item.isBlank()) {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        if ( JOptionPane.showConfirmDialog(null, String.format("Error retrieving item, nothing to use as string:\n'%s'\nThe complete line is:\n'%s'\nIf you didn't edit this file, please report this error.\nIgnore and continue?",item,line), "No string found", 0, 2) == JOptionPane.NO_OPTION ) {
                            proceed.replace("state", "false");
                            break;
                        }
                    }
                    // update the list
                    if (list.containsKey(item)) {
                        // sum counts of already existing items
                        count += list.get(item);
                        list.replace(item, count);
                    } else {
                        // create a new entry for the missing item-count values
                        list.put(item, count);
                    }
                } else {
                    if ( JOptionPane.showConfirmDialog(null, String.format("Invalid line, cannot split in 'count-item':\n'%s'\nIf you didn't edit this file, please report this error.\nIgnore and continue?",line), "No separator found", 0, 2) == JOptionPane.NO_OPTION ) {
                        proceed.replace("state", "false");
                        break;
                    }
                }
            }
        }
    }
    
    
    protected static void updateCollection(
        HashMap<String, HashMap<String, HashMap<String, Integer>>> new_collection,
        HashMap<String,Integer> list,
        String list_name, String log_type
    ) {
        HashMap<String,Integer> up_list = new_collection.get(log_type).get(list_name);
        for ( Entry<String,Integer> l : list.entrySet() ) {
            String i = l.getKey();
            int c = l.getValue();
            if (up_list.containsKey(i)) {
                c += up_list.get(i);
                up_list.replace(i, c);
            } else {
                up_list.put(i, c);
            }
        }
        
    }
    
}
