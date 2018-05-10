package com.slyak.web.support.freemarker.bootstrap;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * .
 *
 * @author stormning 2018/5/9
 * @since 1.3.0
 */
@Data
@ToString
public class Fileinput implements Serializable{
    //initialPreviewConfig: [
//    {type: "image", caption: "Image-1.jpg", size: 847000, url: "/amp/project/delFile.do", key: 1},
    String key;
    String caption;
    long size;
}
