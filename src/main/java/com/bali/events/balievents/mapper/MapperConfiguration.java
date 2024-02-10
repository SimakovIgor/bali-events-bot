package com.bali.events.balievents.mapper;

import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;

@MapperConfig(
    componentModel = "spring",
    builder = @Builder(
        disableBuilder = true
    ),
    injectionStrategy = InjectionStrategy.FIELD,
    implementationPackage = "<PACKAGE_NAME>.impl",
    implementationName = "<CLASS_NAME>Impl"
)
public interface MapperConfiguration {
}
