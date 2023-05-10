package com.insane.eyewalk.api.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.Objects;

public class Tool {

    /**
     * Retrieve extension from a file
     * @param filename type String
     * @return a String containing the file extension
     */
    public static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".")+1);
    }

    /**
     * Retrieve extension from a file
     * @param file type MultipartFile
     * @return a String containing the file extension
     * @throws NullPointerException if the file is null
     */
    public static String getFileExtension(MultipartFile file) throws NullPointerException {
        return getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
    }

    /**
     * Method to check if a folder exists. If it doesn't exist the method will try to create it
     * @param folderName the folder name to be checked or created
     * @return boolean true if exists or if it was created successfully
     */
    public static boolean checkCreateFolder(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) return folder.mkdirs();
        else return true;
    }

    /**
     * Method to concatenate a filename with its extension
     * @param filename a file name
     * @param extension a file extension
     * @return a String with the filename and extension concatenated with a period.
     */
    public static String concatFileExtension(String filename, String extension) {
        return filename+"."+extension;
    }

}
