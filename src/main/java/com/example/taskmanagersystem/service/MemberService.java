package com.example.taskmanagersystem.service;


import com.example.taskmanagersystem.dto.request.MemberRequest;
import com.example.taskmanagersystem.dto.response.MemberResponse;
import com.example.taskmanagersystem.mapper.MemberMapper;
import com.example.taskmanagersystem.model.Member;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private MemberMapper memberMapper;

    private DatabaseReference memberRef;

    @PostConstruct
    public void init() {
        this.memberRef = FirebaseDatabase.getInstance().getReference("members");
    }

    public MemberResponse addMember(MemberRequest request) {
        Member member = memberMapper.toEntity(request);
        member.setId(UUID.randomUUID().toString());
        memberRef.child(member.getId()).setValueAsync(member);
        return memberMapper.toDto(member);
    }

    public List<MemberResponse> getAllMembers() {
        List<MemberResponse> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Member member = child.getValue(Member.class);
                    if (member != null) {
                        list.add(memberMapper.toDto(member));
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return list;
    }
}

