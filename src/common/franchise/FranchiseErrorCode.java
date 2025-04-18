package common.franchise;

/*
    예외 처리에 사용되는 에러 발생 text를 미리 정의하는 enum 클래스입니다.
 */
public enum FranchiseErrorCode {
    DB_ERROR("[DB]: "),
    DB_PROCEDURE_ERROR("procedure를 실행할 수 없습니다."),

    NUMBER_NOT_FOUND("[Service]: 가맹점 번호를 찾을 수 없습니다"),

    INPUT_ERROR("잘못된 입력입니다.");

    private final String text;

    FranchiseErrorCode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
