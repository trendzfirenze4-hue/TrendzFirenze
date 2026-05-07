package com.mydev.ecommerce.auth.controller;

import com.mydev.ecommerce.auth.dto.ForgotPasswordRequest;
import com.mydev.ecommerce.auth.dto.MessageResponse;
import com.mydev.ecommerce.auth.dto.ResetPasswordRequest;
import com.mydev.ecommerce.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.requestReset(request.getEmail());

        /*
         * Always return the same message.
         * This prevents attackers from checking which emails are registered.
         */
        return ResponseEntity.ok(
                new MessageResponse("If an account exists with this email, a password reset link has been sent.")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                new MessageResponse("Password reset successful. You can now login with your new password.")
        );
    }
}