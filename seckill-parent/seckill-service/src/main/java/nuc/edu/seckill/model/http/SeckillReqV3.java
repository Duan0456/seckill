package nuc.edu.seckill.model.http;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author han
 * @version 1.0
 * @date 2020/07/29
 */
@Data
public class SeckillReqV3 implements Serializable {

    @NotNull(message = "产品id 不能为空")
    private Long productId;

    private Long userId;
    @NotEmpty(message = "图片验证码标识 不能为空")
    private String imageId;
    @NotEmpty(message = "图片验证码 不能为空")
    private String imageCode;
}
