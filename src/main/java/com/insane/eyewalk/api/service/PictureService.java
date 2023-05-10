package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.config.AppConfig;
import com.insane.eyewalk.api.model.domain.*;
import com.insane.eyewalk.api.repositories.PictureRepository;
import com.insane.eyewalk.api.utils.Tool;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final PictureRepository pictureRepository;
    private final AppConfig appConfig;

    /**
     * Method to retrieve a picture using its filename
     * @param filename picture file name
     * @return Picture object
     * @throws NoSuchElementException if no image is found
     */
    public Picture getByName(String filename) throws NoSuchElementException {
        return pictureRepository.findByFilename(filename).orElseThrow();
    }

    /**
     * Method to get a Http Header containing the content disposition related to the picture
     * @param picture the picture that will be sent to the client.
     * @return Http Header
     */
    public HttpHeaders getHeaders(Picture picture) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(getContentDisposition(picture));
        return headers;
    }

    /**
     * Private method used on the Http Header to add the content disposition.
     * @param picture the picture that will be sent it to the client.
     * @return a Content Disposition
     */
    private ContentDisposition getContentDisposition(Picture picture) {
        ContentDisposition contentDisposition;
        if (picture.getExtension().equalsIgnoreCase("jpg") || picture.getExtension().equalsIgnoreCase("jpeg")) {
            contentDisposition = ContentDisposition.builder("inline").filename(picture.getFilename()+".jpg").build();
        } else if (picture.getExtension().equalsIgnoreCase("png")) {
            contentDisposition = ContentDisposition.builder("inline").filename(picture.getFilename()+".png").build();
        } else {
            contentDisposition = ContentDisposition.builder("inline").filename(picture.getFilename()+"."+picture.getExtension()).build();
        }
        return contentDisposition;
    }

    /**
     * Method to retrieve a Media Type from a saved Picture. This method will determine if the picture is jpg, png or other type of stream file.
     * @param picture the picture that will be sent to the client.
     * @return a MediaType IMAGE_JPEG or IMAGE_PNG or else APPLICATION_OCTET_STREAM for all other type of files.
     */
    public MediaType getMediaType(Picture picture) {
        MediaType mediaType;
        if (picture.getExtension().equalsIgnoreCase("jpg") || picture.getExtension().equalsIgnoreCase("jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (picture.getExtension().equalsIgnoreCase("png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return mediaType;
    }

    public byte[] streamPicture(Picture picture) throws IOException {
        return Files.readAllBytes(Paths.get(appConfig.getPicturePath(Tool.concatFileExtension(picture.getFilename(), picture.getExtension()))));
    }

    /**
     * Method to persist pictures on the database and store them in the server
     * @param pictureFiles MultipartFile list containing images type jpg or png
     * @return List of Picture
     * @throws InvalidFileNameException if the type of file doesn't match jpg or png
     */
    public List<Picture> savePictures(List<MultipartFile> pictureFiles) throws InvalidFileNameException {
        // CREATE A LIST OF IMAGES IF FILE EXISTS, IS NOT NULL AND SAVE FILES INTO SERVER AND REPOSITORY
        List<Picture> pictures = new ArrayList<>();
        if (pictureFiles != null && !pictureFiles.isEmpty()) {
            for (MultipartFile file : pictureFiles) {
                try {
                    String extension = Tool.getFileExtension(file);
                    String filename = String.valueOf(new Date().getTime());
                    if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg")) {
                        if (saveFile(file, Tool.concatFileExtension(filename, extension))) {
                            pictures.add(pictureRepository.save(
                                    Picture.builder().filename(filename).extension(extension).created(new Date()).build())
                            );
                        }
                    } else throw new InvalidFileNameException(file.getOriginalFilename(), "File type not allowed!");
                } catch (IOException | NullPointerException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return pictures;
    }

    /**
     * Private method used by PictureService to save a file in the picture path folder on server
     * @param file MultipartFile received on the HttpServlet request by the controller
     * @param filename the desired filename to save on server
     * @return boolean true if file was saved successfully
     * @throws IOException if server doesn't allow to save
     */
    private boolean saveFile(MultipartFile file, String filename) throws IOException {
        if (Tool.checkCreateFolder(appConfig.getPicturePath())) {
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(appConfig.getPicturePath(filename));
                    Files.write(path, bytes);
                    return true;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        } else {
            throw new IOException("It was not possible to create folder "+appConfig.getPicturePath());
        }
        return false;
    }

}
