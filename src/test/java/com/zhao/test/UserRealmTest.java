package com.zhao.test;

import com.zhao.shiro.realm.AuthRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

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
        System.out.println("000000(zero):" + md5Hash1);
        System.out.println("admin(admin):" + md5Hash2);
        System.out.println("password(user):" + md5Hash3);
        System.out.println("root(root):" + md5Hash4);
    }

    @Test
    public void WildcardTest() {

    }
}
