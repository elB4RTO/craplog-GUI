
package crapcode;

import javax.swing.JOptionPane;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;



public class crapup {

    public static int CheckUpdates() {
        // 0: no, 1: yes, -1: an error occured
        int update_available = 0;
      
        String version_mark   = ".:!¦version¦!:.";
        String actual_version = "1.0";
        String version_check_link = "https://github.com/elB4RTO/craplog-javaGUI/blob/main/version_check";
        String repository_link    = "https://github.com/elB4RTO/craplog-javaGUI";
        String issues_link        = "https://github.com/elB4RTO/craplog-javaGUI/issues";
        
        try {
            // request the page
            URL version_page = new URL( version_check_link );
            URLConnection page = version_page.openConnection();
            BufferedReader buff_in = new BufferedReader( new InputStreamReader( page.getInputStream() ));
            // search the version string inside the page
            boolean found = false;
            String line = new String();
            try {
                while ((line = buff_in.readLine()) != null) {
                    if ( line.contains("pathlib") ) {
                    //if ( line.contains( version_mark ) ) {
                        found = true;
                        break;
                    }
                }
            } catch (IOException e) {
                throw new Exception(String.format("An error occured while reading content from:\n%s\nPlease report this issue here:\n%s",version_check_link,issues_link));
            }
            buff_in.close();
            // exit if not successful
            if ( found == false ) {
                throw new Exception(String.format("Can't find the version control line inside the page.\nThe version actually in use is: %s\nPlease visit this page to manually check for new a version:\n%s\n%s",actual_version,version_check_link,repository_link));
            }
            // compare versions
            String new_version = line.substring(
                line.indexOf( version_mark ) + version_mark.length(),
                line.indexOf( version_mark, line.indexOf( version_mark ) + version_mark.length() )).trim();
            if ( !actual_version.equals(new_version) ) {
                float actual = 0;
                float check  = 0;
                try {
                    actual = Float.parseFloat( actual_version );
                    check  = Float.parseFloat( new_version );
                } catch (NumberFormatException e) {
                    throw new Exception(String.format("An error occured while parsing versions.\nThe version in use (yours) is: %s\nThe version check (repository) returned: %s\nPlease visit this page to manually check for new a version:\n%s\n%s\nPlease report this issue here:\n%s",actual_version,new_version,version_check_link,repository_link,issues_link));
                }
                if ( actual < check ) {
                    update_available = 1;
                } else if ( actual > check ) {
                    throw new Exception(String.format("It seems that you're running a version from the future...\nThe version in use (yours) is: %s\nBut the version check (repository) returned: %s\nPlease visit this page to manually check for new a version:\n%s\n%s",actual_version,new_version,version_check_link,repository_link));
                }
            }
        
        } catch (MalformedURLException e) {
            update_available = -1;
            JOptionPane.showMessageDialog(null, String.format("Malformed version-check URL:\n%s\nPlease report this issue here:\n%s",version_check_link,issues_link), "Malformed URL", 0);
            
        } catch (IOException e) {
            update_available = -1;
            JOptionPane.showMessageDialog(null, String.format("An error occured while connecting to:\n%s\n\nPlease check your connection or try again later.\nIf you can manually reach the page, please report this issue here:\n%s",version_check_link,issues_link), "Connection failed", 0);
            
        } catch (Exception e) {
            update_available = -1;
            JOptionPane.showMessageDialog(null, e.getMessage(), "Something went wrong", 0);
            
        }
        
        return update_available;
    }
}
