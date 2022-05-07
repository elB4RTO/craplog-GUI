
package crapcode.craputils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import java.util.List;
import javax.swing.JOptionPane;

public class delete {
    
    private static void showPermissionsErrorDialog() {
        // explaination of the error
        JOptionPane.showMessageDialog(null,
            String.format("The error was most likely caused by missing permissions.\nThe process cannot be un-done and will not be aborted now.\nPlease consider to MANUALLY REMOVE THE FILE\nand to change files/folder permissions (USE CAUTION)\nif you're planning to use this function again"),
            "Error removing original logs file", 0);
    }

    
    public static void toTrash( String logs_dir, List<String> target_files, String trash_dir ) {
        Path file_path, trash_path;
        for ( String file : target_files ) {
            file_path = Paths.get(String.format("%s/%s",logs_dir,file));
            trash_path = Paths.get(String.format("%s/%s",trash_dir,file));
            try {
                Files.move( file_path, trash_path );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    String.format("An error occured while moving original logs file to trash:\n'%s'",file_path),
                    "Error moving file to trash", 0);
                delete.showPermissionsErrorDialog();
            }
        }
    }
    
    
    public static void Remove( String logs_dir, List<String> target_files ) {
        Path file_path;
        for ( String file : target_files ) {
            file_path = Paths.get(String.format("%s/%s",logs_dir,file));
            try {
                Files.delete( file_path );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    String.format("An error occured while deleting original logs file:\n'%s'",file_path),
                    "Error deleting file", 0);
                delete.showPermissionsErrorDialog();
            }
        }
    }
}
