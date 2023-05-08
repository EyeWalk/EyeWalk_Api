package com.insane.eyewalk.api.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ModelMapperList {

    public final ModelMapper modelMapper;
    public <SOURCE, TARGET> List<TARGET> mapList(List<SOURCE> source, Class<TARGET> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

}