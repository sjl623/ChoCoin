package cn.scnu.team.API;

public class Response {
    public Response(String type, String content) {
        this.type = type;
        this.content = content;
    }


    String type;
    String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
