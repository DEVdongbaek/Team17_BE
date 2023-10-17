package com.kakaoseventeen.dogwalking.walk.service;

import com.kakaoseventeen.dogwalking._core.utils.MessageCode;
import com.kakaoseventeen.dogwalking._core.utils.exception.WalkNotExistException;
import com.kakaoseventeen.dogwalking.walk.domain.Walk;
import com.kakaoseventeen.dogwalking.walk.dto.WalkRespDTO;
import com.kakaoseventeen.dogwalking.walk.repository.WalkRepository;
import com.kakaoseventeen.dogwalking.chat.model.ChatRoom;
import com.kakaoseventeen.dogwalking.chat.repository.ChatRoomRepository;
import com.kakaoseventeen.dogwalking.member.domain.Member;
import com.kakaoseventeen.dogwalking.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Walk(산책) 서비스
 *
 * @author 승건 이
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class WalkService {

    private final WalkRepository walkRepository;

    private final MemberJpaRepository memberJpaRepository;

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 산책 허락하기 메서드
     */
    @Transactional
    public void saveWalk(long masterId, long walkerId, Long chatRoomId) throws RuntimeException{
        Optional<Member> master = memberJpaRepository.findById(masterId);
        Optional<Member> walker = memberJpaRepository.findById(walkerId);
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);

        if (chatRoom.isEmpty()) {
            throw new RuntimeException("올바르지 않은 채팅방 Id입니다.");
        }

        if (master.isPresent() && walker.isPresent()) {
            walkRepository.save(Walk.of(master.get(), walker.get(), chatRoom.get()));
        }

        else {
            throw new RuntimeException("올바르지 않은 멤버 Id입니다.");
        }
    }

    /**
     * 산책 시작하기 메서드
     */
    @Transactional
    public WalkRespDTO.StartWalk startWalk(Long chatRoomId) throws WalkNotExistException{
        Walk walk = walkRepository.findWalkByChatRoomId(chatRoomId).orElseThrow(() -> new WalkNotExistException(MessageCode.WALK_NOT_EXIST));
        walk.startWalk();

        return new WalkRespDTO.StartWalk(walk);
    }

    /**
     * 산책 종료하기 메서드
     */
    @Transactional
    public WalkRespDTO.EndWalk terminateWalk(Long chatRoomId) throws WalkNotExistException {
        Walk walk = walkRepository.findWalkByChatRoomId(chatRoomId).orElseThrow(() -> new WalkNotExistException(MessageCode.WALK_NOT_EXIST));;
        walk.endWalk();

        return new WalkRespDTO.EndWalk(walk);

    }

    /**
     * userId를 통한 산책 조회 메서드
     */
    @Transactional(readOnly = true)
    public WalkRespDTO.FindByUserId findAllWalkStatusByUserId(long userId) throws RuntimeException{
        Optional<Member> member = memberJpaRepository.findById(userId);

        if (member.isEmpty()) {
            throw new RuntimeException("올바르지 않은 멤버 Id입니다.");
        }

        List<Walk> walks = walkRepository.findByWalkWithUserIdAndEndStatus(userId);

        return new WalkRespDTO.FindByUserId(walks);
    }
}

