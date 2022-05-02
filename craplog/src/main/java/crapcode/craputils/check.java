
package crapcode.craputils;

import java.util.List;
import java.util.HashMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class check {

    public static void initialChecks(
        HashMap<String,String> proceed,
        List<String> target_files,
        String logs_dir, String stats_dir
    ) {
        // check the readability of logs files
        Path path;
        if ( Files.exists( Paths.get(logs_dir)) ) {
            for ( String log_file : target_files ) {
                path = Paths.get(String.format("%s/%s",logs_dir,log_file));
                if (Files.exists(path)) {
                    if ( !Files.isReadable(path) ) {
                        JOptionPane.showMessageDialog(null, String.format("An error occured while checking:\n'%s'\n\nThis file is not readable, please check the permissions and retry",path), "Un-readable log file", 0);
                        proceed.replace("state", "false");
                        break;
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, String.format("An error occured while checking:\n'%s'\n\nThis folder should contain files, but it does not exists.\nPlease open the settings menu and set-up a valid one.",Paths.get(logs_dir)), "Un-existent stats directory", 0);
            proceed.replace("state", "false");
        }
        
        if (proceed.get("state").equals("true")) {
            path = Paths.get(stats_dir);
            if (Files.exists(path)) {
                if ( !Files.isWritable(path) ) {
                    JOptionPane.showMessageDialog(null, String.format("An error occured while checking:\n'%s'\n\nThis folder is not writable, please check the permissions and retry",path), "Un-writable stats directory", 0);
                    proceed.replace("state", "false");
                }
            } else {
                JOptionPane.showMessageDialog(null, String.format("An error occured while checking:\n'%s'\n\nThis folder is responsible for holding statistics files,\nbut it does not exists.\nPlease open the settings menu and set-up a valid one",path), "Un-existent stats directory", 0);
                proceed.replace("state", "false");
            }
        }
    }
    
    
    public static void checkTrash(
        HashMap<String,String> proceed,
        String trash_dir
    ) {
        Path path = Paths.get(trash_dir);
        if (Files.exists(path)) {
            if ( !Files.isWritable(path) ) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while checking:\n'%s'\n\nThis folder is not writable, please check the permissions and retry",path), "Un-writable trash directory", 0);
                proceed.replace("state", "false");
            }
        } else {
            JOptionPane.showMessageDialog(null, String.format("An error occured while checking:\n'%s'\n\nThe folder doen't exists.\nPlease insert the correct path of your trash in the settings and retry",path), "Missing trash", 0);
            proceed.replace("state", "false");
        }
    }
    
}
