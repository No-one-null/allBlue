package com.zhao.test;

import com.zhao.shiro.realm.AuthRealm;
import com.zhao.util.CommonUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import java.util.regex.Pattern;

import static com.zhao.util.CommonUtil.*;

public class UserRealmTest {
    @Test
    public void testAuthenticationToken() {
        AuthRealm userRealm = new AuthRealm();
        //构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(userRealm);

        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");
        matcher.setHashIterations(1);
        userRealm.setCredentialsMatcher(matcher);

        //主题提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zero", "123456");
        subject.login(token);

        System.out.println("isAuthenticationToken:" + subject.isAuthenticated());
    }

    @Test
    public void Md5Test() {
        Md5Hash md5Hash1 = new Md5Hash("000000", "zero");
        Md5Hash md5Hash2 = new Md5Hash("admin", "admin");
        Md5Hash md5Hash3 = new Md5Hash("password", "user");
        Md5Hash md5Hash4 = new Md5Hash("root", "root");
        Md5Hash md5Hash5 = new Md5Hash("那撸多", "123456");
        System.out.println("000000(zero):" + md5Hash1);
        System.out.println("admin(admin):" + md5Hash2);
        System.out.println("password(user):" + md5Hash3);
        System.out.println("root(root):" + md5Hash4);
        System.out.println("123456(那撸多)" + md5Hash5);
    }

    @Test
    public void strTest() {
        String str = "/20200225130036/1.jpg,/20200225130036/2.jpg,/20200225130036/3.jpg,/20200225130036/4.png,/20200225130036/5.png,/20200225130036/6.png";
        System.out.println(str.substring(str.indexOf('/'), str.indexOf('/') + 1));
        System.out.println(str.substring(0, str.indexOf('/', str.indexOf('/') + 1)));
    }

    @Test
    public void testNumber() {
        CommonUtil v = new CommonUtil();
    }

    @Test
    public void test() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            str.append(i).append(",");
        }
        System.out.println(str.substring(0, 20));
    }

    @Test
    public void testZhengZe() {
        String pattern = "^#[\\s\\S]*#$";
        boolean isMatch1 = Pattern.matches(pattern, "#456#");
        boolean isMatch2 = Pattern.matches(pattern, "1#456#2");
        boolean isMatch3 = Pattern.matches(pattern, "##");
        System.out.println(isMatch1 + "|" + isMatch2 + "|" + isMatch3);
        String str = "#123#";
        System.out.println(str.substring(1, str.length() - 1));
        String str2 = "##";
        System.out.println(str2.substring(1, str2.length() - 1));
    }

    @Test
    public void testReplace(){
        String str="_";
        String str1="__";
        String str2="_1_";
        System.out.println(repWildcard(str)+"|"+repWildcard(str1)+"|"+repWildcard(str2));
        str="%";
        str1="%%";
        str2="%1%";
        System.out.println(repWildcard(str)+"|"+repWildcard(str1)+"|"+repWildcard(str2));
    }
}
