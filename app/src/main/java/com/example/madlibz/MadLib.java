package com.example.madlibz;

import java.util.List;

public class MadLib {
    //title
    String title;
    //blanks
    List<String> blanks;
    //story
    List<String>value;

    public MadLib(String t, List<String>b, List<String>v) {
        this.title = t;
        this.blanks = b;
        this.value = v;
    }
}

