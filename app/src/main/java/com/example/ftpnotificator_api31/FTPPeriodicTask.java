package com.example.ftpnotificator_api31;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.SocketException;

import javax.security.auth.login.LoginException;


public class FTPPeriodicTask implements Runnable {

    private String[] ftpParams = new String[4];
    private FTPClient client_instance;
    private static long filecount;
    private static boolean firstCall = true;
    private Context context;

    private Intent onFileFoundUpdateIntent;
    private PendingIntent notifyIntent;
    private AlarmManager alarmManager;

    public FTPPeriodicTask(String[] i_ftpParams, Context i_context) {
        Context context = i_context;
        ftpParams[0] = i_ftpParams[0];
        ftpParams[1] = i_ftpParams[1];
        ftpParams[2] = i_ftpParams[2];
        ftpParams[3] = i_ftpParams[3];

        onFileFoundUpdateIntent = new Intent(context, FTPBroadcastReceiver.class);
        onFileFoundUpdateIntent.putExtra("action", "filefound");
        notifyIntent = PendingIntent.getBroadcast(context, 0, onFileFoundUpdateIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }


    @Override
    public void run() {
        client_instance = new FTPClient();

        try {
            client_instance.connect(ftpParams[0], 21);
            boolean success = client_instance.login(ftpParams[1], ftpParams[2]);

            if (success) {
                client_instance.enterLocalPassiveMode();
                long[] Counted_files_future = calculateDirectoryInfo(client_instance, ftpParams[3], "");
                long Counted_files = Counted_files_future[1];

                //Saved and Currently counted filecount
                if (filecount != Counted_files) {
                    if (!firstCall) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, 0, notifyIntent);
                        Log.d("DebugMessage:", "POPUP COMES HERE");
                        filecount = Counted_files;
                        Log.d("DebugMessage:", "Filecount overwritten, change detected.");
                    } else {
                        firstCall = false;
                        filecount = Counted_files;
                        Log.d("DebugMessage:", "First call of the program, filecount counted.");
                    }
                } else {
                    Log.d("DebugMessage:", "Checked, but no change in file count.");
                }
            } else {
                throw new LoginException();
            }
            client_instance.logout();

        } catch (SocketException e) {
            Log.d("PeriodicTask","Socket Exception, probably port 21 is not open on the remote server.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.d("PeriodicTask","Most likely the calculateDirectoryInfo threw this, something went wrong with the call. Also gets thrown when login-credentials or hostname address is wrong.");
            throw new RuntimeException(e);
        } catch (LoginException e) {
            Log.d("PeriodicTask","The connection did build up, but the log-in credentials were declined. It throws IOException too, but this is the true cause.");
            throw new RuntimeException(e);
        } finally {
            if(client_instance.isConnected()) {
                try {
                    client_instance.disconnect();
                } catch (IOException ioe) {

                }
            }
        }


    }


    /**
     * This method calculates total number of sub directories, files and size
     * of a remote directory.
     *
     * @param ftpClient  An instance of the FTPClient
     * @param parentDir  Path of the remote directory.
     * @param currentDir The current directory (used for recursion).
     * @return An array of long numbers in which:
     * - the 1st number is total directories.
     * - the 2nd number is total files.
     * - the 3rd number is total size.
     * @throws IOException If any I/O error occurs.
     */
    public static long[] calculateDirectoryInfo(FTPClient ftpClient, String parentDir, String currentDir) throws IOException {
        long[] info = new long[3];
        long totalSize = 0;
        int totalDirs = 0;
        int totalFiles = 0;

        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        try {
            FTPFile[] subFiles = ftpClient.listFiles(dirToList);

            if (subFiles != null && subFiles.length > 0) {
                for (FTPFile aFile : subFiles) {
                    String currentFileName = aFile.getName();
                    if (currentFileName.equals(".")
                            || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }

                    if (aFile.isDirectory()) {
                        totalDirs++;
                        long[] subDirInfo =
                                calculateDirectoryInfo(ftpClient, dirToList, currentFileName);
                        totalDirs += subDirInfo[0];
                        totalFiles += subDirInfo[1];
                        totalSize += subDirInfo[2];
                    } else {
                        totalSize += aFile.getSize();
                        totalFiles++;
                    }
                }
            }

            info[0] = totalDirs;
            info[1] = totalFiles;
            info[2] = totalSize;

            return info;
        } catch (IOException ex) {
            throw ex;
        }
    }
}
