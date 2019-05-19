package com.slyak.license.domain;

import com.slyak.core.util.DateUtils;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_license")
@Entity
public class License {
    /**
     * 机器码
     */
    @Id
    private String machineCode;

    /**
     * 失效时间
     */
    private Date expire = DateUtils.addDays(new Date(), 1);
}
