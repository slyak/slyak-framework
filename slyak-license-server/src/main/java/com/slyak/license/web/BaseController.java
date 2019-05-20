package com.slyak.license.web;

import com.slyak.core.util.DateUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;


@ControllerAdvice
public class BaseController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
    }


    public class DateEditor extends PropertyEditorSupport {
        @Override
        public String getAsText() {
            return DateUtils.DATETIME_FORMAT.format(getValue());
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(DateUtils.parse(text));
        }
    }
}
