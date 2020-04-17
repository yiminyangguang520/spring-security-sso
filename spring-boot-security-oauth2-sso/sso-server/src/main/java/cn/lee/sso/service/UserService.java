package cn.lee.sso.service;

/**
 * @author min
 */
public interface UserService {

  /**
   * 账户认证
   * @param account
   * @param password
   * @return
   * @throws Exception
   */
  String acountAuthenticate(String account, String password) throws Exception;
}
