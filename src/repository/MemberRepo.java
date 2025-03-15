package repository;

import dto.MemberDTO;

import java.util.List;
import java.util.Optional;

public interface MemberRepo {

    /*
     * [회원 등록 기능]
     * 본사관리자가 신규 창고관리자를 등록
     */
     MemberDTO addMember(MemberDTO member);

    /*
     * [회원 수정 기능]
     * 본사관리자는 회원의 정보를 수정할 수 있다
     * 가맹점주는 본인 정보 수정
     * 수정하려는 회원이 없을 경우  Optional 처리
     */
    Optional<MemberDTO> updateMember(String memberNo, MemberDTO updatemember);


    /*
     * [회원 삭제 기능]
     * 본사관리자는 아이디로 회원정보를 삭제
     * 가맹점주는 탈퇴
     * 존재하지 않는 아이디의 경우  Optional 처리
     * 삭제된 멤버의 간단한 정보(아이디,이름 등) 리턴
     */
    Optional<MemberDTO> deleteMember(String memberNo);


    /*
     * [회원 가입 기능]
     * 가맹점주의 회원가입 요청을 요청테이블에 저장
     * 요청상태 :승인대기
     * 승인 실패의 경우 : 이미 존재하는 아이디의 경우 예외처리
     */
    boolean requestMember(MemberDTO member);

    /*
     * [회원 승인 기능]
     * 본사관리자는 가맹점주의 가입상태를 승인으로 변경
     * 존재하지 않는 아이디의 경우  Optional 처리
     * 트리거를 이용해 승인된 회원을 회원테이블에 추가 / 요청테이블에서 삭제
     */
    Optional<MemberDTO> approvalMember(String memberNo);


    /*
     * [회원 간편/상세/권한별 조회 기능]
     * 총관리자는 회원아이디와 권한을 기준으로 조회할 수 있다.
     * 창고관리자는 회원아이디로 조회할 수 있다.
     * 가맹점주는 본인 아이디로 조회할 수 있다.
     *      ->현재는 조회 기준이 전부 String이라 t가 필요없긴함
     * 회원이 존재하지 않을 경우  Optional 처리
     */
    <T> Optional<MemberDTO> loadMember(T serchValue);

    /*
     * [전체 회원 조회 기능]
     * 저장된 전체 회원의 정보를 조회
     * 회원이 존재하지 않아 리스트가 null값일 경우 Optional처리
     */
    Optional<List<MemberDTO>> allLoadMember();


    /*
     * [아이디 / 비밀번호 찾기 기능]
     * 이메일로 아이디를 찾을 수 있다.
     * 아이디로 비밀번호를 찾을 수 있다.
     * 회원이 존재하지 않을 경우  Optional 처리
     */
    Optional<String>  searchLoginfo(String searchvalue);


}
