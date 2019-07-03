package com.example.kaBank.services.Controller.pub;

import com.example.kaBank.services.DTO.AccountDTO;
import com.example.kaBank.services.Repository.AccountRepository;
import com.example.kaBank.util.BCrypt;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/pub")
public class LoginController {
    private AccountRepository accountRepository;

    @GetMapping("/login")
    public String login(){
        return "pub/login";
    }

    @PostMapping("/loginProcess")
    public ModelAndView loginProcess(HttpServletRequest request, HttpSession session){


        String returnUrl = "/pub/login";
        ModelAndView mav = new ModelAndView();

        mav.setViewName(returnUrl);
        if ( session.getAttribute("userInfo") != null ){
            // 기존에 userInfo란 세션 값이 존재한다면
            session.removeAttribute("userInfo"); // 기존값을 제거해 준다.
        }
        //login form : ID, password
        String loginId = request.getParameter("id");
        String loginPwd = request.getParameter("password");

        if(loginId == null || "".equals(loginId)) return mav.addObject("msg", "failure");
        if(loginPwd == null || "".equals(loginPwd)) return mav.addObject("msg", "failure");

        //userModel : ID, password
        String userId = "";
        String userPwd = "";

        // 로그인 시도한 user 정보
        AccountDTO user = accountRepository.findById(loginId).orElse(null);
        // 로그인 시도한 user 정보가 있다면 id, password 정보 가져옴
        if(user!=null){
            userId = user.getId();
            userPwd = user.getPasswrod();
        }
        if(userId == null || "".equals(userId)) {
            return mav.addObject("msg", "failure");
        }
        if(loginPwd == null || "".equals(loginPwd)) {
            return mav.addObject("msg", "failure");
        }

        System.out.println("check "+loginPwd);
        // id와 password 비교
        if(loginId.equals(userId) && BCrypt.checkpw(loginPwd,userPwd)){

            // session에 포함될 user Model, menu Api list
            Map<String, Object> userInfo = new HashMap<String, Object>();

            returnUrl = "redirect:/admin/main";
            session.setMaxInactiveInterval(60*60);

            userInfo.put("userModel", user);

            request.getSession().setAttribute("userInfo", userInfo);

        }else{
            mav.addObject("msg", "failure");
        }

        mav.setViewName(returnUrl);
        return mav;
    }
}
