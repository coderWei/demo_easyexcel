package com.example.demo.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.demo.bean.SheetOneVO;
import com.example.demo.bean.TemplateOneVO;
import com.example.demo.handler.XdxCellWriteHandler;
import org.apache.commons.codec.CharEncoding;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
@RequestMapping("/one")
public class OneController {

    // 测试数据构建
    private static final List<String> countryList = Arrays.asList("中国","美国","俄罗斯","德国","日本");
    private static final List<String> cityList = Arrays.asList("深圳","广州","上海","北京","纽约","莫斯科","东京");
    private static final Map<String, List<String>> dropDownData = new HashMap<>(2);
    private static final List<SheetOneVO> cityEntityList = new ArrayList<>(cityList.size());
    static {
        dropDownData.put("country", countryList);
        dropDownData.put("city", cityList);

        for (String item : cityList) {
            cityEntityList.add(new SheetOneVO(item));
        }
    }



    @GetMapping("/kk-one")
    public void one(HttpServletResponse response) throws Exception {

        // 通用内容设置
        String fileName = URLEncoder.encode("templateOne.xlsx", String.valueOf(StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);

        // 构建模板数据
        ExcelWriter excelWriter = EasyExcel
                .write(response.getOutputStream())
                .build();
        WriteSheet writeSheet = EasyExcel
                .writerSheet(0, "one")
                .registerWriteHandler(new XdxCellWriteHandler(dropDownData))
                .head(TemplateOneVO.class)
                .build();

        WriteSheet citySheet = EasyExcel
                .writerSheet(1, "city")
                .head(SheetOneVO.class)
                .needHead(false)
                .build();


        excelWriter.write(Collections.singletonList(new TemplateOneVO("张三", "美国", "胡佛")), writeSheet)
                .write(cityEntityList, citySheet)
                .finish();
    }
}