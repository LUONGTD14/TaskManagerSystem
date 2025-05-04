package com.example.taskmanagersystem.controller;

import com.example.taskmanagersystem.dto.request.MemberRequest;
import com.example.taskmanagersystem.dto.response.MemberResponse;
import com.example.taskmanagersystem.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(@RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.addMember(request));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }
}

