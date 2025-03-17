package common.warehouse;

/*
    사용자에게 출력할 모든 text를 미리 정의하는 enum 클래스입니다.
 */
public enum WarehouseText {
    /** 창고관리 헤더 */
    MENU_HEADER("\n" +
            "====================================\n" +
            "[창고 관리 시스템]\n" +
            "===================================="),

    /** 본사-창고관리 메인메뉴 */
    HQ_WAREHOUSE_MENU("\n[본사 관리자 > 창고 관리]\n" +
            "1. 창고 등록\n" +
            "2. 창고 수정\n" +
            "3. 창고 삭제\n" +
            "4. 창고 조회\n" +
            "5. 뒤로 가기\n"),

    /** 본사-창고관리 > 창고조회 서브메뉴 */
    HQ_WAREHOUSE_VIEW_MENU("\n[본사 관리자 > 창고 관리 > 창고 조회]\n" +
            "1. 전체 창고 조회\n" +
            "2. 소재지별 창고 조회\n" +
            "3. 뒤로 가기\n"),

    /** 본사-창고관리 > 창고조회 > 소재지별 창고 조회 서브메뉴 */
    HQ_WAREHOUSE_VIEW_LOCATION_MENU("\n[본사 관리자 > 창고 관리 > 창고 조회 > 소재지별 창고 조회]\n" +
            "1. 수도권\n" +
            "2. 비수도권\n" +
            "3. 뒤로가기\n"),

    /** 창고-창고관리 메인메뉴 */
    WAREHOUSE_MANAGE_MENU("\n[창고-창고관리]\n" +
              "1. 정보 조회\n" +
              "2. 재고 조회\n" +
              "3. 뒤로 가기\n"),

    /// ////////////////////////////////////////////////////
    /// ////////////////////////////////////////////////////
    /// ////////////////////////////////////////////////////

    // 테이블 조회시 헤더 등


    ;

    private final String text;

    WarehouseText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
