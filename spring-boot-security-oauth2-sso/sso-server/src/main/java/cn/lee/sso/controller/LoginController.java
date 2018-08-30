package cn.lee.sso.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author litz-a
 */
@RestController
public class LoginController {

  @GetMapping("/authentication/require")
  public ModelAndView require(@RequestParam(value = "error", required = false) String error) {
    ModelAndView modelAndView = new ModelAndView("thymeleaf/login");
    if (!StringUtils.isEmpty(error)) {
      modelAndView.addObject("email", "用户名或密码错误");
    }
    return modelAndView;
  }
}
