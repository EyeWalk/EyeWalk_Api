package com.insane.eyewalk.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${api.host}")
    private String HOST;
    @Value("${HOME}")
    private String HOME;
    @Value("${api.media.folder}")
    private String PICTURE_FOLDER;

    public String getPicturePath() {
        return this.HOME +"/"+ PICTURE_FOLDER;
    }

    public String getPicturePath(String filename) {
        return this.HOME +"/"+ PICTURE_FOLDER +"/"+filename;
    }

}
