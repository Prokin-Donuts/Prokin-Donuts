package service;


import common.inbound.InboundErrorCode;
import dto.inbound.InboundDTO;

import dto.inbound.ProductDTO;
import repository.InboundRepo;
import vo.inbound.InboundDetailVO;
import vo.inbound.InboundStatusVO;
import vo.inbound.InboundVO;

import java.util.Date;
import java.util.List;

public class InboundServiceImpl implements InboundService {
    private final InboundRepo inboundRepo;

    public InboundServiceImpl(InboundRepo inboundRepo) {
        this.inboundRepo = inboundRepo;
    }

    //창고 관리자

    /**
     * 창고관리자 작업시 필요한 창고ID를 loginUtil에 있는 멤버 ID로 가져온다.
     * @param memberId
     * @return warehouseId
     */
    @Override
    public int getWarehouseId(int memberId) {
        return inboundRepo.getWarehouseId(memberId).orElseThrow(
                () -> new IllegalArgumentException("ee")
        );
    }

    /**
     * [입고 검수 기능]
     * 입고(승인) 상태의 입고테이블 정보를 반환
     * @return 입고(승인) 리스트
     */
    @Override
    public List<InboundVO> getApprovalInboundList(int warehouseId) throws IllegalArgumentException{
        return inboundRepo.findByApprovedStatus(warehouseId).orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_LIST.getText())
        );
    }

    /**
     * 해당 입고 ID 상태 변경 (승인 -> 완료)
     * @param inboundId 입고 아이디
     */
    @Override
    public boolean completedInbound(int inboundId) {
        return inboundRepo.updateCompletedStatus(inboundId);
    }

    /**
     * 입고 요청시 필요한 상품 메뉴 반환
     * @return 상품 리스트
     */
    @Override
    public List<ProductDTO> getProductMenu() throws IllegalArgumentException{
        return inboundRepo.getProductInfo().orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_PRODUCT_LIST.getText())
        );
    }

    /**
     * 입고 요청 등록
     * @param inboundVO
     */
    @Override
    public void registerInbound(InboundVO inboundVO) {
        inboundRepo.registerInboundInfo(inboundVO);
    }

    /**
     * 입고 요청 상세 등록
     * @param inboundList
     */
    @Override
    public void registerDetailInfo(List<InboundDetailVO> inboundList) {
        inboundRepo.registerInboundDetailInfo(inboundList);
    }

    /**
     * 상품 보관 타입 반환
     * @param productId
     * @return
     */
    @Override
    public int getStoredType(int productId) throws IllegalArgumentException{
        return inboundRepo.getStoredType(productId).orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_TYPE.getText())
        );
    }

    /**
     * [입고 요청 수정]
     * 입고 요청 리스트 출력 (요청, 승인) 상태
     *
     * @return 입고 요청 리스트
     */
    @Override
    public List<InboundVO> getInboundList(int warehouseId) throws IllegalArgumentException{
        return inboundRepo.getInboundStatus(warehouseId).orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_DELETE_LIST.getText())
        );
    }

    /**
     * [입고 요청 수정]
     * 입고 요청 수정 ID 입력, 변경 데이터 update
     * -->>>>>>>> 수정 사항 존재
     * 매개변수 값 추후 수정
     * @param inboundId
     * @param inboundList
     */
    @Override
    public void updateInboundInfo(int inboundId, List<InboundDetailVO> inboundList) {
        inboundRepo.updateInboundInfo(inboundList);
    }

    /**
     * 입고 예정 날짜 확인 후 수정 및 취소 가능 여부 판단
     * 입고ID -> 입고 상세 테이블의 예정입고일 확인
     * @param inboundId
     * @return
     */
    @Override
    public boolean checkInboundDate(int inboundId) {
        return inboundRepo.checkInboundDate(inboundId);
    }

    /**
     * 입고ID 입력시 입고 요청 취소
     * @param inboundId
     */
    @Override
    public void deleteInboundInfo(int inboundId) {
        inboundRepo.deleteInboundInfo(inboundId);
    }

    /**
     * 창고관리자 현황 조회
     * 입고 ID 입력시 입고 상세 정보 출력
     * 창고 ID 필요
     * @param warehouseId
     * @return 입고상세정보
     */
    @Override
    public List<InboundStatusVO> getInboundDetail(int warehouseId) throws IllegalArgumentException{
        return inboundRepo.getInboundDetailList(warehouseId).orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_RECEIPT.getText())
        );
    }



    @Override
    public List<InboundDTO> getDateInboundInfo(Date start_date, Date end_date) {
        return null;
    }

    // 총관리자(본사)

    /**
     * (입고요청) 상태인 입고요청서를 가져온다.
     *
     * @return
     */
    @Override
    public List<InboundVO> getInboundRequest() throws IllegalArgumentException{
        return inboundRepo.getInboundRequest().orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_HQ_LIST.getText())
        );
    }

    /**
     * 입고 요청 승인
     * 입고상태 (요청 -> 승인) 변경
     * @param inbound
     */
    @Override
    public void updateInboundStatus(int inbound) {
        inboundRepo.updateInboundStatus(inbound);
    }

    /**
     * 총관리자 고지서 출력
     * @return 모든 창고의 입고 고지서
     */
    @Override
    public List<InboundVO> getAllInboundInfo() throws IllegalArgumentException{
        return inboundRepo.getAllInboundInfo().orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_RECEIPT.getText())
        );
     }

    @Override
    public List<InboundStatusVO> getAllInbound() {
        return inboundRepo.getAllInbound().orElseThrow(
                () -> new IllegalArgumentException("존재X")
        );
    }

    @Override
    public List<InboundDTO> getInboundByDate(Date start_date, Date end_date) {
        return null;
    }

    @Override
    public int getNextInboundId() throws IllegalArgumentException{
        return inboundRepo.getNextInboundId().orElseThrow(
                () -> new IllegalArgumentException(InboundErrorCode.NOT_FOUND_NEXT_NUMBER.getText())
        );
    }


}
