package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.model.domain.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
}
