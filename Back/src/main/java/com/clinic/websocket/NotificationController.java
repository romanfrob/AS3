package com.clinic.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class NotificationController
{
	@SendTo("/api/ws/notification")
	@MessageMapping("/notification")
	public String processDoctorNotif(String doctor)
	{
		return "w";
	}
}
