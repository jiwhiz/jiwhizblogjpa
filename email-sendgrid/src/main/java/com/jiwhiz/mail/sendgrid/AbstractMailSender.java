/* 
 * Copyright 2013-2014 JIWHIZ Consulting Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiwhiz.mail.sendgrid;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

/**
 * @author Yuan Ji
 */
@Slf4j
public class AbstractMailSender {
    
    @Getter @Setter
    private String sendGridUsername;
    
    @Getter @Setter
    private String sendGridPassword;

    @Getter @Setter
    private String adminName;
    
    @Getter @Setter
    private String adminEmail;
    
    @Getter @Setter
    private String systemName;
    
    @Getter @Setter
    private String systemEmail;
    
    @Getter @Setter
    private String applicationBaseUrl;

    protected void doSend(String fromEmail, String toEmail, String subject, String message) {
        SendGrid sendgrid = new SendGrid(sendGridUsername, sendGridPassword);
        SendGrid.Email email = new SendGrid.Email();
        email.addTo(toEmail);
        email.setFrom(fromEmail);
        email.setSubject(subject);
        email.setText(message);

        try {
            SendGrid.Response response = sendgrid.send(email);
            log.debug("Sent email, response from SendGrid is "+response.getMessage());
          }
          catch (SendGridException e) {
            log.warn("Got error from SendGrid "+e.getMessage());
          }
    }
}
