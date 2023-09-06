package nuc.edu.seckill.security;



import lombok.Getter;
import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.common.exception.ErrorMessage;
import org.springframework.security.core.AuthenticationException;


@Getter
public class BaseAuthenticationException extends AuthenticationException {
    private Integer code;

    public BaseAuthenticationException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public static BaseAuthenticationException error(ErrorMessage message) {
        return new BaseAuthenticationException(message.getCode(), message.getMessage());
    }
    public BaseResponse resp() {
        return BaseResponse.error(code, getMessage());
    }
}
