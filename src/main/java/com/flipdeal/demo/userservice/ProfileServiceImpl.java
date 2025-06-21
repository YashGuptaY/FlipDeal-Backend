package com.flipdeal.demo.userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flipdeal.demo.entity.User;
import com.flipdeal.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import static org.springframework.http.HttpStatus.GONE;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final UserRepository userRepostory;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    public void sendWelcomeEmail(String toEmail, String name) {
    	emailService.sendWelcomeEmail(toEmail, name);
    }
    
    
    public void sendResetOtp(String email) {
        User existingEntity = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: "+email));

        //Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        //calculate expiry time (current time + 15 minutes in milliseconds)
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

        //update the profile/user
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expiryTime);

        //save into the database
        userRepostory.save(existingEntity);

        try{
            emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);
        } catch(Exception ex) {
            throw new RuntimeException("Unable to send email");
        }

    }
    
    public void sendOtp(String email) {
        User existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: "+email));

        if (existingUser.isEmailVerified()) { // Since you're using primitive boolean
            return;
        }

        //Generate 6 digit OTP
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        //calculate expiry time (current time + 24 hours in milliseconds)
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        //Update the user entity
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

        //save to database
        userRepostory.save(existingUser);

        try {
            emailService.sendOtpEmail(existingUser.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email");
        }
    }
    
    @Transactional
    public User verifyOtp(UUID encryptedUserId, String otp) {
        final var existingUser = userRepostory.findById(encryptedUserId)
        		.orElseThrow(() -> new ResponseStatusException(GONE, "The user account has been deleted or inactivated"));
        if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setEmailVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);
        return existingUser;
    }
    
    @Transactional
    public void resetPassword(UUID userId, String otp, String newPassword) {
    	final var existingUser = userRepostory.findById(userId).
    			orElseThrow(() -> new ResponseStatusException(GONE, "The user account has been deleted or inactivated"));

        if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepostory.save(existingUser);

    }

}
