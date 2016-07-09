package com.lightcone.writesdcard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MEDIA";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.TextView01);
        checkExternalMedia();
        writeToSDFile();
        readRaw();
    }

    /**
     * Method to check whether external media available and writable. This is adapted from
     * http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
     */

    private void checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
            tv.append("\n\nEXTERNAL MEDIA: readable="
                + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }

    /**
     * Method to write ascii text characters to file on SD card. In earlier versions of Android a
     * WRITE_EXTERNAL_STORAGE permission must be added to the manifest file or this method will throw
     * a FileNotFound Exception because you won't have write permission. But not true after
     * API 18 for files in storage area of app (then no write permission required).
     */

    private void writeToSDFile() {

        // Root of the external file system

        File root0 = android.os.Environment.getExternalStorageDirectory();

        /* Now find the root of the external storage for this app (where the app can place
        * persistent files that it owns internal to the application and not typically visible
        * to the user as media).  See
        *
        *    http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
        *
        * The method getExternalFilesDir(string) returns the user storage associated with the
        * app, which doesn't require write permissions after API 18.  The string argument specifies various
        * regions of this storage.  For example,
        *
        * - null specifies the root of the storage for this app
        * - Environment.DIRECTORY_NOTIFICATIONS specifies the Notifications directory of app storage
        * - Environvment.DIRECTORY_DOWNLOADS specifies standard directory for files downloaded by user
        * - Environment.DIRECTORY_PICTURES specifies standard directory for pictures available to the user
        * - Environment.DIRECTORY_DOCUMENTS specifies standard directory for documents produced by user
        * etc.
        *
        * See the fields of the Environment class at
        *    https://developer.android.com/reference/android/os/Environment.html
        * for other possibilities.  For example, on my phone (running Android 6.0.1) the root of
        * the user storage for this specific app is found at
        *
        *    /storage/emulated/0/Android/data/com.lightcone.writesdcard/files
        * */

        // Root of the data directories Documents subdirectory specific to this app, for which no write
        // permission is required for Android 4.4 and later.

        File root = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        tv.append("\n\nEXTERNAL FILE SYSTEM ROOT DIRECTORY:\n" + root0);

        tv.append("\n\nEXTERNAL APP DATA ROOT DIRECTORY:\n" + root);

        // Create a Documents/download subdirectory in the data area for this app
        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "myData.txt");

        // Must catch FileNotFoundException and IOException
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Howdy do to you,");
            pw.println("and the horse you rode in on.");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "File not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "I/O exception");
        }
        tv.append("\n\nFILE WRITTEN TO:\n" + file);
    }

    /**
     * Method to read in a text file placed in the res/raw directory of the application. The
     * method reads in all lines of the file sequentially.
     */

    private void readRaw() {
        tv.append("\n\nDATA READ FROM res/raw/textfile.txt:\n");
        InputStream is = this.getResources().openRawResource(R.raw.textfile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

        // More efficient (less readable) implementation of above is the composite expression
        //    BufferedReader br = new BufferedReader(new InputStreamReader(
        //             this.getResources().openRawResource(R.raw.textfile)), 8192);

        try {
            String test;
            while (true) {
                test = br.readLine();
                // readLine() returns null if no more lines in the file
                if (test == null) break;
                tv.append("\n" + "    " + test);
            }
            isr.close();
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv.append("\n\nTHAT IS ALL");
    }
}
