package com.reservemate.reserve_mate_backend.common.sms.util;

import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    private final DefaultMessageService defaultMessageService;
    private final String senderPhoneNumber;

    public SmsUtil(DefaultMessageService defaultMessageService,
        @Value("${spring.coolsms.phone.number}") String senderPhoneNumber) {
        this.defaultMessageService = defaultMessageService;
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public SingleMessageSentResponse sendOne(String to, String authCode) {
        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(to);
        message.setText(String.format("[ReserveMate] 인증번호 : %s를 입력해주세요", authCode));

        SingleMessageSentResponse response = this.defaultMessageService.sendOne(new SingleMessageSendingRequest(
            message));

        System.out.println(message);

        return response;
    }

}
