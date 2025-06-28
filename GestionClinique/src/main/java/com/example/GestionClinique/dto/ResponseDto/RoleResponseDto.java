package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class RoleResponseDto extends BaseResponseDto {
    private RoleType roleType;
}