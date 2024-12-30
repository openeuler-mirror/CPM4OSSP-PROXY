package io.jpom.model.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class CpuMessage implements Serializable {
    /**
     * 当前进程使用cpu时间
     */
    private Double processTime;

    /**
     * 总cpu运行时间
     */
    private Double totalTime;
    private String pid;
}
