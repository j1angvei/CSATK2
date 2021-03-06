package cn.j1angvei.castk2.panther;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wayne on 3/8 2017.
 */
public class PantherCookieJar implements CookieJar {
    private static final String SESSION_KEY = "JSESSIONID";
    private List<Cookie> mCookies;

    public PantherCookieJar() {
        mCookies = new ArrayList<>();
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        mCookies.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return mCookies;
    }

    public String getJSessionId() {
        for (Cookie cookie : mCookies) {
            if (cookie.name().equals(SESSION_KEY)) {
                return cookie.value();
            }
        }
        return null;
    }

    public void resetJSessionId() {
        for (Cookie cookie : mCookies) {
            if (cookie.name().equals(SESSION_KEY)) {
                mCookies.remove(cookie);
            }
        }
    }
}
