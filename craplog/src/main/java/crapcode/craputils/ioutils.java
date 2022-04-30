
package crapcode.craputils;

import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ioutils {
    
    
    protected static void newDirName(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        Path path, boolean bypass 
    ) {
        // find the first available copy-name
        Path new_path = Paths.get("./");
        int choice;
        if ( bypass == false ) {
            choice = JOptionPane.showConfirmDialog(null, String.format("Conflict found while checking:\n'%s'\n\nThe conflict will be renamed with trailing '(copy)'.\nThe copy will be deleted at the end of the process if it succeeds,\nor restored in case something fails.\nChoosing 'NO' will abort the entire process.\nContinue?",path), "Name conflict", 0, 2);
        } else {
            choice = JOptionPane.OK_OPTION;
        }
        if ( choice == JOptionPane.OK_OPTION ) {
            try {
                int n = 1;
                while (true) {
                    new_path = Paths.get(String.format("%s_(copy_%s)",path,n));
                    if (Files.notExists(new_path)) {
                        break;
                    } else {
                        n++;
                    }
                }
                Files.move( path, new_path );
                undo.add( new_path );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while renaming directory:\n'%s'",path), "Error renaming directory", 0);
                proceed.replace("state", "false");
            }
        } else {
            // user decided to not rename the conflict, abort processing
            proceed.replace("state", "false");
        }
    }
    
    protected static void undoDirName( Path path ) {
        // find the first available copy-name
        String p = path.toString();
        try {
            int i = 1;
            while ( p.indexOf("_(copy_",i) != -1 ) {
                i = p.indexOf("_(copy_",i)+1;
            }
            Path new_path = Paths.get(p.substring(0,i-1));
            // shouldn't need double checking existence here
            Files.move( path, new_path );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while renaming directory:\n'%s'",path), "Error renaming directory", 0);
        }
    }
    
    protected static void newFileName(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        Path path, boolean bypass
    ) {
        // find the first available copy-name
        Path new_path = Paths.get("./");
        int choice;
        if ( bypass == false ) {
            choice = JOptionPane.showConfirmDialog(null, String.format("Conflict found while checking:\n'%s'\n\nThe conflict will be renamed with trailing '(copy)'.\nThe copy will be deleted at the end of the process if it succeeds,\nor restored in case something fails.\nChoosing 'NO' will abort the entire process.\nContinue?",path), "Name conflict", 0, 2);
        } else {
            choice = JOptionPane.OK_OPTION;
        }
        if ( choice == JOptionPane.OK_OPTION ) {
            // get the name and perhaps the extension of the file
            try {       
                String p = path.toString();
                String file_name = path.getFileName().toString();
                String new_path_base = p.substring(0,p.indexOf(file_name)-1);
                int i = 1;
                while ( p.indexOf('.',i) != -1 ) {
                    i = p.indexOf('.',i)+1;
                }
                String file_ext = "";
                if ( i != 1 ) {
                    // file has a declared extension
                    file_ext = String.format(".%s",p.substring(i));
                }
                // try renaming the file
                int n = 1;
                while (true) {
                    new_path = Paths.get(String.format("%s/%s_(copy_%s)%s",new_path_base,file_name,n,file_ext));
                    if (Files.notExists(new_path)) {
                        break;
                    } else {
                        n++;
                    }
                }
                Files.move( path, new_path );
                undo.add( new_path );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while renaming file:\n'%s'",path), "Error renaming file", 0);
                proceed.replace("state", "false");
            }
        } else {
            // the user choose to not rename the file, abort processing
            proceed.replace("state", "false");
        }
    }
    
    protected static void undoFileName( Path path ) {
        // find the first available copy-name
        String p = path.toString();
        try {
            int i = 1;
            while ( p.indexOf("_(copy_",i) != -1 ) {
                i = p.indexOf("_(copy_",i)+1;
            }
            String file_path = p.substring(0, i-1);
            String file_ext  = "";
            if (p.indexOf(").", i) != -1 ) {
                file_ext = p.substring( p.indexOf(").", i+1) );
            }
            
            Path new_path = Paths.get( String.format("%s%s",file_path,file_ext) );
            // shouldn't need double checking existence here
            Files.move( path, new_path );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while renaming file:\n'%s'",path), "Error renaming file", 0);
        }
    }
    
    
    protected static void createDir(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        Path path
    ) {
        try {
            Files.createDirectory(path);
            undo.add(path);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while creating directory:\n'%s'",path), "Error making directory", 0);
            proceed.replace("state", "false");
        }
    }
    
    
    protected static void writeFile(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        Path path,
        ArrayList<ArrayList<String>> sorted_list
    ) {
        // attempt writing to file
        if (proceed.get("state").equals("true")) {
            File f_out = new File(path.toString());
            try (
                BufferedOutputStream buff_out = new BufferedOutputStream(new FileOutputStream(f_out))
            ) {
                undo.add( path );
                ArrayList<String> counts = sorted_list.get(0);
                ArrayList<String> items  = sorted_list.get(1);
                for ( int i=0; i<items.size(); i++ ) {
                    String new_line = String.format("%s %s\n",counts.get(i),items.get(i));
                    buff_out.write( new_line.getBytes() );
                }
                buff_out.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while writing new file:\n'%s'",path), "Error writing on new file", 0);
                proceed.replace("state", "false");
            }
        }
    }
    
    
    public static void undoPaths(
        HashMap<String,String> proceed,
        ArrayList<Path> undo
    ) {
        if (proceed.get("state").equals("false")) {
            // if something failed, delete newely created files/folders
            ArrayList<Path> copies = new ArrayList<>();
            ArrayList<Path> retry  = new ArrayList<>();
            // delete new paths first
            for ( Path del_path : undo ) {
                if (del_path.toString().contains("_(copy_")) {
                    copies.add( del_path );
                }
                try {
                    Files.delete(del_path);
                } catch (IOException e) {
                    retry.add(del_path);
                }
            }
            // then raname back the copies
            if ( !copies.isEmpty() ) {
                for ( Path del_path : copies ) {
                    if (Files.isDirectory(del_path)) {
                        ioutils.undoDirName( del_path );
                    } else {
                        ioutils.undoFileName( del_path );
                    }
                }
            }
            // try again if something failed
            if ( !retry.isEmpty() ) {
                for ( Path del_path : retry ) {
                    try {
                        Files.delete(del_path);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, String.format("An error occured while removing:\n'%s'",del_path), "Error deleting file/folder", 0);
                    }
                }
            copies.clear();
            retry.clear();
            }
        
        } else {
            // remove the backup copies of replaced files
            ArrayList<Path> retry = new ArrayList<>();
            // delete new paths first
            for ( Path del_path : undo ) {
                if ( !del_path.toString().contains("_(copy_") ) {
                    continue;
                }
                try {
                    Files.delete(del_path);
                } catch (IOException e) {
                    retry.add(del_path);
                }
            }
            // try again if something failed
            if ( !retry.isEmpty() ) {
                for ( Path del_path : retry ) {
                    try {
                        Files.delete(del_path);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, String.format("An error occured while removing:\n'%s'",del_path), "Error deleting file/folder", 0);
                    }
                }
            retry.clear();
            }
        }
        undo.clear();
        proceed.replace("state", "true");
    }
}
