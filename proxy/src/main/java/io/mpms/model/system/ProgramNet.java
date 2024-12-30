package io.jpom.model.system;

import lombok.Data;

/**
 * @author zywang
 * @date 2022/10/27 16:26:21
 **/

@Data
public class ProgramNet {
    private int PORT;

    private String PROTO;

    private String PROGRAM;

    private String pid;
}
