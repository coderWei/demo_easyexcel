package com.example.demo.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateOneVO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("国家")
    private String country;

    @ExcelProperty("城市")
    private String city;
}