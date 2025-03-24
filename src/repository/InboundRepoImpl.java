package repository;

import config.DBUtil;
import dto.inbound.InboundDTO;
import dto.inbound.ProductDTO;
import vo.inbound.InboundStatusVO;
import vo.inbound.InboundVO;
import vo.inbound.InboundDetailVO;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class InboundRepoImpl implements InboundRepo {
    Connection conn = DBUtil.getConnection();
    PreparedStatement pstmt = null;
    CallableStatement cs = null;
    ResultSet rs = null;

    /**
     * 창고관리자 작업시 필요한 창고ID를 loginUtil에 있는 멤버 ID로 가져온다.
     *
     * @param memberId
     * @return warehouseId
     */
    @Override
    public Optional<Integer> getWarehouseId(int memberId) {
        try {
            String sql = "SELECT warehouseId FROM warehouse WHERE memberNo = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberId);
            rs = pstmt.executeQuery();
            int warehouseId = 0;
            if (rs.next()) {
                warehouseId = rs.getInt(1);
            }
            return Optional.of(warehouseId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 검수 기능]
     * 입고테이블에서 '입고승인' 상태인 행을 가져온다.
     *
     * @return '입고승인 행'
     */
    @Override
    public Optional<List<InboundVO>> findByApprovedStatus(int warehouseId) {
        List<InboundVO> list = new ArrayList<>();
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call ApprovedStatus(?)}");
            cs.setInt(1, warehouseId);
            rs = cs.executeQuery();

            while (rs.next()) {
                InboundVO inboundVO = InboundVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .inboundDate(rs.getDate("inboundDate").toLocalDate())
                        .status(rs.getString("inboundStatus"))
                        .warehouseId(rs.getInt("warehouseId"))
                        .build();
                list.add(inboundVO);
            }
            return list.isEmpty() ? Optional.empty() : Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException("[DB] 오류 발생");
        }
    }

    /**
     * [입고 검수 기능]
     * 입고 ID의 상태 (승인 -> 완료)를 변경
     * 상태가 (승인 -> 완료) 되면 해당 입고 품목 재고에 반영 (트리거)
     *
     * @param inboundId
     */
    @Override
    public boolean updateCompletedStatus(int inboundId) {
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call updateCompletedStatus(?)}");
            cs.setInt(1, inboundId);
            conn.commit();
            //입고완료가 안되면 오류
            return cs.executeUpdate() > 0;
            //DBUtil.closeQuietly(null, cs, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 요청 기능]
     * 제품 테이블의 모든 정보를 가져온다.
     * 프로시저 사용 X
     *
     * @return 창고관리자가 입고를 주문할 때 보는 상품 메뉴
     */
    @Override
    public Optional<List<ProductDTO>> getProductInfo() {
        List<ProductDTO> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Product";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductDTO productDTO = ProductDTO.builder()
                        .productId(rs.getInt("productId"))
                        .productName(rs.getString("productName"))
                        .productPrice(rs.getInt("productPrice"))
                        .categoryId(rs.getString("categoryId"))
                        .storedType(rs.getString("storedType"))
                        .build();
                list.add(productDTO);
            }
            return list.isEmpty() ? Optional.empty() : Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 요청 기능]
     * 입고 요청 입고 상세 테이블에 저장한다.
     *
     * @param inboundList
     */
    @Override
    public void registerInboundDetailInfo(List<InboundDetailVO> inboundList) {
        try {
            conn.setAutoCommit(false);
            inboundList.forEach(inbound -> {
                try {
                    cs = conn.prepareCall("{call register_InboundDetatil_Info(?,?,?,?,?)}");
                    cs.setInt(1, inbound.getInboundDetailId());
                    cs.setInt(2, inbound.getQuantity());
                    cs.setInt(3, inbound.getInboundId());
                    cs.setInt(4, inbound.getProductId());
                    cs.setInt(5, inbound.getSectionId());
                    cs.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            //DBUtil.closeQuietly(null, cs, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 요청 기능]
     * 입고 요청 정보를 입고 테이블에 저장한다.
     * 고지서
     */
    @Override
    public void registerInboundInfo(InboundVO inboundVO) {
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call register_Inbound_Info(?,?,?,?)}");
            cs.setInt(1, inboundVO.getInboundId());
            cs.setDate(2, java.sql.Date.valueOf(inboundVO.getInboundDate()));
            cs.setString(3, inboundVO.getStatus());
            cs.setInt(4, inboundVO.getWarehouseId());

            cs.execute();
            conn.commit();

            //DBUtil.closeQuietly(null, cs, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 수정, 삭제 기능]
     * 수정, 삭제할 입고 ID의 입고 요청을 가져온다.
     *
     * @param warehouseId
     * @return '수정, 삭제 가능한 입고 리스트'
     */
    @Override
    public Optional<List<InboundVO>> getInboundStatus(int warehouseId) {
        List<InboundVO> list = new ArrayList<>();
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call getfixpossible(?)}");
            cs.setInt(1, warehouseId);
            rs = cs.executeQuery();
            while (rs.next()) {
                InboundVO inboundVO = InboundVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .inboundDate(rs.getDate("inboundDate").toLocalDate())
                        .status(rs.getString("inboundStatus"))
                        .warehouseId(rs.getInt("warehouseId"))
                        .build();
                list.add(inboundVO);
            }
            //DBUtil.closeQuietly(rs, cs, conn);
            return list.isEmpty() ? Optional.empty() : Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 수정 기능]
     * 수정 기능 -> 입고 상세 정보를 변경한다.
     * 입고ID, 상품ID, 수량 정보
     *
     * @param inboundList
     */
    @Override
    public void updateInboundInfo(List<InboundDetailVO> inboundList) {
        try {
            conn.setAutoCommit(false);
            inboundList.forEach(inbound -> {
                try {
                    cs = conn.prepareCall("{call update_InboundDetatil_Info(?,?,?,?)}");
                    cs.setInt(1, inbound.getQuantity());
                    cs.setInt(2, inbound.getInboundId());
                    cs.setInt(3, inbound.getProductId());
                    cs.setInt(4, inbound.getInboundDetailId());
                    cs.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            conn.commit();
            //DBUtil.closeQuietly(null, cs, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 취소 기능]
     * 입고 테이블의 해당 ID 행 삭제
     * -> Trigger 통해서 입고상세의 일치하는 입고 ID 부분도 같이 삭제하자
     *
     * @param inboundId
     */
    @Override
    public void deleteInboundInfo(int inboundId) {
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call delete_Inbound_Info(?)}");
            cs.setInt(1, inboundId);

            cs.execute();
            conn.commit();
            //DBUtil.closeQuietly(null, cs, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 입고 수정, 취소가 가능한지 입고예정 날짜를 확인해 반환한다.
     */
    @Override
    public boolean checkInboundDate(int inboundId) {
        try {
            String sql = "SELECT DATEDIFF(inboundDate, now()) FROM Inbound WHERE inboundId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, inboundId);
            rs = pstmt.executeQuery();
            int diffDay = 0;
            if (rs.next()) {
                diffDay = rs.getInt(1);
            }
            if (diffDay <= 2) return false; // 2일 전이면 불가!
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * [입고 고지서 출력]
     * 창고관리자가 자신의 창고 입고 고지서를 출력한다.
     *
     * @param inboundId
     * @return 입고 고지서
     */
    @Override
    public Optional<List<InboundDetailVO>> getInboundDetail(int inboundId) {
        List<InboundDetailVO> list = new ArrayList<>();
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call getInboundDetail(?)}");
            cs.setInt(1, inboundId);
            rs = cs.executeQuery();
            while (rs.next()) {
                InboundDetailVO inboundDetailVO = InboundDetailVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .quantity(rs.getInt("quantity"))
                        .inboundId(rs.getInt("inboundId"))
                        .productId(rs.getInt("productId"))
                        .sectionId(rs.getInt("sectionId"))
                        .build();
                list.add(inboundDetailVO);
            }
            //DBUtil.closeQuietly(rs, cs, conn);
            return list.isEmpty() ? Optional.empty() : Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 창고 관리자 입고 현황 조회
     *
     * @param warehouseId
     * @return
     */
    @Override
    public Optional<List<InboundStatusVO>> getInboundDetailList(int warehouseId) {
        List<InboundStatusVO> list = new ArrayList<>();
        try {
            String sql = "SELECT i.inboundId, d.productId, i.warehouseId, d.sectionId, i.inboundDate, i.inboundStatus, d.quantity\n" +
                    "FROM inbound i, inboundDetail d\n" +
                    "WHERE i.inboundId = d.inboundId and i.warehouseId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, warehouseId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InboundStatusVO inboundStatusVO = InboundStatusVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .productId(rs.getInt("productId"))
                        .warehouseId(rs.getInt("warehouseId"))
                        .sectionId(rs.getInt("sectionId"))
                        .inboundDate(rs.getDate("inboundDate").toLocalDate())
                        .status(rs.getString("inboundStatus"))
                        .quantity(rs.getInt("quantity"))
                        .build();
                list.add(inboundStatusVO);
            }
            //DBUtil.closeQuietly(rs, cs, conn);
            return Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 총관리자(본사)

    /**
     * [입고 요청 승인]
     * (입고요청) 상태인 입고 요청서를 가져온다.
     *
     * @return
     */
    @Override
    public Optional<List<InboundVO>> getInboundRequest() {
        List<InboundVO> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM inbound WHERE inboundStatus = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "입고요청");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InboundVO inboundVO = InboundVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .inboundDate(rs.getDate("inboundDate").toLocalDate())
                        .status(rs.getString("inboundStatus"))
                        .warehouseId(rs.getInt("warehouseId"))
                        .build();
                list.add(inboundVO);
            }
            // DBUtil.closeQuietly(rs, null, conn);
            pstmt.close();
            return Optional.of(list);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 요청 승인]
     * 입고 ID 상태 변경 (요청 -> 승인)
     *
     * @param inboundId
     */
    @Override
    public void updateInboundStatus(int inboundId) {
        try {
            conn.setAutoCommit(false);
            cs = conn.prepareCall("{call updateApprovedStatus(?)}");
            cs.setInt(1, inboundId);
            cs.execute();
            conn.commit();
            cs.close();
            //DBUtil.closeQuietly(null, cs, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [입고 고지서 출력]
     * 총관리자가 볼 모든 입고 고지서
     *
     * @return
     */
    @Override
    public Optional<List<InboundVO>> getAllInboundInfo() {
        List<InboundVO> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM InboundDTO";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InboundVO inboundVO = InboundVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .inboundDate(rs.getDate("inboundDate").toLocalDate())
                        .status(rs.getString("inboundStatus"))
                        .warehouseId(rs.getInt("warehouseId"))
                        .build();
                list.add(inboundVO);
            }
            //DBUtil.closeQuietly(rs, cs, conn);
            return Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 입고 현황 조회
    @Override
    public Optional<List<InboundStatusVO>> getAllInbound() {
        List<InboundStatusVO> list = new ArrayList<>();
        try {
            String sql = "SELECT i.inboundId, d.productId, i.warehouseId, d.sectionId, i.inboundDate, i.inboundStatus, d.quantity\n" +
                    "FROM inbound i, inboundDetail d\n" +
                    "WHERE i.inboundId = d.inboundId";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InboundStatusVO inboundStatusVO = InboundStatusVO.builder()
                        .inboundId(rs.getInt("inboundId"))
                        .productId(rs.getInt("productId"))
                        .warehouseId(rs.getInt("warehouseId"))
                        .sectionId(rs.getInt("sectionId"))
                        .inboundDate(rs.getDate("inboundDate").toLocalDate())
                        .status(rs.getString("inboundStatus"))
                        .quantity(rs.getInt("quantity"))
                        .build();
                list.add(inboundStatusVO);
            }
            //DBUtil.closeQuietly(rs, cs, conn);
            return Optional.of(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<InboundDTO>> getInboundByDate(Date start_date, Date end_date) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getNextInboundId() {
        try {
            String sql = "SELECT MAX(inboundId) FROM inbound";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            int index = 0;
            if (rs.next()) {
                index = rs.getInt(1);
            }

            //DBUtil.closeQuietly(rs, cs, conn);
            return Optional.of(index);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Integer> getStoredType(int productId) {
        try {
            String sql = "SELECT storedType FROM Product WHERE productId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            rs = pstmt.executeQuery();
            String storedType = null;
            if (rs.next()) {
                storedType = rs.getString(1);
            }
            if (storedType.equals("냉장")) return Optional.of(1);
            else if (storedType.equals("냉동")) return Optional.of(2);
            else return Optional.of(3);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
