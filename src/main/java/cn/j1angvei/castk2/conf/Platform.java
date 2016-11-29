package cn.j1angvei.castk2.conf;

/**
 * Created by mjian on 2016/11/29.
 */
public class Platform {
    private String java;
    private String perl;
    private String python;
    private String r;

    public Platform() {
    }

    public String getJava() {
        return java;
    }

    public String getPerl() {
        return perl;
    }

    public String getPython() {
        return python;
    }

    public String getR() {
        return r;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "java='" + java + '\'' +
                ", perl='" + perl + '\'' +
                ", python='" + python + '\'' +
                ", r='" + r + '\'' +
                '}';
    }
}
