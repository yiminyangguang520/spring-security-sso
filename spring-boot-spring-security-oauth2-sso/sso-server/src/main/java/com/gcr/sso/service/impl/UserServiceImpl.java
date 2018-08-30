package com.gcr.sso.service.impl;

import com.gcr.sso.constant.Constant;
import com.gcr.sso.service.UserService;
import java.io.StringReader;
import java.text.MessageFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.Cleanup;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author litz-a
 */
@Service
public class UserServiceImpl implements UserService {

  private OkHttpClient okClient = new OkHttpClient();

  /**
   * 域账户认证
   */
  @Override
  public String accountAuthenticate(String account, String password) throws Exception {
    return Constant.PASS;
  }
}
