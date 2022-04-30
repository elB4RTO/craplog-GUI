
package crapcode.craputils;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.zip.GZIPInputStream;


public class validate {
    
    
    public static ArrayList<String> readFile( HashMap<String,String> proceed, String file_path_str ) {
        // initialize array    
        ArrayList read = new ArrayList<String>();
        // transform to path
        Path file_path = Paths.get( file_path_str );
        // check existence
        if (Files.notExists(file_path)) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while searching for file:\n'%s'",file_path), "File not found", 0);
            proceed.replace("state", "false");
        
        } else {
            
            // try reading as an archive
            try {
                read = validate.tryArchive( file_path );

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while searching for archive:\n'%s'",file_path), "File not found", 0);
                proceed.replace("state", "false");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while reading archive:\n'%s'",file_path), "Error reading", 0);
                proceed.replace("state", "false");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error handling archive", 0);
                proceed.replace("state", "false");
            }
            
            // if archive failed, try as normal file
            if (read.size() < 1
            &&  proceed.get("state").equals("true") ) {
                // try reading as normal file
                try {
                    read = validate.tryFile( file_path );

                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, String.format("An error occured while searching for file:\n'%s'",file_path), "File not found", 0);
                    proceed.replace("state", "false");

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, String.format("An error occured while reading file:\n'%s'",file_path), "Error reading", 0);
                    proceed.replace("state", "false");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error handling file", 0);
                    proceed.replace("state", "false");
                }
            }
        }
        return read;
    }
    
    
    private static ArrayList<String> tryArchive( Path file_path )
    throws FileNotFoundException, IOException, Exception {
        // treat as it is an archive
        ArrayList<String> read = new ArrayList<>();
        // attempt decompression
        try (
            InputStream f_in = Files.newInputStream( file_path );
            BufferedInputStream buff_in = new BufferedInputStream( f_in );
            GZIPInputStream gz_in = new GZIPInputStream( buff_in );
        ) {
            // successfully decompressed, try to read
            byte[] BUFFER = new byte[1024];
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            int buff_read;
            while ((buff_read = gz_in.read(BUFFER)) > 0) {
                data.write( BUFFER, 0, buff_read );
            }
            read.add( data.toString() );
            // close closable opened
            gz_in.close();
            buff_in.close();
            f_in.close();
        
        } catch (Exception e) {
            // not a gzip archive
        
        } finally {
            // close
            return read;
        }
    }
    
    
    private static ArrayList<String> tryFile( Path file_path )
    throws FileNotFoundException, IOException, Exception {
        // treat as a normal log file
        ArrayList read = new ArrayList<String>();
        // attempt reading
        try (
            InputStream f_in = Files.newInputStream( file_path );
            BufferedInputStream buff_in = new BufferedInputStream( f_in );
        ) {
            // get data
            read.add( new String( buff_in.readAllBytes() ));
            // close
            buff_in.close();
            f_in.close();
        }
        return read;
    }

    
}
