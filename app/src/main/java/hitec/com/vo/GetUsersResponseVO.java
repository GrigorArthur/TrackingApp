package hitec.com.vo;

import com.google.gson.annotations.SerializedName;

public class GetUsersResponseVO extends BaseResponseVO{
    @SerializedName("users")
    public String users;
}
