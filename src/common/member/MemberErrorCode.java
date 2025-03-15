package common.member;

/*
    예외 처리에 사용되는 에러 발생 text를 미리 정의하는 enum 클래스입니다.
 */
public enum MemberErrorCode {
    DB_INSERT_ERROR("[DB]: 정보를 저장할 수 없습니다."),

    NUMBER_NOT_FOUND("[Service]: 직원 번호를 찾을 수 없습니다");

    private final String text;

    MemberErrorCode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
