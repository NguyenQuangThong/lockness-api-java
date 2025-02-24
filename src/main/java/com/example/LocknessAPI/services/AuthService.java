package com.example.LocknessAPI.services;

import com.example.LocknessAPI.commons.MessageDefine;
import com.example.LocknessAPI.dtos.requests.VerifyRequest;
import com.moonstoneid.siwe.SiweMessage;
import com.moonstoneid.siwe.error.SiweException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public byte verifySignature(VerifyRequest verifyRequest, HttpSession httpSession) {
        try {
            String storedNonce = (String) httpSession.getAttribute("nonce");
            if (storedNonce == null) {
                return MessageDefine.NONCE_NOT_FOUND_IN_SESSION.getCode();
            }

            // Parse SIWE message
            SiweMessage siweMessage = new SiweMessage.Parser().parse(verifyRequest.getMessage());

            // Verify signature
            siweMessage.verify(siweMessage.getDomain(), storedNonce, verifyRequest.getSignature());

            // Save session info
            httpSession.setAttribute("siwe", siweMessage.getAddress());
            httpSession.setAttribute("chainId", siweMessage.getChainId());
            httpSession.setAttribute("nonce", null); // Xóa nonce sau khi xác thực

            return MessageDefine.SIGNATURE_VERIFIED_SUCCESSFULLY.getCode();

        } catch (SiweException e) {
            return MessageDefine.INVALID_SIGNATURE.getCode();
        }
    }
}
