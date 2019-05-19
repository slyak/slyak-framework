package com.slyak.license.domain;

import com.slyak.core.util.DateUtils;
import lombok.Data;

import javax.persistence.Column;
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
    @Column
    private String id;

    /**
     * 失效时间
     */
    @Column
    private Date expire = DateUtils.addDays(new Date(), 1);
}
