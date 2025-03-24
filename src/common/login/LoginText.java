package common.login;

/*
    사용자에게 출력할 모든 text를 미리 정의하는 enum 클래스입니다.
 */
public enum LoginText {
    MENU_HEADER("\n====================================\n[로그인 시스템]\n===================================="),

    /*LOGIN */
    LOGIN_HEADER("===로그인==="),
    LOGIN_NO("아이디 : "),
    LOGIN_PASSWORD("비밀번호 : "),

    /*Login status*/
    LOGIN_SUCCESS("로그인 완료"),
    LOGIN_APPROVE_WAITING("회원 가입 승인대기 중 "),
    LOGOUT_SUCCESS("로그아웃 완료"),

    /*회원가입*/
    REQUEST_HEADER("=== 회원 가입 ==="),
    REQUEST("등록할 "),
    REQUEST_SUCCESS("회원 가입 신청성공"),

    /*아이디 찾기 */
    SEARCH_ID_HEADER("=== 아이디 찾기 ==="),
    SEARCH_email("검색할 회원의 email : "),
    FIND_ID("찾은 회원 아이디 : "),

    /*인증번호*/
    RANDOM_NUM("인증번호 : "),
    RANDOM_NUM_CHECK("인증번호를 입력해주세요 :  "),
    RANDOM_NUM_CHECK_S("인증번호가 일치합니다."),

    /*비밀번호 찾기 */
    SEARCH_P_HEADER("=== 비밀번호 찾기 ==="),
    SEARCH_ID("검색할 회원의 아이디 : "),
    FIND_P("찾은 회원의 비밀번호 : "),


    /** 메인메뉴 */
    LOGIN_MAINMENU(
            "1. 로그인\n" +
            "2. 회원가입 요청\n"+
            "3. 아이디찾기\n" +
            "4. 비밀번호찾기\n" +
            "5. 로그아웃\n" +
                    "\n메뉴를 선택하세요(\"exit\"입력 시 종료): "),


    HQ_MAINMENU("\n" +
            "===본사관리자 메뉴===\n" +
            "1. 회원관리\n" +
            "2. 창고관리\n" +
            "3. 재고관리\n" +
            "4. 가맹점 관리\n" +
            "5. 출고관리\n" +
            "6. 입고관리\n" +
            "7. 로그아웃\n"),


    WM_MAINMENU("\n" +
            "=== 창고관리자 메뉴===\n" +
            "1. 회원관리\n" +
            "2. 창고관리\n" +
            "3. 재고관리\n" +
            "4. 출고관리\n" +
            "5. 입고관리\n" +
            "6. 로그아웃\n"),


    FM_MAINMENU("\n" +
            "===가맹점주 메뉴===\n" +
            "1. 회원관리\n" +
            "2. 발주관리\n" +
            "3. 로그아웃\n"),



    ;


    private final String text;

    LoginText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
