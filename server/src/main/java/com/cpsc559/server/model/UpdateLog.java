package com.cpsc559.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class UpdateLog {
    @Id
    private Integer timestamp;

    private String method;
    private String path;

    @Lob
    private String headersJson;

    @Lob
    private String body;
}