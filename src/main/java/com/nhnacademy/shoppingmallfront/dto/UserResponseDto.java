package com.nhnacademy.shoppingmallfront.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class UserResponseDto {
    @JsonProperty("id")
    private String userId;
    @JsonProperty("name")
    private String userName;
}
