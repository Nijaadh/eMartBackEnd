package com.example.projectBackEnd.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private boolean status = false;
    private List<String> errorMessages = new ArrayList<>();
    private List<Object> payload = null;
}
