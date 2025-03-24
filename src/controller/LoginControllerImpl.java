package controller;

import common.login.LoginErrorCode;
import common.login.LoginText;
import common.member.MemberText;
import common.util.InputUtil;
import common.util.LoginUtil;
import dto.memberDTO.MemberDTO;
import dto.memberDTO.MemberRequestDTO;
import repository.MemberRepoImpl;
import service.MemberService;
import service.MemberServiceImpl;

import java.util.HashMap;
import java.util.Map;

public class LoginControllerImpl implements LoginController{
    MemberService memberService;
    private boolean loginFlag = false;

    public LoginControllerImpl(MemberService memberService) {
        this.memberService = memberService;
    }

    Map<Integer,Runnable> mainMenu = new HashMap<>();

    public void loginPlay(){
        mainMenu = setMainMenu();
        while(true) {
            System.out.println(LoginText.MENU_HEADER.getText());
            Runnable action = mainMenu.get(
                    InputUtil.getIntegerInput(LoginText.LOGIN_MAINMENU.getText()));
            action.run();
            if (this.loginFlag) {return;}
        }
    }
    public Map<Integer,Runnable> setMainMenu(){
        mainMenu.put(1,()->login());
        mainMenu.put(2,()->memberRequest());
        mainMenu.put(3,()->findId());
        mainMenu.put(4,()->findPassword());
        mainMenu.put(5,()->logout());
        return mainMenu;
    }

    public void login() {
        System.out.println(LoginText.LOGIN_HEADER.getText());

        String id = InputUtil.getInput(LoginText.LOGIN_NO.getText()).get();
        String password = InputUtil.getInput(LoginText.LOGIN_PASSWORD.getText()).get();
        String requst = memberService.searchRequestMember(id);
        if (requst !=null && requst.equals("대기")) {
            System.out.println(LoginErrorCode.LOGIN_FAIL_REQUEST.getText());
        } else {
            String logstatus = memberService.logstatus(id);
            if (logstatus.equals("login")) System.out.println(LoginErrorCode.LOGIN_FAIL.getText()); //로그인 상태 확인
            else {
                MemberDTO loginMember = memberService.searchMember(id);

                if (loginMember == null) System.out.println(LoginErrorCode.LOGIN_NOT_FOUND.getText());
                else {
                    String result = memberService.logIn(id, password);
                    LoginUtil.setLoginMember(loginMember);
                    System.out.println(LoginText.LOGIN_SUCCESS.getText());
                    this.loginFlag = true;
                }
            }
        }
    }

    public void memberRequest(){
        System.out.println(LoginText.REQUEST_HEADER.getText());
        MemberRequestDTO memberRequest = newMemberRequest();
        if(!memberService.checkId(memberRequest.getId())) { //아이디 중복확인
            MemberRequestDTO result = memberService.requestMember(memberRequest);
            if(result == null) System.out.println(LoginErrorCode.REQUEST_NOT_FOUND.getText());
            else System.out.println(LoginText.REQUEST_SUCCESS.getText());
        }else System.out.println(LoginErrorCode.REQUEST_FAIL.getText());
    }

    public void findId(){
        System.out.println(LoginText.SEARCH_ID_HEADER.getText());
        String email = InputUtil.getInput(LoginText.SEARCH_email.getText()).get();

        String code = memberService.randomNumber(email);
        System.out.println(LoginText.RANDOM_NUM.getText()+code);

        String userCode = InputUtil.getInput(LoginText.RANDOM_NUM_CHECK.getText()).get();

        if(memberService.checkRandomNumber(email,userCode)) {
            System.out.println(LoginText.RANDOM_NUM_CHECK_S.getText());
            String result = memberService.findId(email);
            System.out.println(LoginText.FIND_ID.getText()+result);
        }
        else System.out.println(LoginErrorCode.RANDOM_NUM_CHECK_F.getText());
    }
    public void findPassword(){
        System.out.println(LoginText.SEARCH_P_HEADER.getText());
        String id = InputUtil.getInput(LoginText.SEARCH_ID.getText()).get();
        String email = InputUtil.getInput(LoginText.SEARCH_email.getText()).get();

        String userEmail = memberService.findemail(id);

        String code = memberService.randomNumber(userEmail);
        System.out.println(LoginText.RANDOM_NUM.getText()+code);

        String userCode = InputUtil.getInput(LoginText.RANDOM_NUM_CHECK.getText()).get();

        if(memberService.checkRandomNumber(userEmail,userCode)
                && userEmail.equals(email)
        ){
            System.out.println(LoginText.RANDOM_NUM_CHECK_S.getText());
            String result = memberService.findPassword(id);
            System.out.println(LoginText.FIND_P.getText()+result);
        }
        else System.out.println(LoginErrorCode.RANDOM_NUM_CHECK_F.getText());
    }


    public void logout(){
        String id = LoginUtil.getLoginMember().getId();
        String logstatus = memberService.logstatus(id); //로그인 상태 확인
        if (logstatus.equals("logout"))
            System.out.println(LoginErrorCode.LOGIN_FAIL_OUT.getText());
        else {
            String result = memberService.logOut(id);
            if(result == null) System.out.println(LoginErrorCode.LOGOUT_FAIL.getText());
            else {
                System.out.println(LoginText.LOGOUT_SUCCESS.getText());
                LoginUtil.setLoginMember(null);
            }
        }
    }


    public MemberRequestDTO newMemberRequest(){
        MemberRequestDTO newMemberRequest = new MemberRequestDTO();
        newMemberRequest.setName(InputUtil.getInput(LoginText.REQUEST.getText()+MemberText.MEMBER_NAME.getText()).get());
        newMemberRequest.setPhoneNumber(InputUtil.getInput(LoginText.REQUEST.getText()+MemberText.MEMBER_PHONE.getText()).get());
        newMemberRequest.setEmail(InputUtil.getInput(LoginText.REQUEST.getText()+MemberText.MEMBER_EMAIL.getText()).get());
        newMemberRequest.setAddress(InputUtil.getInput(LoginText.REQUEST.getText()+MemberText.MEMBER_ADDRESS.getText()).get());
        newMemberRequest.setId(InputUtil.getInput(LoginText.REQUEST.getText()+MemberText.MEMBER_ID.getText()).get());
        newMemberRequest.setPassword(InputUtil.getInput(LoginText.REQUEST.getText()+MemberText.MEMBER_PASSWORD.getText()).get());
        return newMemberRequest;
    }
}