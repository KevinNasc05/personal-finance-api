package com.project.finance.api.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public interface GenericController {

    default URI generateUriLocation(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    default URI generateUserUriLocation() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/me")
                .build()
                .toUri();
    }

}
