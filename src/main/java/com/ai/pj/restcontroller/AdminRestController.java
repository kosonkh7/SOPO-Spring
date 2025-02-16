package com.ai.pj.restcontroller;


import com.ai.pj.domain.VisitCount;
import com.ai.pj.dto.ResponseDTO;
import com.ai.pj.service.AdminService;
import com.ai.pj.service.VisitCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminService adminService;
    private final VisitCounterService visitCounterService;

    @GetMapping("/{id}/prove")
    public ResponseDTO<?> reqProve(@PathVariable String id) {

        // 업데이트 해줘야댐.
//        int updateRows = adminService.updateRole(id);

        // 나중에 수정... 귀찮음
        adminService.updateRole(id);

        // 없
        return new ResponseDTO<>(1, HttpStatus.OK);
    }

    @GetMapping("/{id}/reject")
    public ResponseDTO<?> reqReject(@PathVariable String id) {

        adminService.deleteByRole(id);

        return new ResponseDTO<>(1, HttpStatus.OK);
    }


    @GetMapping("/api/visitor-data")
    public List<VisitCount> reqVisitorData() {
        return visitCounterService.visitCount();
    }
}
