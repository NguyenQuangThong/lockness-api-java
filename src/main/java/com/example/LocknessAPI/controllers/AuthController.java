package com.example.LocknessAPI.controllers;

import com.example.LocknessAPI.commons.MessageDefine;
import com.example.LocknessAPI.dtos.requests.VerifyRequest;
import com.example.LocknessAPI.services.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;




@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // **TEST DATA** EIP-4361 string
    public static final String MESSAGE = "example.com wants you to sign in with your Ethereum account:\n" +
            "0xAd472fbB6781BbBDfC4Efea378ed428083541748\n\n" +
            "Sign in to use the app.\n\n" +
            "URI: https://example.com\n" +
            "Version: 1\n" +
            "Chain ID: 1\n" +
            "Nonce: EnZ3CLrm6ap78uiNE0MU\n" +
            "Issued At: 2022-06-17T22:29:40.065529400+02:00";

    // **TEST DATA** Matching signature
    public static final String SIGNATURE = "0x2ce1f57908b3d1cfece352a90cec9beab0452829a0bf741d26016d60676d" +
            "63807b5080b4cc387edbe741203387ef0b8a6e79743f636512cc48c80cbb12ffa8261b";

    @GetMapping("/nonce")
    public ResponseEntity<String> generateNonce(HttpSession session) {
        String nonce = UUID.randomUUID().toString(); // Tạo nonce ngẫu nhiên
        session.setAttribute("nonce", nonce); // Lưu nonce vào session
        return ResponseEntity.ok(nonce);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySignature(@RequestBody VerifyRequest request, HttpSession session) {
        short code = authService.verifySignature(request, session);
        if (code == MessageDefine.NONCE_NOT_FOUND_IN_SESSION.getCode()) {
            return ResponseEntity.badRequest().body(MessageDefine.NONCE_NOT_FOUND_IN_SESSION.getMessage());
        }

        if (code == MessageDefine.INVALID_SIGNATURE.getCode()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(MessageDefine.INVALID_SIGNATURE.getMessage());
        }

        return ResponseEntity.ok(MessageDefine.SIGNATURE_VERIFIED_SUCCESSFULLY.getMessage());
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(HttpSession session) {
        String address = (String) session.getAttribute("siwe");
        if (address == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(MessageDefine.NO_ACTIVE_SESSION.getMessage());
        }
        return ResponseEntity.ok("Authenticated address: " + address);
    }

    // Đăng xuất (xóa session)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(MessageDefine.LOGGED_OUT_SUCCESSFULLY.getMessage());
    }
}
