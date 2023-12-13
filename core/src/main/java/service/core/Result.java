package service.core;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Result implements java.io.Serializable {

    public Result(String input, String output, String expectedOutput, ResultFlag flag) {
        this.input = input;
        this.output = output;
        this.expectedOutput = expectedOutput;
        this.flag = flag;
    }

    public ResultFlag flag;
    public String input;
    public String output;
    public String expectedOutput;
};

;