package com.ekold.utils;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/9/26
 */
@Data
@Accessors(chain = true)
public class SubjectVO {

    private String subject;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String answer;
}