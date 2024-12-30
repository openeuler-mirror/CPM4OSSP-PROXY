package io.jpom.model.system;

import lombok.Data;
import lombok.ToString;

/**
 * @author zywang
 * @date 2022/8/15 16:52:09
 **/

@Data
@ToString
public class CpuTime {
//                CPU user nice  system idle iowait irq softirq stealstolen guest

    Double user;

    Double nice;

    Double system;

    Double idle;

    Double ioWait;

    Double irq;

    Double softIrq;

    Double stealStolen;

    Double guest;

    public Double getTotalTIme (){
        return user + nice + system + idle + ioWait +
                irq + softIrq + stealStolen + guest;
    }

    public Double getIdleTime(){
        return idle;
    }
}
