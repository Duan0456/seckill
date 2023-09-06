package nuc.edu.seckill.model.enums;

/**
 * MQ消费者节点业务命名配置
 *
 * @author: han
 * @since: 2020-07-23 09:28
 **/
public enum MqNodesEnum {

    delCache("delCache"),
    order("order");

    private String name;


    MqNodesEnum(String name) {
        this.name = name;
    }

    public static MqNodesEnum getEnumsByValue(String name) {
        MqNodesEnum[] arrays = values();
        for (MqNodesEnum type : arrays) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
