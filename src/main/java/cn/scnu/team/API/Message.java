package cn.scnu.team.API;

public class Message {
    public Message(String methodName, String parameter) {
        this.methodName = methodName;
        this.parameter = parameter;
    }

    public String methodName;
    public String parameter;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}