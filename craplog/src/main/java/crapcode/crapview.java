
package crapcode;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class crapview {
    
    
    private static void sortList( DefaultListModel<String> logs_list ) {
        // re-arrange in a sorted list
        String n1, n2;
        int m, r;
        for ( int i=0; i<logs_list.size(); i++ ) {
            // get the first file name
            n1 = logs_list.get(i);
            for ( int j=0; j<logs_list.size(); j++ ) {
                // get the second file name
                n2 = logs_list.get(j);
                // get the shorter name's length
                m = n1.length();
                if ( m > n2.length() ) {
                    m = n2.length();
                }
                // compare each character
                r = 0;
                for ( int k=0; k<m; k++ ) {
                    if ( n1.charAt(k) < n2.charAt(k) ) {
                        // second is smaller
                        r = 1;
                        break;
                    } else if ( n1.charAt(k) > n2.charAt(k) ) {
                        // firse is smaller
                        r = -1;
                        break;
                    }
                }
                // if no difference was found, the shorter comes first
                if ( r == 0 ) {
                    if ( n1.length() > n2.length() ) {
                        r = 1;
                    }
                }
                // switch positions if needed
                if ( r == 1 ) {
                    logs_list.set(i, n2);
                    logs_list.set(j, n1);
                    n1 = n2;
                }
            }
        }
    }
    
    
    public static String viewLogFile(
        String logs_dir,
        String file_name
    ) {
        // read and return the content
        String read = new String();
        Path path = Paths.get( String.format("%s/%s",logs_dir,file_name) ).toAbsolutePath();
        // attempt reading
        HashMap<String,String> proceed = new HashMap<>();
        proceed.put("state", "true");
        ArrayList<String> lines = crapcode.craputils.validate.readFile( proceed , path.toString());
        for ( String line : lines ) {
            read += String.format("%s",line.replaceAll( "\n", "\n\n" ));
        }
        proceed.clear();
        lines.clear();
        return read;
    }
    
    
    public static void makeViewLogsList(
        String logs_dir_,
        DefaultListModel logs_list,
        JTable table_list
    ) {
        // get the available log files
        logs_list = crapview.getLogsList( logs_dir_ );
        // make the table
        DefaultTableModel m = new DefaultTableModel();
        m.addColumn( logs_dir_ );
        if ( logs_list.size() == 0 ) {
            // no data to show
            m.addRow( new Object[] {""} );

        } else {
            // sort elements by name
            crapview.sortList( logs_list );
            // fill table rows
            for ( int i=0; i<logs_list.size(); i++ ) {
                m.addRow( new Object[] {logs_list.get(i)} );
            }
        }
        table_list.setModel(m);
    }
    
    public static DefaultListModel<String> getLogsList( String logs_dir_ ) {
        DefaultListModel<String> logs_list = new DefaultListModel<>();
        
        File logs_dir = new File( logs_dir_ );
        // retrieve available sessions
        if ( logs_dir.exists() ) {
            FileFilter logFileFilter = (File file) -> file.isFile();
            for ( File log_file : logs_dir.listFiles(logFileFilter) ) {
                // all sessions folders (dates)
                if ( log_file.exists() ) {
                    // path exists
                    if ( log_file.isDirectory() ) {
                        JOptionPane.showMessageDialog(null,
                            String.format("An error occured with:\n'%s'\n\nIt was supposed to be a file, but it was found to be a directory",
                                log_file.getAbsolutePath()),
                            "File-type error", 2);
                    } else {
                        String file_name = log_file.getName();
                        // only append files with default names
                        if (file_name.startsWith("access.log.")
                        ||  file_name.startsWith("error.log.")) {
                            if (( file_name.endsWith(".gz") && !file_name.endsWith(".tar.gz"))
                            ||  file_name.endsWith(".1")
                            ||  file_name.endsWith(".2")
                            ||  file_name.endsWith(".3")) {
                                // valid file-name, may be a valid log file
                                if ( Files.isReadable(Paths.get( log_file.getAbsolutePath() )) ) {
                                    // is readable, add to list
                                    logs_list.addElement( file_name );
                                } else {
                                    // warn for unreadable file
                                    JOptionPane.showMessageDialog(null,
                                        String.format("Warning on file:\n'%s'\n\nThis file cannot be read.\nIf you was planning to use it,\nplease check permissions and retry",
                                            log_file.getAbsolutePath()),
                                        "File not readable", 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        if ( !logs_list.isEmpty() ) {
            crapview.sortList( logs_list );
        }
        return logs_list;
    }

    
    public static ArrayList<DefaultListModel<String>> getStatsList(
        String stats_dir_, // path to statistics folder
        String stat_type,  // "access" / "error"
        String stat_field  // "IP"/"REQ"/"RES"/"UA" / "ERR"/"LEV"
    ) {
        ArrayList<DefaultListModel<String>> names_paths = new ArrayList<>();
        DefaultListModel<String> names = new DefaultListModel<>();
        DefaultListModel<String> paths = new DefaultListModel<>();
        // define base paths
        File global_stats_file = new File(String.format("%s/globals/%s/%s.crapstat",stats_dir_,stat_type,stat_field) );
        File session_stats_dir = new File(String.format("%s/sessions/%s",stats_dir_,stat_type) );
        // globals first
        if ( global_stats_file.exists() ) {
            // global statistics file found
            names.addElement("GLOBALS");
            paths.addElement( global_stats_file.getPath() );
        } else {
            // no global statistics file found for this field
            names.addElement("GLOBALS");
            paths.addElement("NOT_FOUND");
        }
        // retrieve available sessions
        if ( session_stats_dir.exists() ) {
            File stat;
            FileFilter dirFileFilter = (File file) -> file.isDirectory();
            for ( File session : session_stats_dir.listFiles(dirFileFilter) ) {
                // all sessions folders (dates)
                stat = new File( String.format("%s/%s.crapstat", session.getPath(),stat_field) );
                if ( stat.exists() ) {
                    // path exists
                    if ( stat.isDirectory() ) {
                        JOptionPane.showMessageDialog(null,
                            String.format("An error occured with:\n'%s'\n\nIt was supposed to be a file, but it was found to be a directory",
                                stat.getAbsolutePath()),
                            "File-type error", 2);
                    } else {
                        names.addElement(session.getName());
                        paths.addElement(stat.getPath());
                    }
                }
            }
        }
        // put in a single list and return it
        names_paths.add(names);
        names_paths.add(paths);
        return names_paths;
    }
    
    
    private static String[] readStatFile( String file ) {
        // try reading file
        String[] data = {};
        try {
            InputStream f_in = Files.newInputStream( Paths.get(file) );
            BufferedInputStream buff_in = new BufferedInputStream( f_in );
            // get data
            String f = new String( buff_in.readAllBytes() );
            // close
            buff_in.close();
            f_in.close();
            data = f.split("\n");

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                String.format("An error occured while searching for file:\n'%s'",file),
                "File not found", 0);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                String.format("An error occured while reading file:\n'%s'",file),
                "Error reading", 0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                String.format("An error occured while parsing file:\n'%s'\n\nCorrupted file content",file),
                "Error handling file", 0);
        }
        return data;
    }
    

    public static void MakeTable(
        String stat_file_path,
        String stat_field,
        JTable table
    ) {
        String column1;
        String column2 = "Count";
        switch ( stat_field ) {
            case "IP":
                column1 = "IP address";
                break;
            case "REQ":
                column1 = "Requested page";
                break;
            case "RES":
                column1 = "Response code";
                break;
            case "UA":
                column1 = "User agent";
                break;
            case "ERR":
                column1 = "Error description";
                break;
            case "LEV":
                column1 = "Warning level";
                break;
            default:
                column1 = "";
                System.out.println("Unrecognized stat-type: "+stat_field);
                System.exit(1);
        }
        // read file content
        String[] stat_data = {};
        if ( !stat_file_path.equals("NOT_FOUND") ) {
            stat_data = crapview.readStatFile( stat_file_path );
        }
        DefaultTableModel m = new DefaultTableModel();
        m.addColumn(column1);
        m.addColumn(column2);
        if ( stat_data.length == 0 ) {
            // no data to show
            m.addRow( new Object[] {"",""} );

        } else {
            // fill table rows
            String item;
            int count, separator;
            for ( String line : stat_data ) {
                separator = line.indexOf(' ');
                count = Integer.parseInt( line.substring(0, separator) );
                item  = line.substring(separator+1);
                m.addRow( new Object[] { item, count } );
            }
        }
        table.setModel(m);
    }
}
