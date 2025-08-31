package com.example.GestionClinique.dto.ResponseDto;


import lombok.Data;


import java.time.LocalDateTime;

@Data
public abstract class BaseResponseDto {
    protected Long id;

    protected LocalDateTime creationDate;

    protected LocalDateTime modificationDate;
}
