package com.example.LocknessAPI.controller;

import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;




@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    // EIP-4361 string
    public static final String MESSAGE = "example.com wants you to sign in with your Ethereum account:\n" +
            "0xAd472fbB6781BbBDfC4Efea378ed428083541748\n\n" +
            "Sign in to use the app.\n\n" +
            "URI: https://example.com\n" +
            "Version: 1\n" +
            "Chain ID: 1\n" +
            "Nonce: EnZ3CLrm6ap78uiNE0MU\n" +
            "Issued At: 2022-06-17T22:29:40.065529400+02:00";

    // Matching signature
    public static final String SIGNATURE = "0x2ce1f57908b3d1cfece352a90cec9beab0452829a0bf741d26016d60676d" +
            "63807b5080b4cc387edbe741203387ef0b8a6e79743f636512cc48c80cbb12ffa8261b";


    @GetMapping("/nonce")
    public ResponseEntity<String> generateNonce(HttpSession session) {
//        String nonce = UUID.randomUUID().toString(); // Tạo nonce ngẫu nhiên
        String nonce = "EnZ3CLrm6ap78uiNE0MU";
        session.setAttribute("nonce", nonce); // Lưu nonce vào session
        return ResponseEntity.ok(nonce);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySignature(@RequestBody VerifyRequest request, HttpSession session) {
        try {
            String storedNonce = (String) session.getAttribute("nonce");
            if (storedNonce == null) {
                return ResponseEntity.badRequest().body("Nonce not found in session.");
            }

            // Parse SIWE message
            SiweMessage siweMessage = new SiweMessage.Parser().parse(MESSAGE);

            // Verify signature
            siweMessage.verify(siweMessage.getDomain(), storedNonce, SIGNATURE);

            // Save session info
            session.setAttribute("siwe", siweMessage.getAddress());
            session.setAttribute("chainId", siweMessage.getChainId());
            session.setAttribute("nonce", null); // Xóa nonce sau khi xác thực

            return ResponseEntity.ok("Signature verified successfully.");

        } catch (SiweException e) {
            return ResponseEntity.status(401).body("Invalid signature: " + e.getMessage());
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(HttpSession session) {
        String address = (String) session.getAttribute("siwe");
        if (address == null) {
            return ResponseEntity.status(401).body("No active session.");
        }
        return ResponseEntity.ok("Authenticated address: " + address);
    }

    static class VerifyRequest {
        private String message;
        private String signature;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    // Đăng xuất (xóa session)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }
}
