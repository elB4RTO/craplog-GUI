
package crapcode.craputils;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class backup {
    
    public static void backupGlobals(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        String stats_dir
    ) {
        // backup global statistics
        String globals_path = String.format("%s/globals",stats_dir);
        String backups_path = String.format("%s/globals/.backups",stats_dir);
        // check the existence of previous backups
        File backups_dir = new File( backups_path );
        if ( backups_dir.exists() ) {
            // already exists, scale-rename existing directories
            File backup_dir;
            for ( int n=3; n>=1; n-- ) {
                backup_dir = new File( String.format("%s/%s",backups_path,n) );
                if ( backup_dir.exists() ) {
                    if ( n != 3 ) {
                        // increase name only if minor than the last (which here comes first)
                        try {
                            // copy to a scaled name
                            Path new_backup_dir = Paths.get(String.format("%s/%s",backups_path,n+1)).toAbsolutePath();
                            ioutils.createDir( proceed, undo, new_backup_dir );
                            if ( proceed.get("state").equals("false") ) {
                                break;
                            }
                            ioutils.copyDirRecursive(
                                proceed, undo,
                                backup_dir.toPath().toAbsolutePath(),
                                new_backup_dir );
                            // add the newely created dir to the undo-paths
                            undo.add( Paths.get(String.format("%s/%s",backups_path,n+1)) );

                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null,
                                String.format("An error occured while backing-up globals folder:\n'%s'",
                                    Paths.get(String.format("%s/%s",backups_path,n)).toAbsolutePath() ),
                                "Error copying folder", 2);
                            proceed.replace("state", "false");
                            break;
                        }
                    }
                    // rename the now-old dir to a copy
                    if ( proceed.get("state").equals("true") ) {
                        ioutils.newDirName( proceed, undo, Paths.get(backup_dir.getAbsolutePath()), true);
                    }
                }
            }
        } else {
            // create the backups directory
            ioutils.createDir( proceed, undo, Paths.get(backups_path).toAbsolutePath() );
        }
        
        // initial process went fine, make the new backups dir: 1
        if ( proceed.get("state").equals("true") ) {
            ioutils.createDir( proceed, undo, Paths.get(String.format("%s/1",backups_path)).toAbsolutePath() );
        }
        
        // everything went fine, backup actual globals
        if ( proceed.get("state").equals("true") ) {
            String[] dirs = {"access","error"};
            String[] acc = {"IP","REQ","RES","UA"};
            String[] err = {"ERR","LEV"};
            String[] files;
            Path file_path, new_file_path;
            // check both access/error directories
            for ( String dir_name : dirs ) {
                if ( Files.exists( Paths.get(String.format("%s/%s",globals_path,dir_name)).toAbsolutePath() ) ) {
                    // probably there are global stats in this directory
                    if ( !Files.exists( Paths.get(String.format("%s/1/%s",backups_path,dir_name)).toAbsolutePath() ) ) {
                        // create the relative backup dir
                        ioutils.createDir( proceed, undo, Paths.get(String.format("%s/1/%s",backups_path,dir_name)).toAbsolutePath() );
                    }
                    if ( proceed.get("state").equals("false") ) {
                        break;
                    }
                    // pick-up the corresponding field-names list
                    if ( dir_name.equals("access") ) {
                        files = acc;
                    } else {
                        files = err;
                    }
                    // attempt a copy for every file
                    for ( String file_name : files ) {
                        file_path     = Paths.get(String.format("%s/%s/%s.crapstat",globals_path,dir_name,file_name)).toAbsolutePath();
                        new_file_path = Paths.get(String.format("%s/1/%s/%s.crapstat",backups_path,dir_name,file_name)).toAbsolutePath();
                        if ( Files.exists( file_path ) ) {
                            // this stat file exists, make a backup
                            try {
                                if ( Files.exists( new_file_path ) ) {
                                    // surprisingly the new file already exists, remove the conflict before to copy
                                    ioutils.newFileName( proceed, undo, file_path, false);
                                }
                                if ( proceed.get("state").equals("false") ) {
                                    break;
                                }
                                // proceed with the copy
                                Files.copy(
                                    Paths.get(String.format("%s/%s/%s.crapstat",globals_path,dir_name,file_name)).toAbsolutePath(),
                                    Paths.get(String.format("%s/1/%s/%s.crapstat",backups_path,dir_name,file_name)).toAbsolutePath() );

                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null,
                                    String.format("An error occured while backing-up globals file:\n'%s'",
                                        Paths.get(String.format("%s/%s/%s.crapstat",globals_path,dir_name,file_name)).toAbsolutePath() ),
                                    "Error copying file", 2);
                                proceed.replace("state", "false");
                                break;
                            }
                        }
                    }
                    // if something went wrong in the above loop, break the parent one too
                    if ( proceed.get("state").equals("false") ) {
                        break;
                    }
                }
            }
        }
    }
    

    public static void backupOriginalFiles(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        String stats_dir, String backup_date,
        String logs_dir, List<String> target_files
    ) {
        // backup source files
        Path path = Paths.get( String.format("%s/backups",stats_dir) );
        // check the existence of the 'crapstats/globals' dir
        if ( !Files.exists(path) ) {
            // 'backups' directory doesn't exists
            ioutils.createDir(proceed, undo, path);
        } else if ( !Files.isDirectory(path) ) {
            // 'backups' exists but it's a file, make a copy of it
            ioutils.newFileName(proceed, undo, path, false);
            if (proceed.get("state").equals("true")) {
                // create the dir
                ioutils.createDir(proceed, undo, path);
            }
        }
        if (proceed.get("state").equals("true")) {
            // today (date of execution)
            path = Paths.get( String.format("%s/backups/%s",stats_dir,backup_date) );
            if ( !Files.exists(path) ) {
                ioutils.createDir(proceed, undo, path);
            } else if ( !Files.isDirectory(path) ) {
                ioutils.newFileName(proceed, undo, path, false);
                if (proceed.get("state").equals("true")) {
                    ioutils.createDir(proceed, undo, path);
                }
            }
        }
        Path orig_path;
        for ( String file : target_files ) {
            if (proceed.get("state").equals("false")) {
                break;
            }
            // make a copy of original files
            orig_path = Paths.get(String.format("%s/%s",logs_dir,file));
            path      = Paths.get(String.format("%s/backups/%s/%s",stats_dir,backup_date,file));
            if (Files.exists(path) ) {
                if ( Files.isDirectory(path) ) {
                    ioutils.newDirName(proceed, undo, path, false);
                } else {
                    ioutils.newFileName(proceed, undo, path, false);
                }
            }
            if (proceed.get("state").equals("false")) {
                break;
            }
            try {
                Files.copy(orig_path, path);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    String.format("An error occured while making a backup copy of:\n'%s'",orig_path),
                    "Error copying file", 0);
                JOptionPane.showMessageDialog(null,
                    String.format("The error was probably generated by a missing permission.\n\nPreviously created statistics CANNOT be un-done,\nand doing a MANUAL BACKUP IS REQUIRED!\n\nPlease consider modifying files permissions if you're plannig to backup files again."),
                    "BACKUP FAILED", 0);
                proceed.replace("state", "false");
                break;
            }
        }
    }
    
    
    public static void compressBackups(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        String stats_dir, String backup_date,
        List<String> target_files,
        int compressionType
    ) {
        String path = String.format("%s/backups/%s",stats_dir,backup_date);
        // choose the choosen choice
        switch (compressionType) {
            
            case 0:
                // tar.gz
                backup.TarGz(
                    proceed, undo,
                    path, target_files);
                break;
            case 1:
                // tar
                backup.Tar(
                    proceed, undo,
                    path, target_files);
                break;
            case 2:
                // zip
                backup.Zip(
                    proceed, undo,
                    path, target_files);
                break;
            default:
                // WTF!
                System.out.println(String.format("crapcode.craputils.backup.compressBackups() entered with compressionType: (%s), expected to be {0,1,2}",compressionType));
        }
        
        if (proceed.get("state").equals("true")) {
            Path p;
            for ( String file : target_files ) {
                p = Paths.get(String.format("%s/backups/%s/%s",stats_dir,backup_date,file));
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                        String.format("An error occured while deleting temporary file:\n'%s'\n\nThe process cannot be un-done and will not be aborted now\nPlease consider to manually remove the file\nand/or to report this issue",p),
                        "Error removing temporary file", 2);
                }
            }
        }
    }
    
    protected static void TarGz(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        String path_, List<String> target_files
    ) {
        Path archive_path = Paths.get(String.format("%s/backup.tar.gz",path_));
        if ( Files.exists(archive_path) ) {
            if ( Files.isDirectory(archive_path) ) {
               ioutils.newDirName(proceed, undo, archive_path, false);
            } else {
               ioutils.newFileName(proceed, undo, archive_path, false);
            }
        }
        if (proceed.get("state").equals("true")) {
            try (
                OutputStream f_out = Files.newOutputStream( archive_path );
                BufferedOutputStream buff_out = new BufferedOutputStream( f_out );
                GzipCompressorOutputStream gz_arc = new GzipCompressorOutputStream( buff_out );
                TarArchiveOutputStream tar_arc    = new TarArchiveOutputStream( gz_arc );
            ) {
                undo.add(archive_path);
                Path path = Paths.get(String.format("%s",path_));
                try {
                    // attempt archiving original files
                    for ( String file : target_files ) { 
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                        path = Paths.get(String.format("%s/%s",path_,file));
                        TarArchiveEntry entry = new TarArchiveEntry( path, file );
                        tar_arc.putArchiveEntry(entry);
                        Files.copy( path, tar_arc );
                        tar_arc.closeArchiveEntry();
                    }
                    tar_arc.finish();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                        String.format("An error occured while archiving file:\n'%s'",path),
                        "Error archiving file", 0);
                    proceed.replace("state", "false");
                }
                tar_arc.close();
                gz_arc.close();
                buff_out.close();
                f_out.close();
            
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    String.format("An error occured while creating backup archive:\n'%s'",archive_path),
                    "Error creating archive", 0);
                proceed.replace("state", "false");
                
            }
        }
    }
    
    protected static void Tar(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        String path_, List<String> target_files
    ) {
        Path archive_path = Paths.get(String.format("%s/backup.tar",path_));
        if ( Files.exists(archive_path) ) {
            if ( Files.isDirectory(archive_path) ) {
               ioutils.newDirName(proceed, undo, archive_path, false);
            } else {
               ioutils.newFileName(proceed, undo, archive_path, false);
            }
        }
        if (proceed.get("state").equals("true")) {
            try (
                OutputStream f_out = Files.newOutputStream( archive_path );
                BufferedOutputStream buff_out = new BufferedOutputStream( f_out );
                TarArchiveOutputStream tar_arc    = new TarArchiveOutputStream( buff_out );
            ) {
                undo.add(archive_path);
                Path path = Paths.get(String.format("%s",path_));
                try {
                    // attempt archiving original files
                    for ( String file : target_files ) { 
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                        path = Paths.get(String.format("%s/%s",path_,file));
                        TarArchiveEntry entry = new TarArchiveEntry( path, file );
                        tar_arc.putArchiveEntry(entry);
                        Files.copy( path, tar_arc );
                        tar_arc.closeArchiveEntry();
                    }
                    tar_arc.finish();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                        String.format("An error occured while archiving file:\n'%s'",path),
                        "Error archiving file", 0);
                    proceed.replace("state", "false");
                }
                tar_arc.close();
                buff_out.close();
                f_out.close();
            
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    String.format("An error occured while creating backup archive:\n'%s'",archive_path),
                    "Error creating archive", 0);
                proceed.replace("state", "false");
                
            }
        }
    }
    
    protected static void Zip(
        HashMap<String,String> proceed,
        ArrayList<Path> undo,
        String path_, List<String> target_files
    ) {
        Path archive_path = Paths.get(String.format("%s/backup.zip",path_));
        if ( Files.exists(archive_path) ) {
            if ( Files.isDirectory(archive_path) ) {
               ioutils.newDirName(proceed, undo, archive_path, false);
            } else {
               ioutils.newFileName(proceed, undo, archive_path, false);
            }
        }
        if (proceed.get("state").equals("true")) {
            try (
                OutputStream f_out = Files.newOutputStream( archive_path );
                BufferedOutputStream buff_out = new BufferedOutputStream( f_out );
                ZipArchiveOutputStream zip_arc    = new ZipArchiveOutputStream( buff_out );
            ) {
                undo.add(archive_path);
                Path path = Paths.get(String.format("%s",path_));
                try {
                    // attempt archiving original files
                    for ( String file : target_files ) { 
                        if (proceed.get("state").equals("false")) {
                            break;
                        }
                        path = Paths.get(String.format("%s/%s",path_,file));
                        ZipArchiveEntry entry = new ZipArchiveEntry( path, file );
                        zip_arc.putArchiveEntry(entry);
                        Files.copy( path, zip_arc );
                        zip_arc.closeArchiveEntry();
                    }
                    zip_arc.finish();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                        String.format("An error occured while archiving file:\n'%s'",path),
                        "Error archiving file", 0);
                    proceed.replace("state", "false");
                }
                zip_arc.close();
                buff_out.close();
                f_out.close();
            
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    String.format("An error occured while creating backup archive:\n'%s'",archive_path),
                    "Error creating archive", 0);
                proceed.replace("state", "false");
            }
        }
    }
    
}
